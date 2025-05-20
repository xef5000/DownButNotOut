# DBNO (Down But Not Out)
A Minecraft plugin that adds player KO'ing into the game

![image](https://github.com/user-attachments/assets/2a71b495-cc50-400b-965d-76f27fcb872d)

Original download page:
https://www.spigotmc.org/resources/dbno-down-but-not-out.100654/

# Commands
- `/setreviveclicks [amt]` - Sets the needed amount of clicks to revive a player
- `/dbnoreloadconfig` - Reload the config
- `/setbleedouttime [time]` - Sets the amount of time (in seconds) before the player bleeds out (dies)
- `/reviveplayer [player]` - Revives a knocked out player
- `/removedbnostands` - Removes plugin related armorstands

# Fork modifications
- More configuration in the config
- `ReviveEvent` and `KOEvent` added as an API
- Updated the source code to reflect update 1.1 (1.1 is available to download on spigot, but source code is unavailable)
- General bugfix
- Removed damage causes

## New config
```yaml
# Enables/Disables players being downed instead of dying
enable-downs: true

# The amount of time (in seconds) it takes for a downed player to bleed out (die)
bleed-out-time: 15

# The amount of right clicks needed to revive a downed player
clicks-to-revive: 10

# Whether the player can take damage from mobs while being down
damage-while-down: false

# Whether the player can attack while being down
attack-while-down: false

# Whether to play sounds while reviving or a player is down
play-sounds: true


# The text found in the plugin
# when the player goes down without a set cause "player-down-no-cause" will show
# otherwise the message shown will be "player-down-cause" and "down-cause-*" put together
# use & for colour codes
# use (p) to put the players name in the message
messages:
  title-top: "&cYou have been downed."
  title-bottom: "&cHopefully someone is able to revive you!"
  bled-out: "&c(p) bled out."
  revive-by: "&aYou have been revived by (p)!"
  revive-other: "&aYou revived (p)!"
  reviving-by: "&aYou are being revived by (p)!"
  reviving-other: "&aYou are reviving (p)!"
  clicks-left: "&6(c)"
  clicks-left-text: "&6Clicks left to revive!"
  ko-stand: "&c&l(p) IS DOWN!"
  revive-stand: "&6%lRight click to revive!"

```
