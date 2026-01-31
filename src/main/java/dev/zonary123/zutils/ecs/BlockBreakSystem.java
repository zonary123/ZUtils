package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.database.blocks.RegionBlockStorage;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.EventBlockBreak;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.ConcurrentHashMap;

import static dev.zonary123.zutils.ecs.BlockPlacedEvent.BLOCK_PLACE;

public final class BlockBreakSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

  public BlockBreakSystem() {

    super(BreakBlockEvent.class);
  }

  @Override
  public void handle(
    int index,
    @NonNull ArchetypeChunk<EntityStore> chunk,
    @NonNull Store<EntityStore> store,
    @NonNull CommandBuffer<EntityStore> commandBuffer,
    @NonNull BreakBlockEvent evt
  ) {
    var ref = chunk.getReferenceTo(index);
    var player = store.getComponent(ref, Player.getComponentType());
    var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    var pos = evt.getTargetBlock();
    if (player == null || playerRef == null) return;

    var block = evt.getBlockType();
    var blockId = block.getId();

    if (blockId == null || blockId.equals("Empty")) return;

    World world = player.getWorld();

    if (world == null) return;
    world.execute(() -> {
      WorldChunk worldChunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ()));
      if (worldChunk == null) return;


      ZUtils.ASYNC_CONTEXT.runAsync(() -> {
        BLOCK_PLACE.computeIfAbsent(
          playerRef.getUuid(),
          k -> new ConcurrentHashMap<>()
        ).merge(
          block.getItem(),
          -1,
          Integer::sum
        );
        boolean placed = RegionBlockStorage.isPlaced(world, worldChunk, pos);
        RegionBlockStorage.removePlaced(world, worldChunk, pos);
        var event = new EventBlockBreak(
          player,
          playerRef,
          pos,
          world,
          worldChunk,
          placed,
          blockId
        );
        if (ZUtilsEvents.BLOCK_BREAK_EVENT.isEmpty()) return null;
        ZUtilsEvents.BLOCK_BREAK_EVENT.emit(event);
        return event;
      });
    });
  }

  @Override
  public Query<EntityStore> getQuery() {
    return PlayerRef.getComponentType();
  }
}
