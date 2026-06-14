package org.timetracker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TimeTrackerCommand implements CommandExecutor {

    private final TimeTracker plugin;

    public TimeTrackerCommand(TimeTracker plugin) {
        this.plugin = plugin;
    }

    private Component parse(String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadConfig();
                String msg = plugin.getConfig().getString("timetracker.reload",
                        "&aConfiguration reloaded successfully.");
                sender.sendMessage(parse(msg));
            }
            case "help" -> sendHelp(sender);
            case "remove" -> handleRemove(sender, args);
            default -> sendUsage(sender);
        }
        return true;
    }

    private void handleRemove(CommandSender sender, String[] args) {
        if (!sender.isOp()) {
            String msg = plugin.getConfig().getString("timetracker.remove-no-permission",
                    "&cOnly operators can use this command.");
            sender.sendMessage(parse(msg));
            return;
        }

        if (args.length < 2) {
            String msg = plugin.getConfig().getString("timetracker.remove-usage",
                    "&cUsage: &f/timetracker remove <player>");
            sender.sendMessage(parse(msg));
            return;
        }

        String targetName = args[1];
        OfflinePlayer target = findPlayer(targetName);

        if (target == null) {
            String msg = plugin.getConfig().getString("timetracker.remove-not-found",
                    "&cPlayer &f{player} &cnot found.")
                    .replace("{player}", targetName);
            sender.sendMessage(parse(msg));
            return;
        }

        plugin.addExclusion(target.getUniqueId());

        String name = target.getName() != null ? target.getName() : targetName;
        String msg = plugin.getConfig().getString("timetracker.remove-success",
                "&aPlayer &f{player} &ahas been permanently removed from the top.")
                .replace("{player}", name);
        sender.sendMessage(parse(msg));
    }

    private OfflinePlayer findPlayer(String name) {
        org.bukkit.entity.Player online = plugin.getServer().getPlayerExact(name);
        if (online != null) return online;

        for (OfflinePlayer op : plugin.getServer().getOfflinePlayers()) {
            if (name.equalsIgnoreCase(op.getName())) return op;
        }
        return null;
    }

    private void sendUsage(CommandSender sender) {
        String msg = plugin.getConfig().getString("timetracker.usage",
                "&cUsage: &f/timetracker <reload|help|remove>");
        sender.sendMessage(parse(msg));
    }

    private void sendHelp(CommandSender sender) {
        String header = plugin.getConfig().getString("timetracker.help-header",
                "&8--------- &d[TimeTracker Help] &8---------");
        sender.sendMessage(parse(header));

        List<String> lines = plugin.getConfig().getStringList("timetracker.help-lines");
        if (lines.isEmpty()) {
            sender.sendMessage(parse("&e/time &7- Shows your total playtime."));
            sender.sendMessage(parse("&e/timetop [page] &7- Shows the top playtime ranking (offline included)."));
            sender.sendMessage(parse("&e/timetracker remove <player> &7- [OP] Removes a player from the top."));
            sender.sendMessage(parse("&e/timetracker reload &7- [OP] Reloads the configuration."));
            sender.sendMessage(parse("&e/timetracker help &7- Shows this help message."));
        } else {
            for (String line : lines) {
                sender.sendMessage(parse(line));
            }
        }

        String footer = plugin.getConfig().getString("timetracker.help-footer",
                "&8-----------------------------------------");
        sender.sendMessage(parse(footer));
    }
}
