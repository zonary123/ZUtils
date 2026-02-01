package dev.zonary123.zutils.models.notifications;

import dev.zonary123.zutils.utils.PlayerUtils;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class Notification {
  private String title;
  private String subtitle;
  private String icon;

  /**
   * Send the notification to a player without any placeholders.
   *
   * @param playerUuid UUID of the player to send the notification to.
   * @param prefix     Prefix to be added to the notification.
   */
  public void send(UUID playerUuid, String prefix) {
    send(playerUuid, prefix, null);
  }

  /**
   * Send the notification to a player with a specified prefix.
   *
   * @param playerUuid   UUID of the player to send the notification to.
   * @param prefix       Prefix to be added to the notification.
   * @param placeholders Map of placeholders to replace in the title and subtitle.
   */
  public void send(UUID playerUuid, String prefix, @Nullable Map<String, String> placeholders) {
    String title = this.title;
    String subtitle = this.subtitle;
    String icon = this.icon;
    if (placeholders != null && !placeholders.isEmpty()) {
      for (Map.Entry<String, String> entry : placeholders.entrySet()) {
        String placeholder = entry.getKey();
        String value = entry.getValue();
        if (title != null && !title.isBlank() && title.contains(placeholder)) title = title.replace(placeholder, value);
        if (subtitle != null && !subtitle.isBlank() && subtitle.contains(placeholder))
          subtitle = subtitle.replace(placeholder, value);
      }
    }
    PlayerUtils.sendNotification(
      playerUuid,
      title,
      subtitle,
      icon,
      prefix
    );
  }
}
