package dev.zonary123.zutils.models;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EconomySelector {
  public static final BuilderCodec<EconomySelector> CODEC;
  @Builder.Default
  private String economy = "ZEconomy";
  @Builder.Default
  private String currency = "coins";


  static {
    CODEC = BuilderCodec.builder(EconomySelector.class, EconomySelector::new)
      .append(
        new KeyedCodec<>("Economy", Codec.STRING),
        EconomySelector::setEconomy, EconomySelector::getEconomy
      )
      .add()
      .append(
        new KeyedCodec<>("Currency", Codec.STRING),
        EconomySelector::setCurrency, EconomySelector::getCurrency
      )
      .add()
      .build();
  }
}
