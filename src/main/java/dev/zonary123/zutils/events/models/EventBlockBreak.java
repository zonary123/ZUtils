package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 10:00
 */
@EqualsAndHashCode(callSuper = true) @Data
public class EventBlockBreak extends EventBlock {
  public EventBlockBreak(Player player, PlayerRef playerRef, Vector3i pos, World world, WorldChunk worldChunk, boolean placed, String blockId) {
    super(player, playerRef, pos, world, worldChunk, placed, blockId);
  }
}
