package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@ToString
public class InteractionNPC {
  private PlayerRef playerRef;
  private Player player;
  private NPCEntity npcEntity;
  private UUID npcId;
}
