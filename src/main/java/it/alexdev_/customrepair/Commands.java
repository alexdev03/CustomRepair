package it.alexdev_.customrepair;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public class Commands implements CommandExecutor {

    private CustomRepair cr;
    private final FileManager fileManager;
    private final Utils utils;
    private final String prefix;

    public Commands(CustomRepair cr){
        this.cr = cr;
        fileManager = new FileManager(cr);
        utils = new Utils(cr);
        prefix = ChatColor.translateAlternateColorCodes('&', cr.getConfig().getString("Prefix"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("cr")) {
            if (args.length == 0) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("sign.create")) {
                        Player player = (Player) sender;
                        Block b = player.getTargetBlock(null, 5);
                        System.out.println(b);
                        boolean check = fileManager.addBlock(b);
                        if (check) {
                            player.sendMessage(prefix + "Error whilst adding the block");
                        } else {
                            player.sendMessage(prefix + "Block successfully added");
                        }
                    }
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("repair") && sender.hasPermission("repair")) {
                if (sender instanceof Player) {
                    //if(args.length==2){
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player.isValid()) {
                        utils.repair(player);
                    } else {
                        sender.sendMessage(prefix + "Invalid player");
                    }
                } else {
                    sender.sendMessage(prefix + "This command can only be executed from a player");
                }
            }
        }
        return false;
    }
}
