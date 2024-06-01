package xyz.airplanegobrr.autofarmer;

import org.bukkit.ChatColor; // TODO: Replace ChatColor with use raw color codes using §
import org.bukkit.plugin.java.JavaPlugin;
import org.patheloper.mapping.PatheticMapper;
import xyz.airplanegobrr.autofarmer.commands.autofarm;
import xyz.airplanegobrr.autofarmer.commands.autofarmTabber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public final class AutoFarmer extends JavaPlugin {

    public final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public Map<String, Object> config = new HashMap<>();
    public Map<String, Object> data = new HashMap<>();

    private File configFile;
    private File dataFile;

    public void info(String msg) {
        getServer().getConsoleSender().sendMessage("[AutoFarm] "+msg);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        info("Plugin loading!");

        info(ChatColor.BLUE + "Loading "+ChatColor.RESET+ "conifg + data");

        configFile = new File(getDataFolder(), "config.json");
        if (!configFile.exists()) saveResource(configFile.getName(), false);
        try {
            config = gson.fromJson(new FileReader(configFile), new HashMap<String, Object>().getClass());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        dataFile = new File(getDataFolder(), "data.json");
        if (!dataFile.exists()) saveResource(dataFile.getName(), false);
        try {
            data = gson.fromJson(new FileReader(dataFile), new HashMap<String, Object>().getClass());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        info(ChatColor.GREEN + "Loaded" +ChatColor.RESET+" config + data");
        info(ChatColor.BLUE + "Loading" +ChatColor.RESET+" pathfinder");

        PatheticMapper.initialize(this);

        info(ChatColor.GREEN + "Loaded" +ChatColor.RESET+" pathfinder");
        info(ChatColor.BLUE + "Loading" +ChatColor.RESET+" commands");

        getCommand("autofarm").setExecutor(new autofarm(this));
        getCommand("autofarm").setTabCompleter(new autofarmTabber(this));

        info(ChatColor.GREEN + "Loaded" +ChatColor.RESET+" commands");

        // getCommand()
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        final String json = gson.toJson(config);
        configFile.delete();
        try {
            Files.write(configFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PatheticMapper.shutdown();
    }
}
