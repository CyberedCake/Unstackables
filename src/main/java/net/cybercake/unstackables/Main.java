package net.cybercake.unstackables;

import me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {
        long mss = System.currentTimeMillis();
        plugin = this;

        if(CommodoreProvider.isSupported()) {

        }else{
            logError("Failed to load Commodore, disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        logInfo("Enabled Unstackables [v" + getPlugin(Main.class).getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms");
    }

    @Override
    public void onDisable() {
        long mss = System.currentTimeMillis();

        logInfo("Disabled Unstackables [v" + getPlugin(Main.class).getDescription().getVersion() + "] in " + (System.currentTimeMillis()-mss) + "ms");
    }

    public static Main getPlugin() { return plugin; }
    public static FileConfiguration getMainConfig() { return plugin.getConfig(); }
    public static String getPluginPrefix() { return getPlugin(Main.class).getDescription().getPrefix(); }
    public static void logInfo(String msg) { Bukkit.getLogger().info("[" + getPluginPrefix() + "] " + msg); }
    public static void logWarn(String msg) { Bukkit.getLogger().warning("[" + getPluginPrefix() + "] " + msg); }
    public static void logError(String msg) { Bukkit.getLogger().severe(msg); }

    private static void registerCommand(String name, CommandExecutor commandExecutor) { plugin.getCommand(name).setExecutor(commandExecutor); }
    private static void registerTabCompleter(String name, TabCompleter tabCompleter) { plugin.getCommand(name).setTabCompleter(tabCompleter); }
    private static void registerListener(Listener listener) { plugin.getServer().getPluginManager().registerEvents(listener, plugin); }
    private static void registerRunnable(Runnable runnable, long period) { Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, 10L, period); }
}
