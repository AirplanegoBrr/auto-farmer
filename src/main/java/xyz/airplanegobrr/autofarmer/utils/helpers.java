package xyz.airplanegobrr.autofarmer.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;

public class helpers {
    public int getAge(Block block) {
        return ((Ageable) block.getState().getBlockData()).getAge();
    }

    public int getMaxAge(Block block) {
        return ((Ageable) block.getState().getBlockData()).getMaximumAge();
    }

    public void setAge(Block block, int age) {
        Ageable ageC = (Ageable) block.getState().getBlockData();
        ageC.setAge(age);
        block.setBlockData(ageC);
    }

    public boolean isHarvestable(Block block) {
        World world = block.getWorld();
        Location blockLocation = block.getLocation();
        // Check if block below is farmland
        if (world.getBlockAt(blockLocation.subtract(0, 1, 0)).getType() != Material.FARMLAND)
            return false;
        if (!(block.getBlockData() instanceof Ageable)) return false; // Not a crop
        if (getAge(block) != getMaxAge(block)) return false; // Not harvestable
        return true;
    }
}
