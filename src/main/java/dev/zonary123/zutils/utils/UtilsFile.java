package dev.zonary123.zutils.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import dev.zonary123.zutils.utils.async.AsyncContext;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * File utility helper with shared Gson instance.
 *
 * <p>
 * Thread-safe, async-safe and crash-safe.
 * </p>
 *
 * @author Carlos Varas Alonso
 * @since 18/01/2026
 */
public final class UtilsFile {

  /* -------------------------------------------------------------------------- */
  /* Async IO Context                                                            */
  /* -------------------------------------------------------------------------- */

  private static final AsyncContext IO_CONTEXT = new AsyncContext(
    "ZUtils-IO",
    2,
    8
  );

  /* -------------------------------------------------------------------------- */
  /* File Locks (per-path)                                                       */
  /* -------------------------------------------------------------------------- */

  private static final ConcurrentMap<Path, ReadWriteLock> FILE_LOCKS =
    new ConcurrentHashMap<>();

  private static ReadWriteLock lock(Path path) {
    return FILE_LOCKS.computeIfAbsent(
      path.toAbsolutePath().normalize(),
      p -> new ReentrantReadWriteLock()
    );
  }

  /* -------------------------------------------------------------------------- */
  /* Gson                                                                        */
  /* -------------------------------------------------------------------------- */

  private static volatile Gson GSON;
  private static final ConcurrentMap<Type, Object> ADAPTERS = new ConcurrentHashMap<>();

  private UtilsFile() {}

  public static void registerAdapter(
    @Nonnull Type type,
    @Nonnull Object adapter
  ) {
    ADAPTERS.put(type, adapter);
    rebuildGson();
  }

  public static void configureGson(Consumer<GsonBuilder> config) {
    synchronized (UtilsFile.class) {
      GsonBuilder builder = new GsonBuilder();
      config.accept(builder);
      ADAPTERS.forEach(builder::registerTypeAdapter);
      GSON = builder.create();
    }
  }

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
  public static <T> T read(@Nonnull Path path, @Nonnull Class<T> clazz)
    throws IOException {

    if (Files.notExists(path)) return null;

    ReadWriteLock lock = lock(path);
    lock.readLock().lock();
    try {
      return getGson().fromJson(
        Files.readString(path, StandardCharsets.UTF_8),
        clazz
      );
    } finally {
      lock.readLock().unlock();
    }
  }

  public static <T> CompletableFuture<@Nullable T> readAsync(
    @Nonnull Path path,
    @Nonnull Class<T> clazz
  ) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return read(path, clazz);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }, IO_CONTEXT.getExecutor());
  }

  @Nullable
  public static <T> List<T> readList(
    @Nonnull Path path,
    @Nonnull Class<T> clazz
  ) throws IOException {

    if (Files.notExists(path)) return null;

    ReadWriteLock lock = lock(path);
    lock.readLock().lock();
    try {
      return getGson().fromJson(
        Files.readString(path, StandardCharsets.UTF_8),
        com.google.gson.reflect.TypeToken
          .getParameterized(List.class, clazz)
          .getType()
      );
    } finally {
      lock.readLock().unlock();
    }
  }

  public static <T> CompletableFuture<@Nullable List<T>> readListAsync(
    @Nonnull Path path,
    @Nonnull Class<T> clazz
  ) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        return readList(path, clazz);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }, IO_CONTEXT.getExecutor());
  }

  /* -------------------------------------------------------------------------- */
  /* JSON Write (atomic + locked)                                                */
  /* -------------------------------------------------------------------------- */

  public static void write(@Nonnull Path path, @Nonnull Object object) throws IOException {

    ReadWriteLock lock = lock(path);
    lock.writeLock().lock();
    try {
      Files.createDirectories(path.getParent());

      Path tmp = path.resolveSibling(path.getFileName() + ".tmp");

      Files.writeString(
        tmp,
        getGson().toJson(object),
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      );

      try {
        Files.move(
          tmp,
          path,
          StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.ATOMIC_MOVE
        );
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(
          tmp,
          path,
          StandardCopyOption.REPLACE_EXISTING
        );
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  public static CompletableFuture<Void> writeAsync(
    @Nonnull Path path,
    @Nonnull Object object
  ) {
    return CompletableFuture.runAsync(() -> {
      try {
        write(path, object);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }, IO_CONTEXT.getExecutor());
  }

  /* -------------------------------------------------------------------------- */
  /* Raw IO                                                                      */
  /* -------------------------------------------------------------------------- */

  @Nonnull
  public static String readString(@Nonnull Path path) throws IOException {
    ReadWriteLock lock = lock(path);
    lock.readLock().lock();
    try {
      return Files.readString(path, StandardCharsets.UTF_8);
    } finally {
      lock.readLock().unlock();
    }
  }

  public static void writeString(
    @Nonnull Path path,
    @Nonnull String content
  ) throws IOException {

    ReadWriteLock lock = lock(path);
    lock.writeLock().lock();
    try {
      Files.createDirectories(path.getParent());

      Path tmp = path.resolveSibling(path.getFileName() + ".tmp");

      Files.writeString(
        tmp,
        content,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
      );

      try {
        Files.move(
          tmp,
          path,
          StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.ATOMIC_MOVE
        );
      } catch (AtomicMoveNotSupportedException e) {
        Files.move(
          tmp,
          path,
          StandardCopyOption.REPLACE_EXISTING
        );
      }
    } finally {
      lock.writeLock().unlock();
    }
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
