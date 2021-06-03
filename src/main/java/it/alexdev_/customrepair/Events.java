package it.alexdev_.customrepair;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Events implements Listener {

    private final FileManager fileManager;
    private final Utils utils;
    private final String prefix;



    public Events(CustomRepair cr){
        fileManager = new FileManager(cr);
        utils = new Utils(cr);
        prefix = ChatColor.translateAlternateColorCodes('&', cr.getConfig().getString("Prefix"));
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignClick(PlayerInteractEvent e){

            if(e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (e.getClickedBlock() != null && (e.getClickedBlock().getType().toString().contains("SIGN"))) {
                    if (utils.isRepairSign(e.getClickedBlock())) {
                        utils.repair(e.getPlayer());
                    }
                }
            }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBreakSign(BlockBreakEvent e){
        if(e.getBlock().getType().name().contains("SIGN")){
            if(utils.isRepairSign(e.getBlock()) && e.getPlayer().hasPermission("sign.break")){
                e.getPlayer().sendMessage(prefix+ "§cYou removed this §eRepairSign");
                fileManager.removeBlock(e.getBlock());
            }
        }
    }


}
