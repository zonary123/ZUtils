package dev.zonary123.zutils;

import com.hypixel.hytale.builtin.adventure.objectives.events.TreasureChestOpeningEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import dev.zonary123.zutils.commands.ZUtilsCommand;
import dev.zonary123.zutils.config.Config;
import dev.zonary123.zutils.config.Lang;
import dev.zonary123.zutils.database.blocks.RegionBlockStorage;
import dev.zonary123.zutils.ecs.*;
import dev.zonary123.zutils.events.ZUtilsEvents;
import dev.zonary123.zutils.events.models.Chat;
import dev.zonary123.zutils.events.models.Command;
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

  @Override protected void setup() {
    super.setup();
    files();
    events();
    RegionBlockStorage.init(getPath());
    this.getCommandRegistry().registerCommand(new ZUtilsCommand());
  }

  private void files() {
    config = Config.init();
    lang = Lang.init();
  }

  @Override protected void shutdown() {
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
    this.getEventRegistry().registerGlobal(PlayerChatEvent.class, evt -> {
        var playerRef = evt.getSender();
        var message = evt.getContent();
        ASYNC_CONTEXT.runAsync(() -> {
          ZUtilsEvents.CHAT_EVENT.emit(new Chat(
            playerRef,
            message
          ));
          if (message.startsWith("/")) {
            ZUtilsEvents.COMMAND_EVENT.emit(new Command(
              playerRef,
              message
            ));
          }
          return null;
        });
      }
    );
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

