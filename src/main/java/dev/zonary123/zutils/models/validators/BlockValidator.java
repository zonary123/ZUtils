package dev.zonary123.zutils.models.validators;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 11:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockValidator {
  private Set<String> blockIds = new HashSet<>(
    Set.of("*")
  );

  /**
   * Check if the given block ID is valid according to the validator's criteria.
   *
   * @param blockId The block ID to validate.
   * @return True if the block ID is valid, false otherwise.
   */
  public boolean isValid(@NonNull String blockId) {
    return ValidatorUtil.match(blockId, blockIds);
  }

  /**
   * Check if the given ItemStack is valid according to the validator's criteria.
   *
   * @param itemStack The ItemStack to validate.
   * @return True if the ItemStack is valid, false otherwise.
   */
  public boolean isValid(@NonNull ItemStack itemStack) {
    String blockId = itemStack.getBlockKey();
    if (blockId == null) return false;
    return isValid(blockId);
  }

  /**
   * Check if the given BlockType is valid according to the validator's criteria.
   *
   * @param blockType The BlockType to validate.
   * @return True if the BlockType is valid, false otherwise.
   */
  public boolean isValid(@NonNull BlockType blockType) {
    String blockId = blockType.getId();
    try {
      var item = blockType.getItem();
      if (item == null) return isValid(blockId);
      var categories = item.getCategories();
      if (categories != null) {
        for (String category : categories) {
          if (ValidatorUtil.match(category, blockIds)) return true;
        }
      }
      return isValid(blockId);
    } catch (Exception e) {
      return isValid(blockId);
    }
  }
}
