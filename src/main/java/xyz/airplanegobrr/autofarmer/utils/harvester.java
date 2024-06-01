package xyz.airplanegobrr.autofarmer.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.configuration.PathingRuleSet;
import org.patheloper.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.mapping.PatheticMapper;
import org.patheloper.mapping.bukkit.BukkitMapper;
import org.patheloper.api.pathing.result.PathfinderResult;
import xyz.airplanegobrr.autofarmer.AutoFarmer;

import java.util.concurrent.CompletionStage;

public class harvester {
    private final AutoFarmer main;

    public harvester(AutoFarmer main) {
        this.main = main;
    }

    private boolean harvest(Block block, Inventory inv) {
        World world = block.getWorld();
        Location blockLocation = block.getLocation();
        if (!new helpers().isHarvestable(block)) return false;

        world.playEffect(blockLocation.add(0,2,0), Effect.BONE_MEAL_USE, 10);

        for (ItemStack dropItemStack : block.getDrops()) {
            inv.addItem(dropItemStack.clone()); // Add item to inv
            new helpers().setAge(block, 0);
        }
        return true;
    }

    private Block moveTurtle(Block block, Location newLocation, Boolean leaveGap) {
        ItemStack[] items = new ItemStack[0];
        if (block.getType() == Material.DROPPER)
            items = ((Container) block.getState()).getInventory().getContents();

        block.setType(Material.AIR);

        World world = block.getWorld();

        Block b = world.getBlockAt(newLocation);
        if (b.getType() != Material.AIR && leaveGap)
            b = world.getBlockAt(newLocation.add(0, 1, 0));
        b.setType(Material.DROPPER);
        ((Container) b.getState()).getInventory().setContents(items);

        Directional dir = (Directional) b.getBlockData();
        dir.setFacing(BlockFace.DOWN);
        b.setBlockData(dir);
        b.getState().update();
        return b;
    }

    private boolean gotoPlace(World world, int[] startLoc, int[] endLoc) {
        Block startBlock = world.getBlockAt(startLoc[0], startLoc[1], startLoc[2]);
        Block endBlock = world.getBlockAt(endLoc[0], endLoc[1], endLoc[2]);

        PathPosition start = BukkitMapper.toPathPosition(startBlock.getLocation());
        PathPosition end = BukkitMapper.toPathPosition(endBlock.getLocation());

        Pathfinder pathfinder = PatheticMapper.newPathfinder(
                PathingRuleSet.createAsyncRuleSet()
                        .withAllowingFailFast(true)
                        .withAllowingFallback(true)
                        .withLoadingChunks(true));

        CompletionStage<PathfinderResult> pathfindingResult = pathfinder.findPath(start, end, new DirectPathfinderStrategy());

        pathfindingResult.thenAccept(result -> {
            main.getLogger().info("State: " + result.getPathState().name());
            main.getLogger().info("Path length: " + result.getPath().length());

            if (result.successful() || result.hasFallenBack()) {

                main.getLogger().info("Path finding starting!");
                final Block[] lastBlock = {startBlock};
                final int[] pathTimer = {5};

                result.getPath().forEach(loc -> {
                    new BukkitRunnable() {
                        public void run() {
                            main.getLogger().info("Moving turtle!");
                            Block oldBlock = lastBlock[0];
                            Block newBlock = moveTurtle(oldBlock,BukkitMapper.toLocation(loc), true);

                            lastBlock[0] = newBlock;
                        }
                    }.runTaskLater(main, pathTimer[0]);
                    pathTimer[0] = pathTimer[0] + 10;
                });

                new BukkitRunnable() {
                    public void run() {
                        main.getLogger().info("After path timer " +pathTimer[0]);
                        new BukkitRunnable() {
                            public void run() {
                                main.getLogger().info("Path done in "+ pathTimer[0]);
                                Block turtle = moveTurtle(lastBlock[0],endBlock.getLocation(), false);
                            }
                        }.runTaskLater(main,pathTimer[0]);
                    }
                }.runTaskLater(main,20);
            } else {
                main.getLogger().info("Path not found!");
            }
        });
        return true;
    }

    public boolean autoHarvest(Inventory inv, Location baseLoc, int[] endLoc, int width, int height) {
        Location cords = baseLoc.clone();

        int x = (int) cords.getX();
        int y = (int) cords.getY();
        int z = (int) cords.getZ();

        int turtleY = y + 2;
        int sleeper = 2;
        int sleeperAdd = 5;

        World world = cords.getWorld();

        final int[] last = new int[x];
        last[0] = x;
        last[1] = turtleY;
        last[2] = z;

        for (int mz = 0; mz < height; mz++) {
            int direction = mz % 2 == 0 ? 1 : -1;
            for (int mx = (direction == 1 ? 0 : width - 1); mx >= 0 && mx < width; mx += direction) {
                if (mx == x & mz == z) continue; // Skip
                int newAX = x - mx;
                int newAY = y - 1 + 1; // Makes the IDE shut up
                int newAZ = z - mz;

                // Crops
                Block gb = world.getBlockAt(newAX, newAY, newAZ);


                new BukkitRunnable() {
                    public void run() {
                        main.getLogger().info(String.valueOf(newAX) + " " + String.valueOf(newAZ));
                        Location d = gb.getLocation().add(0, 1, 0);

                        Block oldBlock = world.getBlockAt(last[0], last[1], last[2]);
                        Block newBlock = moveTurtle(oldBlock,d,false);

                        boolean good = harvest(gb, ((Container) newBlock.getState()).getInventory());

                        last[0] = newAX;
                        last[1] = (int) d.getY();
                        last[2] = newAZ;
                    }

                }.runTaskLater(main, sleeper);
                sleeper = sleeper + sleeperAdd;

            }
        }

        int finalSleeper = sleeper;
        int waitTime = 20;
        new BukkitRunnable() {
            public void run() {
                main.getLogger().info(String.valueOf(finalSleeper) + " timer!");

                new BukkitRunnable() {
                    public void run() {
                        main.getLogger().info("Done in " + finalSleeper + "!");
                        gotoPlace(world, last, endLoc);
                    }
                }.runTaskLater(main, finalSleeper - (waitTime / 2));
            }
        }.runTaskLater(main, waitTime);
        return true;
    }
}
