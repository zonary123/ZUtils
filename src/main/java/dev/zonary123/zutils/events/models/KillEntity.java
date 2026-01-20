package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 16:07
 */
@Data
@AllArgsConstructor
public class KillEntity {
  private Player player;
  private PlayerRef playerRef;
  private NPCEntity npcEntity;
  private String npcTypeId;

}
