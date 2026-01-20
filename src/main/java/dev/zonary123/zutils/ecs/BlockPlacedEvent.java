package dev.zonary123.zutils.ecs;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.database.blocks.RegionBlockStorage;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.EventBlockPlaced;
import org.jspecify.annotations.NonNull;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 9:24
 */
public class BlockPlacedEvent extends EntityEventSystem<EntityStore, PlaceBlockEvent> {
  public BlockPlacedEvent() {
    super(PlaceBlockEvent.class);
  }

  @Override
  public void handle(int index, @NonNull ArchetypeChunk<EntityStore> archetypeChunk, @NonNull Store<EntityStore> store,
                     @NonNull CommandBuffer<EntityStore> commandBuffer, @NonNull PlaceBlockEvent evt) {
    var ref = archetypeChunk.getReferenceTo(index);
    var player = store.getComponent(ref, Player.getComponentType());
    var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    var pos = evt.getTargetBlock();
    if (player == null || playerRef == null) {
      ZUtils.getLog().atWarning().log(
        "A block place event was triggered by an entity without a Player or PlayerRef component"
      );
      return;
    }
    World world = player.getWorld();
    if (world == null) {
      ZUtils.getLog().atWarning().log(
        "Player %s placed a block at %s in a null world",
        playerRef.getUsername(),
        pos
      );
      return;
    }
    ItemStack itemStack = evt.getItemInHand();
    if (itemStack == null) {
      ZUtils.getLog().atWarning().log(
        "Player %s placed a block at %s in world %s, but the item in hand is null",
        playerRef.getUsername(),
        pos,
        world.getName()
      );
      return;
    }

    var blockId = itemStack.getBlockKey();
    if (blockId == null || blockId.equals("Empty")) {
      ZUtils.getLog().atWarning().log(
        "Player %s placed a block at %s in world %s, but the block ID is null",
        playerRef.getUsername(),
        pos,
        world.getName()
      );
      return;
    }
    world.execute(() -> {
      WorldChunk worldChunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ()));
      if (worldChunk == null) {
        ZUtils.getLog().atWarning().log(
          "Player %s placed a block at %s in world %s, but the chunk is null",
          playerRef.getUsername(),
          pos,
          world.getName()
        );
        return;
      }
      ZUtils.ASYNC_CONTEXT.runAsync(() -> {
        boolean placed = RegionBlockStorage.isPlaced(world, worldChunk, pos);
        RegionBlockStorage.markPlaced(world, worldChunk, pos);
        if (ZUtils.getConfig().isDebug()) {
          ZUtils.getLog().atInfo().log(
            "Player %s placed block at %s in world %s. Already placed: %s",
            playerRef.getUsername(),
            pos,
            world.getName(),
            placed
          );
        }

        var event = new EventBlockPlaced(
          player,
          playerRef,
          pos,
          world,
          worldChunk,
          placed,
          blockId
        );
        ZUtilsEvents.BLOCK_PLACED_EVENT.emit(event);
        return event;
      });
    });
  }

  @Override public Query<EntityStore> getQuery() {
    return Query.any();
  }
}
