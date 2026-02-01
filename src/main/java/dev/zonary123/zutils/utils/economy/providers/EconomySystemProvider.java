package dev.zonary123.zutils.utils.economy.providers;

import com.economy.api.EconomyAPI;
import dev.zonary123.zutils.utils.economy.Economy;
import dev.zonary123.zutils.utils.economy.EconomyResult;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of Economy using EconomyAPI.
 * Provides safe async operations returning EconomyResult.
 */
public class EconomySystemProvider extends Economy {

  public EconomySystemProvider(String economyId) {
    super(economyId);
  }

  private EconomyAPI get() {
    return EconomyAPI.getInstance();
  }

  @Override
  public CompletableFuture<EconomyResult> getBalance(UUID playerId, String currencyId) {
    return CompletableFuture.supplyAsync(() -> {
      double balance = get().getBalance(playerId);
      BigDecimal bal = BigDecimal.valueOf(balance);
      return EconomyResult.success(bal, bal, "Retrieved balance");
    });
  }

  @Override
  public CompletableFuture<EconomyResult> setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        double before = get().getBalance(playerId);
        get().setBalance(playerId, amount.doubleValue());
        return EconomyResult.success(BigDecimal.valueOf(before), amount, reason);
      } catch (Exception ex) {
        double before = get().getBalance(playerId);
        return EconomyResult.fail("Failed to set balance: " + ex.getMessage(), BigDecimal.valueOf(before));
      }
    });
  }

  @Override
  public CompletableFuture<EconomyResult> deposit(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    if (amount == null || amount.signum() <= 0) return getBalance(playerId, currencyId)
      .thenApply(balance -> EconomyResult.fail("Invalid deposit amount", balance.getBefore()));

    return CompletableFuture.supplyAsync(() -> {
      try {
        double before = get().getBalance(playerId);
        get().addBalance(playerId, amount.doubleValue());
        double after = get().getBalance(playerId);
        return EconomyResult.success(BigDecimal.valueOf(before), BigDecimal.valueOf(after), reason);
      } catch (Exception ex) {
        double before = get().getBalance(playerId);
        return EconomyResult.fail("Deposit failed: " + ex.getMessage(), BigDecimal.valueOf(before));
      }
    });
  }

  @Override
  public CompletableFuture<EconomyResult> withdraw(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    if (amount == null || amount.signum() <= 0) {
      return getBalance(playerId, currencyId)
        .thenApply(balance -> EconomyResult.fail("Invalid withdraw amount", balance.getBefore()));
    }

    return CompletableFuture.supplyAsync(() -> {
      try {
        double before = get().getBalance(playerId);
        boolean success = get().removeBalance(playerId, amount.doubleValue());
        double after = get().getBalance(playerId);

        if (!success) {
          return EconomyResult.fail("Insufficient funds", BigDecimal.valueOf(before));
        }

        return EconomyResult.success(BigDecimal.valueOf(before), BigDecimal.valueOf(after), reason);
      } catch (Exception ex) {
        double before = get().getBalance(playerId);
        return EconomyResult.fail("Withdrawal failed: " + ex.getMessage(), BigDecimal.valueOf(before));
      }
    });
  }

  @Override
  public CompletableFuture<Boolean> hasBalance(UUID playerId, String currencyId, BigDecimal amount) {
    return CompletableFuture.supplyAsync(() -> get().hasBalance(playerId, amount.doubleValue()));
  }

  @Override
  public String formatCurrency(String currencyId, BigDecimal amount) {
    return String.format("%s %.2f", currencyId, amount);
  }
}
