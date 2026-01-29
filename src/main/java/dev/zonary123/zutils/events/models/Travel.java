package dev.zonary123.zutils.events.models;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Travel {
  private PlayerRef playerRef;
  private Player player;
  private World world;
  private WorldMapTracker worldMapTracker;
  private Vector3d position;
  private double distanceTraveled;


  @Override
  public String toString() {
    return "Travel{" +
      "playerRef=" + playerRef.getUsername() +
      ", world=" + world.getName() +
      ", zone=" + worldMapTracker.getCurrentZone() +
      ", distanceTraveled=" + distanceTraveled +
      '}';
  }
}
