package dev.zonary123.zutils;

import com.hypixel.hytale.builtin.adventure.objectives.events.TreasureChestOpeningEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import dev.zonary123.zutils.commands.ZUtilsCommand;
import dev.zonary123.zutils.config.Config;
import dev.zonary123.zutils.config.Lang;
import dev.zonary123.zutils.database.blocks.RegionBlockStorage;
import dev.zonary123.zutils.ecs.*;
import dev.zonary123.zutils.utils.async.AsyncContext;
import dev.zonary123.zutils.utils.async.UtilsAsync;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

@Getter
@Setter
public class ZUtils extends JavaPlugin {
  private static ZUtils instance;
  private Config config;
  private Lang lang = new Lang();
  public static final AsyncContext ASYNC_CONTEXT = UtilsAsync.getContext("ZUtils");

  public ZUtils(@NonNull JavaPluginInit init) {
    super(init);
    instance = this;
  }

  @Override
  protected void setup() {
    try {
      super.setup();
      files();
      events();
      RegionBlockStorage.init(getPath());
      this.getCommandRegistry().registerCommand(new ZUtilsCommand());
    } catch (Exception e) {
      getLogger().atSevere().withCause(e).log("Error during ZUtils setup");
    }
  }

  private void files() {
    config = Config.init();
    lang = Lang.init();
  }

  @Override
  protected void shutdown() {
    super.shutdown();
    UtilsAsync.shutdownAll();
    RegionBlockStorage.shutdown();
  }

  private void events() {
    this.getEntityStoreRegistry().registerSystem(new BlockBreakSystem());
    this.getEntityStoreRegistry().registerSystem(new InteractPickUp());
    this.getEntityStoreRegistry().registerSystem(new CraftEvent());
    this.getEntityStoreRegistry().registerSystem(new BlockPlacedEvent());
    this.getEntityStoreRegistry().registerSystem(new DamageSystem());
    this.getEntityStoreRegistry().registerSystem(new KillEntitySystem());
    this.getEntityStoreRegistry().registerSystem(new UseBlockPickUp());
    this.getEntityStoreRegistry().registerSystem(new UseBlockECS());
    this.getEntityStoreRegistry().registerSystem(new PlayerThrowItem());
    this.getEventRegistry().registerGlobal(PlayerMouseButtonEvent.class, evt -> {
      Player player = evt.getPlayer();
      World world = player.getWorld();
      if (world == null) {
        if (ZUtils.getConfig().isDebug()) {
          ZUtils.getLog().atWarning().log(
            "PlayerMouseMotionEvent: Player %s is not in a world when moving mouse",
            player.getDisplayName()
          );
        }
        return;
      }
      world.execute(() -> {
        var ref = evt.getPlayerRef();
        var store = ref.getStore();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
          if (ZUtils.getConfig().isDebug()) {
            ZUtils.getLog().atWarning().log(
              "PlayerMouseMotionEvent: No PlayerRef found for entity %s when moving mouse",
              ref
            );
          }
          return;
        }
        ItemStack itemInHand = player.getInventory().getItemInHand();
        var mouseMotion = evt.getMouseButton();
        if (ZUtils.getConfig().isDebug()) {
          ZUtils.getLog().atInfo().log(
            "Player %s moved mouse with item %s in hand. button=%s",
            playerRef.getUsername(),
            itemInHand != null ? itemInHand.getItemId() : "null",
            mouseMotion.mouseButtonType
          );
        }
      });
    });
    this.getEventRegistry().registerGlobal(TreasureChestOpeningEvent.class, evt -> {
      var ref = evt.getPlayerRef();
      var store = evt.getStore();
      var player = store.getComponent(ref, PlayerRef.getComponentType());
      if (player == null) return;
      if (ZUtils.getConfig().isDebug()) {
        ZUtils.getLog().atInfo().log(
          "Player %s opened a treasure chest at %s",
          player.getUsername(),
          evt.getChestUUID()
        );
      }
    });
  }


  public static ZUtils get() {
    return instance;
  }

  public static Path getPath() {
    return get().getDataDirectory();
  }

  public static Config getConfig() {
    return ZUtils.get().config;
  }

  public static Lang getLang() {
    return ZUtils.get().lang;
  }

  public static HytaleLogger getLog() {
    return get().getLogger();
  }

}

