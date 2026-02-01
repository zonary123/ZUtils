package dev.zonary123.zutils.utils.economy.providers;

import dev.zonary123.zeconomy.api.ZEconomyApi;
import dev.zonary123.zutils.utils.economy.Economy;
import dev.zonary123.zutils.utils.economy.EconomyResult;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Economy provider for ZEconomy API, async and returning EconomyResult.
 */
public class ZEconomyProvider extends Economy {

  public ZEconomyProvider(String economyId) {
    super(economyId);
  }


  @Override
  public CompletableFuture<EconomyResult> getBalance(UUID playerId, String currencyId) {
    return CompletableFuture.supplyAsync(() -> {
      BigDecimal balance = ZEconomyApi.getBalance(playerId, currencyId);
      return EconomyResult.success(balance, balance, "Retrieved balance");
    });
  }

  @Override
  public CompletableFuture<EconomyResult> setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        BigDecimal before = ZEconomyApi.getBalance(playerId, currencyId);
        boolean success = ZEconomyApi.setBalance(playerId, currencyId, amount, reason);
        if (!success) {
          return EconomyResult.fail("Failed to set balance", before);
        }
        return EconomyResult.success(before, amount, reason);
      } catch (Exception ex) {
        BigDecimal before = ZEconomyApi.getBalance(playerId, currencyId);
        return EconomyResult.fail("Exception setting balance: " + ex.getMessage(), before);
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
        BigDecimal before = ZEconomyApi.getBalance(playerId, currencyId);
        boolean success = ZEconomyApi.deposit(playerId, currencyId, amount, reason);
        BigDecimal after = ZEconomyApi.getBalance(playerId, currencyId);

        if (!success) {
          return EconomyResult.fail("Deposit failed", before);
        }

        return EconomyResult.success(before, after, reason);
      } catch (Exception ex) {
        BigDecimal before = ZEconomyApi.getBalance(playerId, currencyId);
        return EconomyResult.fail("Deposit exception: " + ex.getMessage(), before);
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
        BigDecimal before = ZEconomyApi.getBalance(playerId, currencyId);
        boolean success = ZEconomyApi.withdraw(playerId, currencyId, amount, reason);
        BigDecimal after = ZEconomyApi.getBalance(playerId, currencyId);

        if (!success) {
          return EconomyResult.fail("Insufficient funds", before);
        }

        return EconomyResult.success(before, after, reason);
      } catch (Exception ex) {
        BigDecimal before = ZEconomyApi.getBalance(playerId, currencyId);
        return EconomyResult.fail("Withdrawal exception: " + ex.getMessage(), before);
      }
    });
  }

  @Override
  public CompletableFuture<Boolean> hasBalance(UUID playerId, String currencyId, BigDecimal amount) {
    return CompletableFuture.supplyAsync(() -> ZEconomyApi.getBalance(playerId, currencyId).compareTo(amount) >= 0);
  }

  @Override
  public String formatCurrency(String currencyId, BigDecimal amount) {
    return ZEconomyApi.getCurrency(currencyId).getFormat(amount);
  }
}
