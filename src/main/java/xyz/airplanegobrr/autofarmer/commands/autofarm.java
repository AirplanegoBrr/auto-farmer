package xyz.airplanegobrr.autofarmer.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.airplanegobrr.autofarmer.AutoFarmer;
import xyz.airplanegobrr.autofarmer.utils.harvester;
import xyz.airplanegobrr.autofarmer.utils.scaner;


import java.util.Arrays;

public class autofarm implements CommandExecutor {
    private final AutoFarmer main;

    public autofarm(AutoFarmer main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        if (args.length == 0) {
            sender.sendMessage("[AutoFarmer] First thing first, thank you for downloading my plugin!");
            sender.sendMessage(String.format("[AutoFarmer] /%s harvest - Harvest the farm at the cords (Dev)", cmd.getLabel()));
            sender.sendMessage(String.format("[AutoFarmer] /%s scan - Scan for farm (Dev)", cmd.getLabel()));
            sender.sendMessage(String.format("[AutoFarmer] /%s create - Create farm", cmd.getLabel()));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "harvest": {
                Inventory inv = ((Player) sender).getInventory();
                Location loc = ((Player) sender).getLocation();

                Location loc2 = ((Player) sender).getLocation();
                int[] endLoc = {(int) loc2.getX(),  (int) loc2.getY(), (int) loc2.getZ()};

                int width = 9;
                int height = 9;

                if (args.length >= 4) {
                    loc.set(Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
                }
                if (args.length >= 7) {
                    endLoc[0] = Integer.valueOf(args[4]);
                    endLoc[1] = Integer.valueOf(args[5]);
                    endLoc[2] = Integer.valueOf(args[6]);
                }

                if (args.length >= 9) {
                    width = Integer.valueOf(args[7]);
                    height = Integer.valueOf(args[8]);
                }
                main.getLogger().info("Harvesting "+ loc.toString() + " End: " + Arrays.toString(endLoc));
                new harvester(main).autoHarvest(inv, loc, endLoc, width, height);
                break;
            }
            case "scan": {
                new scaner(main).scan(((Player) sender).getLocation());
                break;
            }
            default: {
                break;
            }
        }

        return true;
    }
}