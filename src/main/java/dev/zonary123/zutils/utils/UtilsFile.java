package dev.zonary123.zutils.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * File utility helper with shared Gson instance.
 *
 * <p>
 * Allows external modules to register {@link TypeAdapter}s dynamically
 * without overriding existing adapters.
 * </p>
 * <p>
 * Thread-safe.
 *
 * @author Carlos Varas Alonso
 * @since 18/01/2026
 */
public final class UtilsFile {

  private static volatile Gson GSON;

  private static final ConcurrentMap<Type, Object> ADAPTERS = new ConcurrentHashMap<>();

  private UtilsFile() {
  }

  /* -------------------------------------------------------------------------- */
  /* Gson / Adapters                                                             */
  /* -------------------------------------------------------------------------- */

  /**
   * Registers a {@link TypeAdapter} for a specific type.
   * <p>
   * If a Gson instance was already created, it will be rebuilt automatically.
   * </p>
   *
   * @param type    target type
   * @param adapter gson adapter (TypeAdapter, JsonSerializer or JsonDeserializer)
   */
  public static void registerAdapter(
    @Nonnull Type type,
    @Nonnull Object adapter
  ) {
    ADAPTERS.put(type, adapter);
    rebuildGson();
  }

  /**
   * Configures the shared {@link Gson} instance.
   * <p>
   * This will override any previous configuration and rebuild the Gson instance.
   * </p>
   *
   * @param config consumer to configure the GsonBuilder
   */
  public static void configureGson(Consumer<GsonBuilder> config) {
    synchronized (UtilsFile.class) {
      GsonBuilder builder = new GsonBuilder();
      config.accept(builder);
      ADAPTERS.forEach(builder::registerTypeAdapter);
      GSON = builder.create();
    }
  }

  /**
   * Returns the shared {@link Gson} instance.
   *
   * @return gson instance
   */
  @Nonnull
  public static Gson getGson() {
    Gson local = GSON;
    if (local == null) {
      synchronized (UtilsFile.class) {
        if (GSON == null) {
          rebuildGson();
        }
        local = GSON;
      }
    }
    return local;
  }

  private static void rebuildGson() {
    synchronized (UtilsFile.class) {
      GsonBuilder builder = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping();

      ADAPTERS.forEach(builder::registerTypeAdapter);

      GSON = builder.create();
    }
  }


  /* -------------------------------------------------------------------------- */
  /* JSON Read                                                                   */
  /* -------------------------------------------------------------------------- */

  @Nullable
  public static <T> T read(@Nonnull Path path, @Nonnull Class<T> clazz) throws IOException {
    if (Files.notExists(path)) return null;
    return getGson().fromJson(Files.readString(path, StandardCharsets.UTF_8), clazz);
  }

  @Nullable
  public static <T> List<T> readList(@Nonnull Path path, @Nonnull Class<T> clazz) throws IOException {
    if (Files.notExists(path)) return null;
    return getGson().fromJson(
      Files.readString(path, StandardCharsets.UTF_8),
      com.google.gson.reflect.TypeToken.getParameterized(List.class, clazz).getType()
    );
  }

  /* -------------------------------------------------------------------------- */
  /* JSON Write                                                                  */
  /* -------------------------------------------------------------------------- */

  public static void write(@Nonnull Path path, @Nonnull Object object) throws IOException {
    Files.createDirectories(path.getParent());
    Files.writeString(
      path,
      getGson().toJson(object),
      StandardCharsets.UTF_8,
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    );
  }

  /* -------------------------------------------------------------------------- */
  /* Raw IO                                                                      */
  /* -------------------------------------------------------------------------- */

  @Nonnull
  public static String readString(@Nonnull Path path) throws IOException {
    return Files.readString(path, StandardCharsets.UTF_8);
  }

  public static void writeString(@Nonnull Path path, @Nonnull String content) throws IOException {
    Files.createDirectories(path.getParent());
    Files.writeString(
      path,
      content,
      StandardCharsets.UTF_8,
      StandardOpenOption.CREATE,
      StandardOpenOption.TRUNCATE_EXISTING
    );
  }

  /* -------------------------------------------------------------------------- */
  /* File Info                                                                   */
  /* -------------------------------------------------------------------------- */

  public static boolean exists(@Nonnull Path path) {
    return Files.exists(path);
  }

  public static long size(@Nonnull Path path) throws IOException {
    return Files.size(path);
  }

  public static Stream<Path> list(@Nonnull Path directory) throws IOException {
    return Files.list(directory);
  }
}
