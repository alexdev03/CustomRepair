package it.alexdev_.customrepair;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private final CustomRepair cr;

    public FileManager(CustomRepair cr){
        this.cr=cr;
    }

    public List<Block> getBlocks(){
        List<Block> blocks = new ArrayList<>();
        for(String num : cr.getConfig().getConfigurationSection("Blocks").getKeys(false)){
            int number = Integer.parseInt(num);
            blocks.add(getBlock(number));

        }
        return blocks;
    }

    public boolean addBlock(Block block){
        if(block==null || !block.getType().name().contains("SIGN")) return true;
        try {
            int size = cr.getConfig().getConfigurationSection("Blocks").getKeys(false).size();
            cr.getConfig().getConfigurationSection("Blocks").createSection(String.valueOf(size));
            cr.getConfig().set("Blocks."+size+".Material", block.getType().name());
            cr.getConfig().set("Blocks."+size+".World", block.getLocation().getWorld().getName());
            cr.getConfig().set("Blocks."+size+".X", block.getLocation().getBlockX());
            cr.getConfig().set("Blocks."+size+".Y", block.getLocation().getBlockY());
            cr.getConfig().set("Blocks."+size+".Z", block.getLocation().getBlockZ());
            save();
        }catch (NullPointerException e){
            return true;
        }
        return false;

    }

    public int getSize(Block block){
        for(String num : cr.getConfig().getConfigurationSection("Blocks").getKeys(false)){
            int number = Integer.parseInt(num);
            Block block1 = getBlock(number);
            if(block.equals(block1)) return number;
        }
        return -1;
    }

    private Block getBlock(int num){
        World world = Bukkit.getWorld(cr.getConfig().getString("Blocks."+num+".World"));
        int x = Integer.parseInt(cr.getConfig().getString("Blocks."+num+".X"));
        int y = Integer.parseInt(cr.getConfig().getString("Blocks."+num+".Y"));
        int z = Integer.parseInt(cr.getConfig().getString("Blocks."+num+".Z"));
        return world.getBlockAt(new Location(world, x,y,z));
    }

    public void removeBlock(Block block){
        try {
            int size = getSize(block);
            cr.getConfig().set("Blocks."+size, null);
            save();
        }catch (NullPointerException ignored){
            ignored.printStackTrace();
        }

    }

    public void save(){
        cr.saveConfig();
    }
}
