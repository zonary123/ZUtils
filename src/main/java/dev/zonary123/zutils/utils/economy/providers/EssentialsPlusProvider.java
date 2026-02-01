package dev.zonary123.zutils.utils.economy.providers;

import de.fof1092.essentialsplus.economy.EconomyAPI;
import dev.zonary123.zutils.utils.economy.Economy;
import dev.zonary123.zutils.utils.economy.EconomyResult;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EssentialsPlusProvider extends Economy {

  public EssentialsPlusProvider(String economyId) {
    super(economyId);
  }

  @Override
  public CompletableFuture<EconomyResult> getBalance(UUID playerId, String currencyId) {
    return EconomyAPI.getBalance(playerId)
      .thenApply(balance -> {
        BigDecimal bal = BigDecimal.valueOf(balance);
        return EconomyResult.success(bal, bal, "Retrieved balance");
      });
  }

  @Override
  public CompletableFuture<EconomyResult> setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return getBalance(playerId, currencyId)
      .thenCompose(before -> EconomyAPI.setBalance(playerId, amount.doubleValue(), reason)
        .thenApply(v -> EconomyResult.success(before.getBefore(), amount, reason))
        .exceptionally(ex -> EconomyResult.fail("Failed to set balance: " + ex.getMessage(), before.getBefore()))
      );
  }

  @Override
  public CompletableFuture<EconomyResult> deposit(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return getBalance(playerId, currencyId)
      .thenCompose(before -> EconomyAPI.increaseBalance(playerId, amount.doubleValue(), reason)
        .thenCompose(v -> getBalance(playerId, currencyId)
          .thenApply(after -> EconomyResult.success(before.getBefore(), after.getAfter(), reason))
        )
        .exceptionally(ex -> EconomyResult.fail("Failed to deposit: " + ex.getMessage(), before.getBefore()))
      );
  }

  @Override
  public CompletableFuture<EconomyResult> withdraw(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return getBalance(playerId, currencyId)
      .thenCompose(before -> EconomyAPI.decreaseBalance(playerId, amount.doubleValue(), reason)
        .thenCompose(v -> getBalance(playerId, currencyId)
          .thenApply(after -> EconomyResult.success(before.getBefore(), after.getAfter(), reason))
        )
        .exceptionally(ex -> EconomyResult.fail("Failed to withdraw: " + ex.getMessage(), before.getBefore()))
      );
  }

  @Override
  public CompletableFuture<Boolean> hasBalance(UUID playerId, String currencyId, BigDecimal amount) {
    return EconomyAPI.hasBalance(playerId, amount.doubleValue());
  }

  @Override
  public String formatCurrency(String currencyId, BigDecimal amount) {
    return EconomyAPI.formatCurrency(amount.doubleValue());
  }
}
