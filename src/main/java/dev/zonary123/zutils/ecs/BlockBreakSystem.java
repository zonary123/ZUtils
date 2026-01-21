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
    if (player == null || playerRef == null) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atWarning().log(
          "BlockBreakSystem: Player or PlayerRef is null for entity %s",
          ref
        );
      }
      return;
    }

    var block = evt.getBlockType();
    var blockId = block.getId();

    if (blockId == null || blockId.equals("Empty")) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atWarning().log(
          "BlockBreakSystem: Block ID is null or Empty for block %s at %s by player %s",
          block,
          pos,
          playerRef.getUsername()
        );
      }
      return;
    }

    World world = player.getWorld();

    if (world == null) {
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atWarning().log(
          "BlockBreakSystem: World is null for player %s",
          playerRef.getUsername()
        );
      }
      return;
    }
    world.execute(() -> {
      WorldChunk worldChunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ()));
      if (worldChunk == null) {
        ZUtils.getLog().atWarning().log(
          "Player %s break a block at %s in world %s, but the chunk is null",
          playerRef.getUsername(),
          pos,
          world.getName()
        );
        return;
      }

      ZUtils.ASYNC_CONTEXT.runAsync(() -> {
        boolean placed = RegionBlockStorage.isPlaced(world, worldChunk, pos);
        RegionBlockStorage.removePlaced(world, worldChunk, pos);
        if (ZUtils.getConfig().isDebug()) {
          ZUtils.getLog().atInfo().log(
            "Player %s break block at %s in world %s. Already placed: %s",
            playerRef.getUsername(),
            pos,
            world.getName(),
            placed
          );
        }

        var event = new EventBlockBreak(
          player,
          playerRef,
          pos,
          world,
          worldChunk,
          placed,
          blockId
        );

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
