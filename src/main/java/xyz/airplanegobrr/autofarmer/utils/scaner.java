package xyz.airplanegobrr.autofarmer.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
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
        }else {
            width +=1;
        }
        return width;
    }

    // TODO: Finish this function
    public int[] scan(Location startingLoc) {
        Location loc = startingLoc.clone();

        // Start ZYX
        // End ZYX
        // Width, Height
        //             X,Y,Z,X,Y,Z,W,H
        //             0,1,2,3,4,5,6,7
        int[] cords = {0,0,0,0,0,0,9,9};

        boolean checkingX = true;
        boolean checkingZ = true;

        int currentX = 0;
        boolean x1D = false;
        boolean x2D = false;

        int currentZ = 0;
        boolean z1D = false;
        boolean z2D = false;

        while (checkingX) {
            int pos = currentX;

            // Q: Is .clone() needed?
            Location posLoc = loc.clone().add(pos,0,0);
            Location negLoc = loc.clone().subtract(pos,0,0);

            Block posBlock = posLoc.getBlock();
            Block negBlock = negLoc.getBlock();

            Material posMat = posBlock.getType();
            Material negMat = negBlock.getType();

            if (posMat == Material.FARMLAND && !x1D) cords[0] = (int) posLoc.getX();
            if (negMat == Material.FARMLAND && !x2D) cords[3] = (int) negLoc.getX();

            if (posMat != Material.FARMLAND) x1D = true;
            if (negMat != Material.FARMLAND) x2D = true;

            if (posMat == Material.WATER) x1D = false;
            if (negMat == Material.WATER) x2D = false;

            if (x1D && posBlock.getBlockData() instanceof Waterlogged) {
                Waterlogged wl = (Waterlogged) posBlock.getBlockData();
                if(wl.isWaterlogged()) x1D = false;
            }
            if (x2D && negBlock.getBlockData() instanceof Waterlogged) {
                Waterlogged wl = (Waterlogged) negBlock.getBlockData();
                if(wl.isWaterlogged()) x2D = false;
            }

            // Exit as we found needed values
            if (x1D && x2D) checkingX = false;
            currentX++;
        }

        while (checkingZ) {
            int pos = currentZ;

            // Q: Is .clone() needed?
            Location posLoc = loc.clone().add(0,0,pos);
            Location negLoc = loc.clone().subtract(0,0,pos);

            Block posBlock = posLoc.getBlock();
            Block negBlock = negLoc.getBlock();

            Material posMat = posBlock.getType();
            Material negMat = negBlock.getType();

            if (posMat == Material.FARMLAND && !z1D) cords[2] = (int) posLoc.getZ();
            if (negMat == Material.FARMLAND && !z2D) cords[5] = (int) negLoc.getZ();

            if (posMat != Material.FARMLAND) z1D = true;
            if (negMat != Material.FARMLAND) z2D = true;

            if (posMat == Material.WATER) z1D = false;
            if (negMat == Material.WATER) z2D = false;

            if (z1D && posBlock.getBlockData() instanceof Waterlogged) {
                Waterlogged wl = (Waterlogged) posBlock.getBlockData();
                if(wl.isWaterlogged()) z1D = false;
            }
            if (z2D && negBlock.getBlockData() instanceof Waterlogged) {
                Waterlogged wl = (Waterlogged) negBlock.getBlockData();
                if(wl.isWaterlogged()) z2D = false;
            }

            // Exit as we found needed values
            if (z1D && z2D) checkingZ = false;
            currentZ++;
        }

        cords[6] = getWidth(cords[0],cords[3]);
        cords[7] = getWidth(cords[2],cords[5]);

        return cords;
    }
}
