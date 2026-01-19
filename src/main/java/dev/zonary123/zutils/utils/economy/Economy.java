package dev.zonary123.zutils.utils.economy;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * @author Carlos Varas Alonso - 18/01/2026 7:17
 */
@Data
public abstract class Economy {
  /**
   * ID of the economy
   */
  private final String economyId;

  /**
   * Constructor for Economy.
   *
   * @param economyId ID of the economy
   */
  protected Economy(String economyId) {
    this.economyId = economyId;
  }

  /**
   * Get the balance of a player for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   *
   * @return the balance of the player in the specified currency
   */
  public abstract BigDecimal getBalance(UUID playerId, String currencyId);

  /**
   * Set the balance of a player for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to set
   * @param reason     Reason for setting the balance
   *
   * @return true if the balance was set successfully, false otherwise
   */
  public abstract boolean setBalance(UUID playerId, String currencyId, BigDecimal amount, String reason);

  /**
   * Deposit an amount to a player's balance for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to deposit
   * @param reason     Reason for the deposit
   *
   * @return true if the deposit was successful, false otherwise
   */
  public abstract boolean deposit(UUID playerId, String currencyId, BigDecimal amount, String reason);

  /**
   * Withdraw an amount from a player's balance for a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to withdraw
   * @param reason     Reason for the withdrawal
   *
   * @return true if the withdrawal was successful, false otherwise
   */
  public abstract boolean withdraw(UUID playerId, String currencyId, BigDecimal amount, String reason);

  /**
   * Check if a player has at least a certain amount of a specific currency.
   *
   * @param playerId   UUID of the player
   * @param currencyId ID of the currency
   * @param amount     Amount to check
   *
   * @return true if the player has at least the specified amount, false otherwise
   */
  public abstract boolean hasBalance(UUID playerId, String currencyId, BigDecimal amount);

  /**
   * Transfer an amount from one player to another for a specific currency.
   *
   * @param fromPlayerId UUID of the player to transfer from
   * @param toPlayerId   UUID of the player to transfer to
   * @param currencyId   ID of the currency
   * @param amount       Amount to transfer
   * @param reason       Reason for the transfer
   *
   * @return true if the transfer was successful, false otherwise
   */
  public abstract boolean transfer(
    UUID fromPlayerId,
    UUID toPlayerId,
    String currencyId,
    BigDecimal amount,
    String reason
  );
}
