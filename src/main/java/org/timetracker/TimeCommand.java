package org.timetracker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TimeCommand implements CommandExecutor {

    private final TimeTracker plugin;

    public TimeCommand(TimeTracker plugin) {
        this.plugin = plugin;
    }

    private Component parse(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            String msg = plugin.getConfig().getString("time.player-only",
                    "&cThis command can only be used by players.");
            sender.sendMessage(parse(msg));
            return true;
        }

        int totalTicks = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        int totalMinutes = (totalTicks / 20) / 60;
        int totalHours = totalMinutes / 60;
        int totalDays = totalHours / 24;

        String prefix = plugin.getConfig().getString("prefix", "&d[TIME]");
        String format = plugin.getConfig().getString("time.format",
                "{prefix} &f{player} &f➤ &7Time played: &e{days} days, {hours} hours and {minutes} minutes.");

        String message = format
                .replace("{prefix}", prefix)
                .replace("{player}", player.getName())
                .replace("{days}", String.valueOf(totalDays))
                .replace("{hours}", String.valueOf(totalHours % 24))
                .replace("{minutes}", String.valueOf(totalMinutes % 60));

        player.getServer().broadcast(parse(message));
        return true;
    }
}
