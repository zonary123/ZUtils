package dev.zonary123.zutils;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import dev.zonary123.zutils.utils.async.UtilsAsync;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

public class ZUtils extends JavaPlugin {
  private static ZUtils instance;

  public ZUtils(@NonNull JavaPluginInit init) {
    super(init);
    instance = this;
  }

  @Override protected void setup() {
    super.setup();
  }

  @Override protected void shutdown() {
    super.shutdown();
    UtilsAsync.shutdownAll();
  }

  public static ZUtils get() {
    return instance;
  }

  public static Path getPath() {
    return get().getDataDirectory();
  }

  public static HytaleLogger getLog() {
    return get().getLogger();
  }

}

