package dev.zonary123.zutils.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Carlos Varas Alonso - 20/01/2026 11:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseConfig {

  @Builder.Default
  private DatabaseType type = DatabaseType.JSON;
  @Builder.Default
  private String url = "localhost";
  @Builder.Default
  private String database = "zutils";
  @Builder.Default
  private String username = "root";
  @Builder.Default
  private String password = "password";

  public enum DatabaseType {
    JSON,
    SQL,
    MONGODB
  }

}
