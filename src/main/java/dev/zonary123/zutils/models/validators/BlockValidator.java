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

  public boolean isValid(@NonNull String blockId) {
    return ValidatorUtil.match(blockId, blockIds);
  }

  public boolean isValid(@NonNull ItemStack itemStack) {
    String blockId = itemStack.getBlockKey();
    if (blockId == null) return false;
    return isValid(blockId);
  }

  public boolean isValid(@NonNull BlockType blockType) {
    return isValid(blockType.getId());
  }
}
