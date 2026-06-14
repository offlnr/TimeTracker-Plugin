# TimeTracker Plugin

A Paper Minecraft plugin that tracks and displays playtime for all players — online and offline.

## Features

- Shows individual playtime in days, hours and minutes
- Global top ranking with pagination, including offline players
- Fully configurable messages, prefix and format via `config.yml`
- Persistent player exclusion from the top ranking
- Admin commands protected by operator permission

## Commands

| Command | Description |
|---|---|
| `/time` | Broadcasts your total playtime to all players |
| `/timetop [page]` | Shows the playtime ranking (5 players per page) |
| `/timetracker help` | Shows all available commands |
| `/timetracker reload` | Reloads `config.yml` without restarting the server |
| `/timetracker remove <player>` | **[OP]** Permanently removes a player from the top ranking |

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
  line-format: "&6{position}. &f{player} &f➤ &e{hours} hours"
```

### Color codes

Use standard Minecraft `&` color codes. Full list: https://minecraft.wiki/w/Formatting_codes

### Placeholders

| Placeholder | Available in |
|---|---|
| `{prefix}` | `time.format` |
| `{player}` | `time.format`, `timetop.line-format`, `timetracker.remove-*` |
| `{days}` | `time.format` |
| `{hours}` | `time.format`, `timetop.line-format` |
| `{minutes}` | `time.format` |
| `{position}` | `timetop.line-format` |
| `{page}` | `timetop.footer` |
| `{total}` | `timetop.footer` |

## Permissions

All commands are usable by any player except `/timetracker remove`, which requires **operator** status.

## Requirements

- Paper 1.21.11+
- Java 21+

## Building

```bash
./gradlew build
```

The compiled JAR will be at `build/libs/TimeTracker-1.0.jar`.
