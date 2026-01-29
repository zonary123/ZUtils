package dev.zonary123.zutils.ecs;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.Travel;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TravelSystem extends EntityTickingSystem<EntityStore> {
  /**
   * Cache to store the last known position for each player to calculate distance traveled.
   */
  private static final Cache<UUID, Vector3d> LAST_POS_CACHE = Caffeine.newBuilder()
    .expireAfterAccess(15, TimeUnit.SECONDS)
    .build();
  /**
   * Cache to store the last travel time for each player to enforce a cooldown on travel events.
   */
  private static final Cache<UUID, Long> LAST_TRAVEL_TIME_CACHE = Caffeine.newBuilder()
    .expireAfterAccess(15, TimeUnit.SECONDS)
    .build();

  // In hytale 1 tick = 33ms
  private static final Long TRAVEL_COOLDOWN_MS = 1000L; // 1 second

  @Override
  public void tick(
    float v,
    int index,
    @NonNull ArchetypeChunk<EntityStore> archetypeChunk,
    @NonNull Store<EntityStore> store,
    @NonNull CommandBuffer<EntityStore> commandBuffer
  ) {
    var ref = archetypeChunk.getReferenceTo(index);
    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) return;
    UUID worldUuid = playerRef.getWorldUuid();
    if (worldUuid == null) return;
    World world = Universe.get().getWorld(worldUuid);
    if (world == null) return;
    world.execute(() -> {
      Player player = store.getComponent(ref, Player.getComponentType());
      if (player == null) return;
      ZUtils.ASYNC_CONTEXT.runAsync(() -> {
        long now = System.currentTimeMillis();
        Long lastTravelTime = LAST_TRAVEL_TIME_CACHE.getIfPresent(playerRef.getUuid());
        if (lastTravelTime != null) {
          long timeSinceLastTravel = now - lastTravelTime;
          if (timeSinceLastTravel < TRAVEL_COOLDOWN_MS) return null;
        }
        LAST_TRAVEL_TIME_CACHE.put(playerRef.getUuid(), now);
        WorldMapTracker worldMapTracker = player.getWorldMapTracker();
        var transformPosition = worldMapTracker.getTransformComponent();
        if (transformPosition == null) return null;
        Vector3d currentPos = transformPosition.getPosition();
        Vector3d lastPos = LAST_POS_CACHE.getIfPresent(playerRef.getUuid());
        double distance;
        if (lastPos != null) {
          distance = currentPos.distanceTo(lastPos);
          if (distance < 0.1 || distance > 20) {
            LAST_POS_CACHE.put(playerRef.getUuid(), currentPos.clone());
            return null;
          }
        } else {
          distance = 0;
        }
        LAST_POS_CACHE.put(playerRef.getUuid(), currentPos.clone());
        Travel travel = Travel.builder()
          .playerRef(playerRef)
          .player(player)
          .world(world)
          .worldMapTracker(worldMapTracker)
          .distanceTraveled(distance)
          .position(currentPos)
          .build();
        if (ZUtils.getConfig().isDebug()) {
          ZUtils.getLog().atInfo().log(
            "Player %s, emit event Travel: %s",
            playerRef.getUsername(),
            travel
          );
        }
        ZUtilsEvents.TRAVEL_EVENT.emit(travel);
        return null;
      });
    });
  }

  @Override
  public @Nullable Query<EntityStore> getQuery() {
    return PlayerRef.getComponentType();
  }
}
