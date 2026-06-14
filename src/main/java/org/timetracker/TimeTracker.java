package org.timetracker;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class TimeTracker extends JavaPlugin {
    private static final String RED = "[31m";
    private static final String RESET = "[0m";
    private static final String MAGENTA = "[35m";
    private static final String GREEN = "[32m";
    private static final String GRAY = "[90m";

    private File dataFile;
    private YamlConfiguration dataConfig;
    private final Set<UUID> excludedPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadData();
        this.getCommand("time").setExecutor(new TimeCommand(this));
        this.getCommand("timetop").setExecutor(new TimeTopCommand(this));
        this.getCommand("timetracker").setExecutor(new TimeTrackerCommand(this));

        getServer().getConsoleSender().sendMessage(
                MAGENTA + "[TimeTracker] " + GREEN + "✔ " + GRAY + "Commands /time, /timetop and /timetracker ready." + RESET
        );
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(
                MAGENTA + "[TimeTracker] " + RED + "❌ " + GRAY + "Time tracker plugin disabled." + RESET
        );
    }

    private void loadData() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                getSLF4JLogger().error("Could not create data.yml", e);
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        for (String s : dataConfig.getStringList("excluded-players")) {
            try {
                excludedPlayers.add(UUID.fromString(s));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    private void saveData() {
        List<String> uuids = excludedPlayers.stream()
                .map(UUID::toString)
                .collect(Collectors.toList());
        dataConfig.set("excluded-players", uuids);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getSLF4JLogger().error("Could not save data.yml", e);
        }
    }

    public boolean isExcluded(UUID uuid) {
        return excludedPlayers.contains(uuid);
    }

    public void addExclusion(UUID uuid) {
        excludedPlayers.add(uuid);
        saveData();
    }
}
