package xyz.airplanegobrr.autofarmer.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import xyz.airplanegobrr.autofarmer.AutoFarmer;

import java.util.Arrays;

public class scaner {

    AutoFarmer main;

    public scaner(AutoFarmer main) {
        this.main = main;
    }


    public int getWidth(int a, int b) {
        int width = Math.abs(a-b);
        if (a*b<0){
            width+=2;
        }
        return width;
    }

    // TODO: Finish this function
    public int[] scan(Location startingLoc) {

        World world = startingLoc.getWorld();
        Block check1 = world.getBlockAt(startingLoc.clone());
        Block check2 = world.getBlockAt(startingLoc.clone().subtract(0,1,0));

        Location loc = startingLoc.clone();

        if (check1.getType() != Material.FARMLAND && check2.getType() != Material.FARMLAND) {
            return null;
        }

        if (check1.getType() != Material.FARMLAND) loc.subtract(0,1,0);

        // Start ZYX
        // End ZYX
        // Width, Height
        //             X,Y,Z,X,Y,Z,W,H
        //             0,1,2,3,4,5,6,7
        int[] cords = {0,0,0,0,0,0,9,9};

        boolean checkingX = true;
        boolean checkingZ = true;

        main.info("Starting scan!");

        int currentX = 0;
        int currentZ = 0;
        while (checkingX) {
            int pos = currentX;

            // Q: Is .clone() needed?
            Location posLoc = loc.clone().add(pos,0,0);
            Location negLoc = loc.clone().subtract(pos,0,0);

            if (posLoc.getBlock().getType() == Material.FARMLAND) cords[0] = (int) posLoc.getX();
            if (negLoc.getBlock().getType() == Material.FARMLAND) cords[3] = (int) negLoc.getX();

            // Exit as we found needed values
            if (posLoc.getBlock().getType() != Material.FARMLAND && negLoc.getBlock().getType() != Material.FARMLAND) checkingX = false;
            currentX++;
        }

        while (checkingZ) {
            int pos = currentZ;

            // Q: Is .clone() needed?
            Location posLoc = loc.clone().add(0,0,pos);
            Location negLoc = loc.clone().subtract(0,0,pos);

            if (posLoc.getBlock().getType() == Material.FARMLAND) cords[2] = (int) posLoc.getZ();
            if (negLoc.getBlock().getType() == Material.FARMLAND) cords[5] = (int) negLoc.getZ();

            // Exit as we found needed values
            if (posLoc.getBlock().getType() != Material.FARMLAND && negLoc.getBlock().getType() != Material.FARMLAND) checkingZ = false;
            currentZ++;
        }

        cords[6] = getWidth(cords[0],cords[3]);
        cords[7] = getWidth(cords[2],cords[5]);

        main.info(Arrays.toString(cords));

        return cords;
    }
}
