package dev.zonary123.zutils.config;

import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.utils.UtilsFile;
import lombok.Data;

import java.nio.file.Path;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 12:09
 */
@Data
public class Lang {
  private String yes = "&a✔";
  private String no = "&c✘";
  private String unknown = "&cUnknown";
  private String nocooldown = "&cNo cooldown";
  // Time
  private String days = "&6%s &adays ";
  private String day = "&6%s &aday ";
  private String hours = "&6%s &ahours ";
  private String hour = "&6%s &ahour ";
  private String minutes = "&6%s &aminutes ";
  private String minute = "&6%s &aminute ";
  private String seconds = "&6%s &aseconds ";
  private String second = "&6%s &asecond";

  public void init() {
    Path path = ZUtils.getPath();
    Path file = path.resolve("lang").resolve(ZUtils.getConfig().getLang() + ".json");
    Lang lang = this;
    if (file.toFile().exists()) {
      try {
        lang = UtilsFile.read(file, Lang.class);
        if (lang == null) lang = this;
        ZUtils.get().setLang(lang);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    try {
      UtilsFile.write(file, lang);
      ZUtils.get().setLang(lang);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
