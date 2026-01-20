package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 9:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventBlock {
  private Player player;
  private PlayerRef playerRef;
  private Vector3i blockPos;
  private World world;
  private WorldChunk worldChunk;
  private boolean placed;
  private String blockId;
}
