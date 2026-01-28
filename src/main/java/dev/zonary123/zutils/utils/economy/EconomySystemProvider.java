package dev.zonary123.zutils.utils.economy;

import com.economy.api.EconomyAPI;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:29
 */
public class EconomySystemProvider extends Economy {
  /**
   * Constructor for ZEconomyProvider.
   *
   * @param economyId ID of the economy
   */
  public EconomySystemProvider(String economyId) {
    super(economyId);
  }

  private EconomyAPI get() {
    return EconomyAPI.getInstance();
  }

  /**
   * Get the balance of a player for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @return the balance of the player in the specified currency
   */
  @Override
  public BigDecimal getBalance(UUID playerId, String currencyId) {
    return BigDecimal.valueOf(get().getBalance(playerId));
  }

  /**
   * Set the balance of a player for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to set
   * @param reason     Reason for setting the balance
   * @return true if the balance was set successfully, false otherwise
   */
  @Override
  public boolean setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    get().setBalance(playerId, amount.doubleValue());
    return true;
  }

  /**
   * Deposit an amount to a player's balance for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to deposit
   * @param reason     Reason for the deposit
   * @return true if the deposit was successful, false otherwise
   */
  @Override
  public boolean deposit(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    get().addBalance(playerId, amount.doubleValue());
    return true;
  }

  /**
   * Withdraw an amount from a player's balance for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to withdraw
   * @param reason     Reason for the withdrawal
   * @return true if the withdrawal was successful, false otherwise
   */
  @Override
  public boolean withdraw(UUID playerId, String currencyId, BigDecimal amount, String reason) {
    return get().removeBalance(playerId, amount.doubleValue());
  }

  /**
   * Check if a player has at least a certain amount of a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to check
   * @return true if the player has at least the specified amount, false otherwise
   */
  @Override
  public boolean hasBalance(UUID playerId, String currencyId, BigDecimal amount) {
    return get().hasBalance(playerId, amount.doubleValue());
  }

  @Override
  public String formatCurrency(String currencyId, BigDecimal amount) {
    return String.format("%s %.2f", currencyId, amount);
  }

  /**
   * Transfer an amount from one player to another for a specific currency.
   *
   * @param fromPlayerId UUID of the player to transfer from
   * @param toPlayerId   UUID of the player to transfer to
   * @param currencyId   ID of the currency
   * @param amount       Amount to transfer
   * @param reason       transaction reason
   * @return {@code true} if the transfer was successful
   */
  @Override
  public boolean transfer(UUID fromPlayerId, UUID toPlayerId, String currencyId, BigDecimal amount, String reason) {
    if (withdraw(fromPlayerId, currencyId, amount, reason)) {
      deposit(toPlayerId, currencyId, amount, reason);
      return true;
    }
    return false;
  }
}
