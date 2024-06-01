package xyz.airplanegobrr.autofarmer.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.airplanegobrr.autofarmer.AutoFarmer;

import java.util.Arrays;
import java.util.List;

public class autofarmTabber implements TabCompleter {

    private final AutoFarmer main;

    public autofarmTabber(AutoFarmer main) {
        this.main = main;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        main.getLogger().info(Arrays.toString(strings) +" " + strings.length);

        if (strings.length == 1) return List.of("scan","harvest","check","list","create");

        switch (strings[0]){
            case "harvest": {
                Location loc = ((Player) commandSender).getLocation();
                if (strings.length == 2 || strings.length == 5) return List.of(String.valueOf((int) loc.getX()));
                if (strings.length == 3 || strings.length == 6) return List.of(String.valueOf((int) loc.getY()));
                if (strings.length == 4 || strings.length == 7) return List.of(String.valueOf((int) loc.getZ()));
            }
            default: {
                return List.of();
            }
        }
    }
}
