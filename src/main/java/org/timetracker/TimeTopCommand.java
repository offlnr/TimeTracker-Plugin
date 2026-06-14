package org.timetracker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class TimeTopCommand implements CommandExecutor {

    private static final int PAGE_SIZE = 5;

    private final TimeTracker plugin;

    public TimeTopCommand(TimeTracker plugin) {
        this.plugin = plugin;
    }

    private Component parse(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    private record PlayerTime(String name, long ticks) {}

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int page = 1;
        if (args.length > 0) {
            try {
                page = Math.max(1, Integer.parseInt(args[0]));
            } catch (NumberFormatException ignored) {}
        }

        List<PlayerTime> players = loadAllPlayerTimes();

        if (players.isEmpty()) {
            String msg = plugin.getConfig().getString("timetop.no-players",
                    "&cNo player data found.");
            sender.sendMessage(parse(msg));
            return true;
        }

        players.sort((a, b) -> Long.compare(b.ticks(), a.ticks()));

        int totalPages = (int) Math.ceil((double) players.size() / PAGE_SIZE);
        page = Math.min(page, totalPages);

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, players.size());

        String header = plugin.getConfig().getString("timetop.header",
                "&8--------- &d[TOP PLAYTIME] &8---------");
        sender.sendMessage(parse(header));

        String lineFormat = plugin.getConfig().getString("timetop.line-format",
                "&6{position}. &f{player} &f➤ &e{hours} hours");

        for (int i = start; i < end; i++) {
            PlayerTime pt = players.get(i);
            int totalHours = (int) ((pt.ticks() / 20) / 3600);

            String line = lineFormat
                    .replace("{position}", String.valueOf(i + 1))
                    .replace("{player}", pt.name())
                    .replace("{hours}", String.valueOf(totalHours));

            sender.sendMessage(parse(line));
        }

        String footerFormat = plugin.getConfig().getString("timetop.footer",
                "&8-------- &7Page {page}&8/&7{total} &8--------");
        String footer = footerFormat
                .replace("{page}", String.valueOf(page))
                .replace("{total}", String.valueOf(totalPages));
        sender.sendMessage(parse(footer));

        return true;
    }

    private List<PlayerTime> loadAllPlayerTimes() {
        List<PlayerTime> result = new ArrayList<>();
        File statsFolder = new File(plugin.getServer().getWorlds().get(0).getWorldFolder(), "stats");
        if (!statsFolder.exists()) return result;

        for (OfflinePlayer op : plugin.getServer().getOfflinePlayers()) {
            String name = op.getName();
            if (name == null) continue;
            if (plugin.isExcluded(op.getUniqueId())) continue;

            File statsFile = new File(statsFolder, op.getUniqueId() + ".json");
            if (!statsFile.exists()) continue;

            try (FileReader reader = new FileReader(statsFile)) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject stats = json.getAsJsonObject("stats");
                if (stats == null) continue;
                JsonObject custom = stats.getAsJsonObject("minecraft:custom");
                if (custom == null) continue;

                // "play_one_minute" was renamed to "play_time" in 1.17 but Minecraft
                // may still write the old key depending on when the data was created
                long ticks = 0;
                if (custom.has("minecraft:play_one_minute")) {
                    ticks = custom.get("minecraft:play_one_minute").getAsLong();
                } else if (custom.has("minecraft:play_time")) {
                    ticks = custom.get("minecraft:play_time").getAsLong();
                }

                if (ticks > 0) {
                    result.add(new PlayerTime(name, ticks));
                }
            } catch (Exception ignored) {}
        }

        return result;
    }
}
