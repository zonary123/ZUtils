package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 16:07
 */
@Data
@NoArgsConstructor
public class Damage {
  private Player player;
  private PlayerRef playerRef;
  private NPCEntity npcEntity;
  private String npcTypeId;
  private com.hypixel.hytale.server.core.modules.entity.damage.Damage hytaleDamage;

  public Damage(Player player, PlayerRef playerRef, NPCEntity npcEntity, String targetNPCId, com.hypixel.hytale.server.core.modules.entity.damage.Damage hytaleDamage) {
    this.player = player;
    this.playerRef = playerRef;
    this.npcEntity = npcEntity;
    this.npcTypeId = npcEntity.getNPCTypeId();
    this.hytaleDamage = hytaleDamage;
  }
}
