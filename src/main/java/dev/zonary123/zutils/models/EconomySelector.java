package dev.zonary123.zutils.models;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import dev.zonary123.zutils.api.EconomyAPI;
import dev.zonary123.zutils.utils.economy.Economy;
import dev.zonary123.zutils.utils.economy.EconomyResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

  public CompletableFuture<EconomyResult> getBalance(UUID playerUuid) {
    return EconomyAPI.getBalance(playerUuid, this);
  }

  public CompletableFuture<EconomyResult> deposit(UUID playerUuid, BigDecimal amount, String reason) {
    return EconomyAPI.deposit(playerUuid, this, amount, reason);
  }

  public CompletableFuture<EconomyResult> withdraw(UUID playerUuid, BigDecimal amount, String reason) {
    return EconomyAPI.withdraw(playerUuid, this, amount, reason);
  }

  public CompletableFuture<EconomyResult> transfer(UUID fromPlayer, UUID toPlayer, BigDecimal amount, String reason) {
    return EconomyAPI.transfer(fromPlayer, toPlayer, this, amount, reason);
  }

  public String format(BigDecimal amount) {
    Economy eco = EconomyAPI.getEconomy(economy);
    if (eco == null) return amount.toString();
    return eco.formatCurrency(currency, amount);
  }


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
