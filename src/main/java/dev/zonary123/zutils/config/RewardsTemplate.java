package dev.zonary123.zutils.config;

import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.api.RewardAPI;
import dev.zonary123.zutils.models.rewards.Reward;
import dev.zonary123.zutils.utils.UtilsFile;

import java.nio.file.Path;

public class RewardsTemplate {
  public static void register() {
    Path path = ZUtils.getPath().resolve("rewards_templates");
    if (!path.toFile().exists()) {
      path.toFile().mkdirs();
      try {
        Reward example = new Reward();
        example.setId("example");
        UtilsFile.write(path.resolve("example.json"), example);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      var files = UtilsFile.getAllJsonFiles(path);
      for (Path file : files) {
        try {
          Reward reward = UtilsFile.read(file, Reward.class);
          if (reward == null) {
            ZUtils.getLog().atWarning().log("Reward template in file '%s' is null. Skipping.".formatted(file.getFileName()));
            continue;
          }
          if (reward.getId().isBlank()) {
            ZUtils.getLog().atWarning().log("Reward template in file '%s' has no ID defined. Skipping.".formatted(file.getFileName()));
            continue;
          }
          RewardAPI.registerRewardsTemplate(reward);
          ZUtils.getLog().atInfo().log("Registered Reward template with ID '%s' from file '%s'.".formatted(reward.getId(), file.getFileName()));
        } catch (Exception e) {
          ZUtils.getLog().atWarning().log("Failed to load Reward template from file '%s': %s".formatted(file.getFileName(), e.getMessage()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
