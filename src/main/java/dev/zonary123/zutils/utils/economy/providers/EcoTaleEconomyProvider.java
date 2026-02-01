package dev.zonary123.zutils.utils.economy.providers;

import com.ecotale.api.EcotaleAPI;
import dev.zonary123.zutils.utils.economy.Economy;
import dev.zonary123.zutils.utils.economy.EconomyResult;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Economy provider for EcoTale API, async and returning EconomyResult.
 */
public class EcoTaleEconomyProvider extends Economy {

  public EcoTaleEconomyProvider(String economyId) {
    super(economyId);
  }

  @Override
  public CompletableFuture<EconomyResult> getBalance(UUID playerId, String currencyId) {
    return CompletableFuture.supplyAsync(() -> {
      double balance = EcotaleAPI.getBalance(playerId);
      BigDecimal bal = BigDecimal.valueOf(balance);
      return EconomyResult.success(bal, bal, "Retrieved balance");
    });
  }

  @Override
  public CompletableFuture<EconomyResult> setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        double before = EcotaleAPI.getBalance(playerId);
        EcotaleAPI.setBalance(playerId, amount.doubleValue(), reason);
        return EconomyResult.success(BigDecimal.valueOf(before), amount, reason);
      } catch (Exception ex) {
        double before = EcotaleAPI.getBalance(playerId);
        return EconomyResult.fail("Failed to set balance: " + ex.getMessage(), BigDecimal.valueOf(before));
      }
    });
  }

  @Override
  public CompletableFuture<EconomyResult> deposit(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    if (amount == null || amount.signum() <= 0) {
      return getBalance(playerId, currencyId)
        .thenApply(balance -> EconomyResult.fail("Invalid deposit amount", balance.getBefore()));
    }

    return CompletableFuture.supplyAsync(() -> {
      try {
        double before = EcotaleAPI.getBalance(playerId);
        boolean success = EcotaleAPI.deposit(playerId, amount.doubleValue(), reason);
        double after = EcotaleAPI.getBalance(playerId);

        if (!success) {
          return EconomyResult.fail("Deposit failed", BigDecimal.valueOf(before));
        }

        return EconomyResult.success(BigDecimal.valueOf(before), BigDecimal.valueOf(after), reason);
      } catch (Exception ex) {
        double before = EcotaleAPI.getBalance(playerId);
        return EconomyResult.fail("Deposit exception: " + ex.getMessage(), BigDecimal.valueOf(before));
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
        double before = EcotaleAPI.getBalance(playerId);
        boolean success = EcotaleAPI.withdraw(playerId, amount.doubleValue(), reason);
        double after = EcotaleAPI.getBalance(playerId);

        if (!success) {
          return EconomyResult.fail("Insufficient funds", BigDecimal.valueOf(before));
        }

        return EconomyResult.success(BigDecimal.valueOf(before), BigDecimal.valueOf(after), reason);
      } catch (Exception ex) {
        double before = EcotaleAPI.getBalance(playerId);
        return EconomyResult.fail("Withdrawal exception: " + ex.getMessage(), BigDecimal.valueOf(before));
      }
    });
  }

  @Override
  public CompletableFuture<Boolean> hasBalance(UUID playerId, String currencyId, BigDecimal amount) {
    return CompletableFuture.supplyAsync(() -> EcotaleAPI.getBalance(playerId) >= amount.doubleValue());
  }

  @Override
  public String formatCurrency(String currencyId, BigDecimal amount) {
    return EcotaleAPI.format(amount.doubleValue());
  }
}
