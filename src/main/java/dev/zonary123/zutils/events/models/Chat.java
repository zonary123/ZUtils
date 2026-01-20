package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chat {
  private PlayerRef playerRef;
  private String content;

}
