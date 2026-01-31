package dev.zonary123.zutils.utils;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.models.DurationValue;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Carlos Varas Alonso - 28/06/2024 20:44
 */
public class PlayerUtils {
  /**
   * Method to get the cooldown based on the player's permissions.
   *
   * @param cooldowns       The cooldowns to check.
   * @param defaultCooldown The default cooldown.
   * @param player          The player to check.
   * @return The cooldown.
   */
  public static int getCooldown(Map<String, Integer> cooldowns, int defaultCooldown, PlayerRef player) {
    int cooldown = defaultCooldown;
    var entries = cooldowns.entrySet();
    for (Map.Entry<String, Integer> entry : entries) {
      if (entry.getValue() < cooldown && player != null && PermissionsModule.get().hasPermission(player.getUuid(), entry.getKey())) {
        cooldown = entry.getValue();
      }
    }
    return cooldown;
  }

  public static long getCooldown(Map<String, DurationValue> cooldowns, DurationValue defaultCooldown, PlayerRef player) {
    long cooldown = defaultCooldown.toMillis();
    var entries = cooldowns.entrySet();
    for (Map.Entry<String, DurationValue> entry : entries) {
      if (entry.getValue().toMillis() > cooldown) continue;
      if (player != null && PermissionsModule.get().hasPermission(player.getUuid(), entry.getKey())) {
        cooldown = entry.getValue().toMillis();
      }
    }
    return cooldown;
  }

  @Deprecated(forRemoval = true, since = "1.1.3")
  public static String getCooldown(Date date) {
    if (date == null) return ZUtils.getLang().getNocooldown();
    return getCooldown(date.getTime());
  }

  private static final Cache<Long, String> cooldownCache = Caffeine.newBuilder()
    .maximumSize(10000)
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .build();

  /**
   * Method to get the cooldown in a human-readable format.
   *
   * @param timestamp The timestamp to check.
   * @return The cooldown in a human-readable format.
   */
  public static String getCooldown(long timestamp) {
    long timeMillis = timestamp - System.currentTimeMillis();
    if (timeMillis <= 0) return ZUtils.getLang().getNocooldown();

    long timeSeconds = timeMillis / 1000;
    return cooldownCache.get(timeSeconds, t -> {
      long days = t / (60 * 60 * 24);
      long hours = (t / (60 * 60)) % 24;
      long minutes = (t / 60) % 60;
      long seconds = t % 60;

      StringBuilder result = new StringBuilder();
      if (days > 0)
        result.append((days == 1 ? ZUtils.getLang().getDay() : ZUtils.getLang().getDays())
          .replace("%s", Long.toString(days)));
      if (hours > 0)
        result.append((hours == 1 ? ZUtils.getLang().getHour() : ZUtils.getLang().getHours())
          .replace("%s", Long.toString(hours)));
      if (minutes > 0)
        result.append((minutes == 1 ? ZUtils.getLang().getMinute() : ZUtils.getLang().getMinutes())
          .replace("%s", Long.toString(minutes)));
      if (seconds > 0)
        result.append((seconds == 1 ? ZUtils.getLang().getSecond() : ZUtils.getLang().getSeconds())
          .replace("%s", Long.toString(seconds)));

      return result.isEmpty() ? ZUtils.getLang().getNocooldown() : result.toString().trim();
    });
  }

  /**
   * Method to check if a cooldown is active.
   *
   * @param cooldown The cooldown to check.
   * @return true if the cooldown is active.
   */
  public static boolean isCooldown(Date cooldown) {
    if (cooldown == null) return false;
    return new Date().before(cooldown);
  }

  /**
   * Method to check if a cooldown is active.
   *
   * @param cooldown The cooldown to check.
   * @return true if the cooldown is active.
   */
  public static boolean isCooldown(Long cooldown) {
    if (cooldown == null) return false;
    return isCooldown(new Date(cooldown));
  }

  /**
   * Method to execute a command.
   *
   * @param command   The command to execute.
   * @param playerRef The player reference to execute the command as.
   */
  public static void executeCommand(String command, PlayerRef playerRef) {
    if (command.startsWith("console:")) executeCommandAsConsole(command.replaceFirst("console:", ""), playerRef);
    else if (command.startsWith("player:")) executeCommandAsPlayer(command.replaceFirst("player:", ""), playerRef);
    else executeCommandAsConsole(command, playerRef);
  }

  /**
   * Method to execute a command as console.
   *
   * @param command   The command to execute.
   * @param playerRef The player reference to replace %player% placeholder.
   */
  public static void executeCommandAsConsole(String command, PlayerRef playerRef) {
    var commandManager = HytaleServer.get().getCommandManager();
    commandManager.handleCommand(ConsoleSender.INSTANCE, command.replace("%player%", playerRef.getUsername()));
  }

  /**
   * Method to execute a command as player.
   *
   * @param command   The command to execute.
   * @param playerRef The player reference to execute the command as.
   */
  public static void executeCommandAsPlayer(String command, PlayerRef playerRef) {
    var commandManager = HytaleServer.get().getCommandManager();
    commandManager.handleCommand(playerRef, command.replace("%player%", playerRef.getUsername()));
  }

}
