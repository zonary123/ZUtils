package dev.zonary123.zutils.config;

import dev.zonary123.zutils.ZUtils;
import dev.zonary123.zutils.api.RewardAPI;
import dev.zonary123.zutils.models.rewards.AdvancedRewards;
import dev.zonary123.zutils.utils.UtilsFile;

import java.nio.file.Path;

public class AdvancedRewardsTemplate {
  public static void register() {
    Path path = ZUtils.getPath().resolve("advanced_rewards_templates");
    if (!path.toFile().exists()) {
      path.toFile().mkdirs();
      try {
        AdvancedRewards example = new AdvancedRewards();
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
          AdvancedRewards advancedRewards = UtilsFile.read(file, AdvancedRewards.class);
          if (advancedRewards == null) {
            ZUtils.getLog().atWarning().log("AdvancedRewards template in file '%s' is null. Skipping.".formatted(file.getFileName()));
            continue;
          }
          if (advancedRewards.getId().isBlank()) {
            ZUtils.getLog().atWarning().log("AdvancedRewards template in file '%s' has no ID defined. Skipping.".formatted(file.getFileName()));
            continue;
          }
          RewardAPI.registerAdvancedRewardsTemplate(advancedRewards);
          ZUtils.getLog().atInfo().log("Registered AdvancedRewards template with ID '%s' from file '%s'.".formatted(advancedRewards.getId(), file.getFileName()));
        } catch (Exception e) {
          ZUtils.getLog().atWarning().log("Failed to load AdvancedRewards template from file '%s': %s".formatted(file.getFileName(), e.getMessage()));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
