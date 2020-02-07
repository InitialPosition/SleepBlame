package de.SYRAPT0R.sleepblame;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BedListener(), this);
        Logging.consoleLog("SleepBlame enabled.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Logging.consoleLog("SleepBlame shut down. Sleep tight.");
    }
}
