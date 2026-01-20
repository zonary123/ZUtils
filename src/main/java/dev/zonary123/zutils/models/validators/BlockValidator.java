package dev.zonary123.zutils.models.validators;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  public boolean isValid(String blockId) {
    if (blockId == null) return false;
    return blockId.isEmpty() || blockIds.contains(blockId) || blockIds.contains("*");
  }

  public boolean isValid(ItemStack itemStack) {
    if (itemStack == null) return false;
    return isValid(itemStack.getBlockKey());
  }

  public boolean isValid(BlockType blockType) {
    if (blockType == null) return false;
    return isValid(blockType.getId());
  }
}
