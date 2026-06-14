# TimeTracker Plugin

A Paper Minecraft plugin that tracks and displays playtime for all players — online and offline.

## Features

- Shows individual playtime in days, hours and minutes
- Global top ranking with pagination, including offline players
- Only players with at least 1 minute of playtime appear in the ranking
- Fully configurable messages, prefix and format via `config.yml`
- Persistent player exclusion and restoration from the top ranking
- Admin commands protected by operator permission

## Commands

| Command | Description |
|---|---|
| `/playtime` | Broadcasts your total playtime to all players |
| `/timetop [page]` | Shows the playtime ranking (5 players per page, offline included) |
| `/timetracker help` | Shows all available commands |
| `/timetracker reload` | Reloads `config.yml` without restarting the server |
| `/timetracker remove <player>` | **[OP]** Permanently removes a player from the top ranking |
| `/timetracker restore <player>` | **[OP]** Restores a previously removed player to the top ranking |

## Configuration

After the first start, `config.yml` is generated in `plugins/TimeTracker/`.

```yaml
prefix: "&d[TIME]"

time:
  player-only: "&cThis command can only be used by players."
  format: "{prefix} &f{player} &f➤ &7Time played: &e{days} days, {hours} hours and {minutes} minutes."

timetop:
  no-players: "&cNo player data found."
  header: "&8--------- &d[TOP PLAYTIME] &8---------"
  footer: "&8-------- &7Page {page}&8/&7{total} &8--------"
  line-format: "&6{position}. &f{player} &f➤ &e{days} days, {hours} hours and {minutes} minutes."

timetracker:
  usage: "&cUsage: &f/timetracker <reload|help|remove|restore>"
  reload: "&aConfiguration reloaded successfully."
  remove-success: "&aPlayer &f{player} &ahas been permanently removed from the top."
  restore-success: "&aPlayer &f{player} &ahas been restored to the top."
```

### Color codes

Use standard Minecraft `&` color codes. Full list: https://minecraft.wiki/w/Formatting_codes

### Placeholders

| Placeholder | Available in |
|---|---|
| `{prefix}` | `time.format` |
| `{player}` | `time.format`, `timetop.line-format`, remove/restore messages |
| `{days}` | `time.format`, `timetop.line-format` |
| `{hours}` | `time.format`, `timetop.line-format` |
| `{minutes}` | `time.format`, `timetop.line-format` |
| `{position}` | `timetop.line-format` |
| `{page}` | `timetop.footer` |
| `{total}` | `timetop.footer` |

## Permissions

All commands are usable by any player except `/timetracker remove` and `/timetracker restore`, which require **operator** status.

## Data

Removed players are stored in `plugins/TimeTracker/data.yml` and persist across server restarts. Use `/timetracker restore <player>` to undo a removal.

## Requirements

- Paper 1.21.11+
- Java 21+

## Building

```bash
./gradlew build
```

The compiled JAR will be at `build/libs/TimeTracker-1.0.jar`.
