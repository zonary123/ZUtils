package dev.zonary123.zutils;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zonary123.zutils.config.Config;
import dev.zonary123.zutils.config.Lang;
import dev.zonary123.zutils.database.blocks.RegionBlockStorage;
import dev.zonary123.zutils.ecs.BlockBreakSystem;
import dev.zonary123.zutils.ecs.BlockPlacedEvent;
import dev.zonary123.zutils.ecs.CraftEvent;
import dev.zonary123.zutils.ecs.InteractPickUp;
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
  private Config config = new Config();
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
  }

  private void files() {
    config.init();
    lang.init();
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
  }


  public static ZUtils get() {
    return instance;
  }

  public static Path getPath() {
    return get().getDataDirectory();
  }

  public static Config getConfig() {
    return get().config;
  }

  public static Lang getLang() {
    return get().lang;
  }

  public static HytaleLogger getLog() {
    return get().getLogger();
  }

}

