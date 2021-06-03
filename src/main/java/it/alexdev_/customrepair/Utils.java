package it.alexdev_.customrepair;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Utils {

    private final CustomRepair cr;
    private final FileManager fileManager;
    private final TokenEnchantAPI te = TokenEnchantAPI.getInstance();
    private final HashMap<Player, Boolean> mappa = new HashMap<>();
    private final String prefix;


    public Utils(CustomRepair cr) {
        this.cr = cr;
        fileManager = new FileManager(cr);
        prefix = ChatColor.translateAlternateColorCodes('&', cr.getConfig().getString("Prefix"));
    }


    public boolean isRepairSign(Block block) {
        for (Block blocks : fileManager.getBlocks()) {
            if (blocks.equals(block)) {
                return true;
            }
        }
        return false;
    }


    public int getTimes(Player player) {
        if (cr.getConfig().getString("Players." + player.getUniqueId()) != null) {
            return cr.getConfig().getInt("Players." + player.getUniqueId() + ".num");
        }
        cr.getConfig().getConfigurationSection("Players").createSection(player.getUniqueId().toString());
        cr.getConfig().set("Players." + player.getUniqueId() + ".nickname", player.getName());
        cr.getConfig().set("Players." + player.getUniqueId() + ".num", 1);
        return 1;
    }

    public void addTimes(Player player) {
        setTimes(player, getTimes(player) + 1);
    }

    public void setTimes(Player player, int num) {
        if (cr.getConfig().getString("Players." + player.getUniqueId().toString()) != null) {
            cr.getConfig().set("Players." + player.getUniqueId() + ".num", num);
            fileManager.save();
            return;
        }
        cr.getConfig().getConfigurationSection("Players").createSection(player.getUniqueId().toString());
        cr.getConfig().set("Players." + player.getUniqueId() + ".nickname", player.getName());
        cr.getConfig().set("Players." + player.getUniqueId() + ".num", num);
        fileManager.save();
    }

    public long fixCost(Player player) {
        long repair;
        if (player.hasPermission("sign.vip.ultimate")) {
            repair = cr.getConfig().getLong("Fix-Cost-Base-Vip-Ultimate") * getTimes(player);
        }
        else if (player.hasPermission("sign.vip.elite")) {
            repair = cr.getConfig().getLong("Fix-Cost-Base-Vip-Elite") * getTimes(player);
        } else  {
            repair = cr.getConfig().getLong("Fix-Cost-Base") * getTimes(player);
        }

        return repair;
    }

    public void removeTokens(Player player) {
        te.removeTokens(Bukkit.getOfflinePlayer(player.getUniqueId()), fixCost(player));
    }

    public boolean hasTokens(Player player) {
        if (te.getTokens(Bukkit.getOfflinePlayer(player.getUniqueId())) >= fixCost(player)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public void fix(Player player) {
        ItemStack item = player.getItemInHand();
        item.setDurability((short) 0);
    }

    public void repair(Player player) {
        if (!mappa.containsKey(player)) {
            if (isTool(player.getInventory().getItemInMainHand())) {
                if (player.getItemInHand().getDurability() > 0) {
                    if (cr.getMap().containsKey(player) && cr.getMap().get(player).equals(player.getItemInHand())) {
                        if (hasTokens(player)) {
                            removeTokens(player);
                            long cost = fixCost(player);
                            fix(player);
                            player.sendMessage(prefix + "You fixed your item for §e" + cost + " tokens");
                            cr.getMap().remove(player);
                            addTimes(player);
                            cr.saveDefaultConfig();
                        } else {
                            player.sendMessage(prefix + "You don't have enough tokens to repair your tool. You need di §e" + fixCost(player) + " tokens§c.");
                            cr.getMap().remove(player);
                        }
                    } else {
                        schedule(10, player);
                        player.sendMessage(prefix + "The fix cost is §e" + fixCost(player) + " tokens§7. Click again in 10 seconds to confirm the process.");
                        cr.getMap().put(player, player.getItemInHand());
                    }
                } else {
                    player.sendMessage(prefix + "Your tool is already fixed");
                }

            } else {
                player.sendMessage(prefix + "You don't have any tool in your hand");
            }
        } else {
            mappa.put(player, true);
            schedule2(player);

        }
    }

    private boolean isTool(ItemStack itemStack){
        if(itemStack==null) return false;
        String name = itemStack.getType().name();
        if(name.contains("PICKAXE")) return true;
        else if(name.contains("SWORD")) return true;
        else if(name.contains("SHOVEL")) return true;
        else if(name.contains("HOE")) return true;
        else if(itemStack.getType().equals(Material.SHEARS)) return true;
        else if(itemStack.getType().equals(Material.FLINT_AND_STEEL)) return true;
        else return false;
    }

    public void schedule(long delaySeconds, Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(cr,
                () -> mappa.remove(player), delaySeconds * 20);
    }

    public void schedule2(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(cr,
                () -> mappa.remove(player), 5);
    }




}
