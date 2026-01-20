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

  public static Config init() {
    Path file = ZUtils.getPath().resolve("config.json");
    Config config = new Config();

    if (file.toFile().exists()) {
      try {
        Config loaded = UtilsFile.read(file, Config.class);
        if (loaded != null) config = loaded;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      UtilsFile.write(file, config);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return config;
  }
}

