package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.Chat;
import dev.zonary123.zutils.events.models.Command;

/**
 *
 * @author Carlos Varas Alonso - 23/01/2026 13:50
 */
public class PlayerChatEcs {
  public static void register() {
    ZUtils.get().getEventRegistry().registerGlobal(PlayerChatEvent.class, evt -> {
      if (ZUtilsEvents.CHAT_EVENT.isEmpty()) return;
      PlayerRef playerRef = evt.getSender();
      String content = evt.getContent();
      ZUtils.ASYNC_CONTEXT.runAsync(() -> {
        ZUtilsEvents.CHAT_EVENT.emit(new Chat(playerRef, content));
        if (content.startsWith("/")) ZUtilsEvents.COMMAND_EVENT.emit(new Command(playerRef, content));
        return null;
      });
    });
  }
}
