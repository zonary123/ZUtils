package dev.zonary123.zutils;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zonary123.zutils.adapters.AtomicReferenceAdapter;
import dev.zonary123.zutils.commands.ZUtilsCommand;
import dev.zonary123.zutils.config.Config;
import dev.zonary123.zutils.config.Lang;
import dev.zonary123.zutils.database.blocks.RegionBlockStorage;
import dev.zonary123.zutils.ecs.*;
import dev.zonary123.zutils.models.DurationValue;
import dev.zonary123.zutils.utils.UtilsFile;
import dev.zonary123.zutils.utils.async.AsyncContext;
import dev.zonary123.zutils.utils.async.UtilsAsync;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

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
      UtilsFile.registerAdapter(AtomicReference.class, AtomicReferenceAdapter.INSTANCE);
      UtilsFile.registerAdapter(DurationValue.class, DurationValue.INSTANCE);
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
    this.getEntityStoreRegistry().registerSystem(new UseBlockECS());
    this.getEntityStoreRegistry().registerSystem(new TravelSystem());
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

