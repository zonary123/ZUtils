package dev.zonary123.zutils.config;

import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.utils.UtilsFile;
import lombok.Data;

import java.nio.file.Path;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 12:15
 */
@Data
public class Config {
  private boolean debug = false;
  private String lang = "en_us";

  public void init() {
    Path path = ZUtils.getPath();
    Path file = path.resolve("config.json");
    Config config = this;
    if (file.toFile().exists()) {
      try {
        config = UtilsFile.read(file, Config.class);
        if (config == null) {
          config = this;
          ZUtils.getLog().atWarning().log(
            "Config file was invalid, using default configuration."
          );
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      ZUtils.getLog().atInfo().log(
        "Config file not found, creating default configuration."
      );
    }
    try {
      UtilsFile.write(file, config);
      ZUtils.get().setConfig(config);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
