# TODO

## Extra Feature List:

- **Punishment History with Annotations**  
  Allow staff to annotate specific punishments with notes to provide more context and explanations about the decision.

- **Punishment Appeals Voting System**  
  Let the community (or staff) vote on appeals or punishment cases (like a jury system) to decide on the appropriate action.

- **Punishment Notification with Customization**  
  Allow players to customize how they receive punishment notifications (in chat, in a private message, through Discord integration, etc.).

- **Global / Local Punishments**  
  Make it so certain punishments (like bans or mutes) can be global across all servers or just limited to the server they occurred on.

- **Smart Punishment Suggestion System (Configurable)**  
  Introduce a system that suggests punishment severity based on the player’s past behavior and the type of offense. Make it configurable by the server admins.

---

## Commands (so far)

### **Punishment Commands**  
Flags are case-insensitive:  
- `-s` - Silence punishment (do not announce).  
- `-g` - Make the punishment global (default action can be configured).  
- `-l` - Make the punishment local to the current server (default action can be configured).  

- **/ban <player> [reason] [-sGL]**: Permanently bans a player with an optional reason. `-s` silences the ban notification.  
- **/tempban <player> <duration> [reason] [-sGL]**: Temporarily bans a player for a specified duration with an optional reason.  
- **/unban <player>**: Unbans a previously banned player.  
- **/banip <IP|player> [reason] [-sGL]**: Bans a player's IP address or username with an optional reason.  
- **/tempbanip <IP|player> <duration> [reason] [-sGL]**: Temporarily bans a player's IP address for a specified duration.  
- **/unbanip <IP|player>**: Unbans a previously banned IP or player.  
- **/mute <player> [reason] [-sGL]**: Permanently mutes a player to prevent them from chatting.  
- **/tempmute <player> <duration> [reason] [-sGL]**: Temporarily mutes a player for a specified duration.  
- **/unmute <player>**: Unmutes a previously muted player.  
- **/kick <player> [reason] [-sGL]**: Kicks a player from the server with an optional reason.  
- **/warn <player> [reason] [-sGL]**: Issues a warning to a player for breaking rules.  
- **/clearwarns <player>**: Clears all warnings for a player.  
- **/checkpunish <player>**: Displays a player's punishment history.  

### **Management Commands**  
- **/history <player>**: Displays a full punishment history for a player.  
- **/staffhistory <staff>**: Shows all punishments issued by a specific staff member.  
- **/undo <player>**: Undoes the last punishment issued to a player.  
- **/dupeip <player>**: Checks if a player is using multiple accounts from the same IP.  
- **/checkip <player>**: Displays the IP address of a player.  
- **/clearchat [-sGL]**: Clears the global chat for all players. `-s` silences the chat clearing.  
- **/appeal <banID>**: Submits an appeal for a specific ban ID.  
- **/listappeals**: Displays a list of all pending punishment appeals.  

### **Server & API Commands**  
- **/syncpunish**: Syncs punishment data with the website.  
- **/reloadpunish**: Reloads the plugin configuration.  

### **Freeze Commands**  
- **/freeze <player>**: Freezes a player in place to prevent movement or interaction.  
- **/unfreeze <player>**: Unfreezes a previously frozen player.  

### **Rollback Commands**  
- **/rollbackpunish <player> [amount|duration|punishmentID]**: Undoes a specific number of punishments or a punishment by ID for a player.  
- **/rollbackstaff <staff> [amount|duration|punishmentID]**: Undoes a specific number of punishments or a punishment by ID issued by a staff member.  
- **/rollbackall [days]**: Undoes all punishments within the last X days.  

### **Additional Commands**  
- **/shadowmute <player> [reason]**: Mutes a player in such a way that only they can see their messages.  
- **/lockchat**: Locks the global chat so only staff members can send messages.  
- **/slowchat <seconds>**: Adds a delay between chat messages for all players.  
- **/checkalts <player>**: Displays any known alternate accounts for a player.  
- **/lookup <player>**: Shows detailed player information (e.g., IP, bans, first join).  
- **/softban <player> [reason]**: Bans a player temporarily but allows them to join again immediately after.  
- **/blacklist <player>**: Permanently prevents a player from rejoining the server.  
- **/temppardon <player> <duration>**: Temporarily unbans a player for a specified time period.  
- **/punishlog <player>**: Displays a detailed log of all punishments for a player.  
- **/stafflog <staff>**: Displays all punishments issued by a specific staff member.  
- **/notify <on|off>**: Toggles punishment notifications per player.  
- **/announcepunishments <on|off> [global]**: Toggles punishment announcements, can be used globally or locally.  

### **Punishment Appeal System**  
- **/annotatepunishment <punishmentID> <note>**: Adds an annotation or note to a specific punishment.  
- **/appeal <punishmentID>**: Allows a player to appeal a specific punishment.  
- **/appealvote <punishmentID> <approve|reject>**: Staff can vote on an appeal to approve or reject it.  
- **/appealstatus <punishmentID>**: Displays the status of a punishment appeal.  
- **/notifyprefs <in-game|discord|both>**: Allows players to set how they receive punishment notifications.  
- **/notifystatus <player>**: Displays a player’s notification preferences.  

## Permissions (so far)
# Plugin Permissions

## General Permissions
- **`punishplugin.command.use`**: Allows the user to use punishment-related commands.
- **`punishplugin.command.manage`**: Grants the ability to manage punishments (e.g., issue bans, mutes, kicks).
- **`punishplugin.command.view`**: Allows the user to view punishment histories and logs.

## Punishment Commands
- **`punishplugin.command.ban`**: Grants the ability to issue permanent bans (`/ban`, `/banip`).
- **`punishplugin.command.tempban`**: Grants the ability to issue temporary bans (`/tempban`, `/tempbanip`).
- **`punishplugin.command.unban`**: Grants the ability to unban players (`/unban`, `/unbanip`).
- **`punishplugin.command.mute`**: Grants the ability to issue permanent mutes (`/mute`).
- **`punishplugin.command.tempmute`**: Grants the ability to issue temporary mutes (`/tempmute`).
- **`punishplugin.command.unmute`**: Grants the ability to unmute players (`/unmute`).
- **`punishplugin.command.kick`**: Grants the ability to kick players from the server (`/kick`).
- **`punishplugin.command.warn`**: Grants the ability to issue warnings to players (`/warn`).
- **`punishplugin.command.clearwarns`**: Grants the ability to clear warnings for players (`/clearwarns`).
- **`punishplugin.command.checkpunish`**: Grants the ability to view a player's punishment history (`/checkpunish`).

## Appeals & Voting Commands
- **`punishplugin.command.appeal`**: Grants the ability to submit an appeal for a punishment (`/appeal`).
- **`punishplugin.command.appealvote`**: Grants the ability to vote on punishment appeals (`/appealvote`).
- **`punishplugin.command.appealstatus`**: Grants the ability to view the status of a punishment appeal (`/appealstatus`).

## Management Commands
- **`punishplugin.command.history`**: Grants the ability to view the full punishment history for a player (`/history`).
- **`punishplugin.command.staffhistory`**: Grants the ability to view the punishment history issued by a specific staff member (`/staffhistory`).
- **`punishplugin.command.undo`**: Grants the ability to undo the last punishment issued to a player (`/undo`).
- **`punishplugin.command.dupeip`**: Grants the ability to check if a player is using multiple accounts from the same IP (`/dupeip`).
- **`punishplugin.command.checkip`**: Grants the ability to check the IP address of a player (`/checkip`).
- **`punishplugin.command.clearchat`**: Grants the ability to clear the global chat (`/clearchat`).
- **`punishplugin.command.rollbackpunish`**: Grants the ability to undo punishments for a player (`/rollbackpunish`).
- **`punishplugin.command.rollbackstaff`**: Grants the ability to undo punishments issued by a specific staff member (`/rollbackstaff`).
- **`punishplugin.command.rollbackall`**: Grants the ability to undo all punishments in the last X days (`/rollbackall`).

## Freeze and Lock Commands
- **`punishplugin.command.freeze`**: Grants the ability to freeze a player in place (`/freeze`).
- **`punishplugin.command.unfreeze`**: Grants the ability to unfreeze a player (`/unfreeze`).
- **`punishplugin.command.lockchat`**: Grants the ability to lock the global chat (`/lockchat`).
- **`punishplugin.command.slowchat`**: Grants the ability to slow down chat messages (`/slowchat`).

## Additional Features
- **`punishplugin.command.shadowmute`**: Grants the ability to shadow mute a player (`/shadowmute`).
- **`punishplugin.command.checkalts`**: Grants the ability to check for alternate accounts of a player (`/checkalts`).
- **`punishplugin.command.lookup`**: Grants the ability to view detailed player information (`/lookup`).
- **`punishplugin.command.softban`**: Grants the ability to issue soft bans (`/softban`).
- **`punishplugin.command.blacklist`**: Grants the ability to blacklist a player (`/blacklist`).
- **`punishplugin.command.temppardon`**: Grants the ability to temporarily pardon a banned player (`/temppardon`).
- **`punishplugin.command.punishlog`**: Grants the ability to view detailed logs of all punishments for a player (`/punishlog`).
- **`punishplugin.command.stafflog`**: Grants the ability to view detailed logs of all punishments issued by a staff member (`/stafflog`).
- **`punishplugin.command.notify`**: Grants the ability to toggle punishment notifications for a player (`/notify`).
- **`punishplugin.command.announcepunishments`**: Grants the ability to toggle punishment announcements (`/announcepunishments`).

## Server & API Commands
- **`punishplugin.command.syncpunish`**: Grants the ability to sync punishment data with the website (`/syncpunish`).
- **`punishplugin.command.reloadpunish`**: Grants the ability to reload the plugin configuration (`/reloadpunish`).

## Annotation & Preferences
- **`punishplugin.command.annotatepunishment`**: Grants the ability to add annotations to punishments (`/annotatepunishment`).
- **`punishplugin.command.notifyprefs`**: Grants the ability to set notification preferences (`/notifyprefs`).
- **`punishplugin.command.notifystatus`**: Grants the ability to view a player's notification preferences (`/notifystatus`).

## Global Permissions
- **`punishplugin.command.global.manage`**: Grants full administrative control over punishments globally (e.g., issuing, viewing, undoing, and annotating punishments for all players).
- **`punishplugin.command.global.view`**: Grants the ability to view punishments and related data globally (e.g., all punishments across all servers).

## Immunity Permissions
- **`punishplugin.immune.ban`**: Allows the user to bypass bans and unban players (`/ban`).
- **`punishplugin.immune.mute`**: Allows the user to bypass mutes and unmute players (`/mute`, `/tempmute`).
- **`punishplugin.immune.kick`**: Allows the user to bypass kicks and prevent being kicked (`/kick`).
- **`punishplugin.immune.warn`**: Allows the user to bypass warnings (`/warn`).
- **`punishplugin.immune.freeze`**: Allows the user to bypass freeze effects (`/freeze`, `/unfreeze`).

## Bypass Permissions
- **`punishplugin.bypass.ban`**: Allows the user to bypass bans.
- **`punishplugin.bypass.mute`**: Allows the user to bypass mutes.
- **`punishplugin.bypass.kick`**: Allows the user to bypass kicks.
- **`punishplugin.bypass.warn`**: Allows the user to bypass warns.
- **`punishplugin.bypass.lockchat`**: Allows the user to bypass chat lock and slow chat restrictions (`/lockchat`, `/slowchat`).
- **`punishplugin.bypass.slowchat`**: Allows the user to bypass slow chat restrictions and send messages faster (`/slowchat`).
- **`punishplugin.bypass.all`**: Allows the user to commit actions on users with bypass permissions.

## Viewing Announcements
- **`punishplugin.bypass.viewannouncements`**: Allows the user to view announcements even if they are currently disabled or hidden.
- **`punishmentplugin.announcements.silent`**: Allows the user to view announcements if they are silent