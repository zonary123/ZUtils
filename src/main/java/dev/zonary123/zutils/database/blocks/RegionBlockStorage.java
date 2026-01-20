package dev.zonary123.zutils.database.blocks;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import dev.zonary123.zutils.utils.async.AsyncContext;
import dev.zonary123.zutils.utils.async.UtilsAsync;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class RegionBlockStorage {
  private static final AsyncContext IO_CONTEXT = UtilsAsync.getContext("ZUtils-RegionBlock-IO");

  private static final int REGION_SIZE = 32;

  private static Path storageDir;

  private static final Cache<String, RegionData> REGION_CACHE = Caffeine.newBuilder()
    .expireAfterAccess(5, TimeUnit.MINUTES)
    .maximumSize(10_000)
    .removalListener((String key, RegionData data, RemovalCause cause) -> {
      if (data.isDirty()) saveAsync(key, data);
    })
    .build();

  // ========================
  // INIT
  // ========================

  public static void init(Path baseDir) {
    storageDir = baseDir.resolve("region_blocks");
    storageDir.toFile().mkdirs();
  }

  // ========================
  // PUBLIC API
  // ========================

  public static void markPlaced(World world, WorldChunk worldChunk, Vector3i pos) {
    getRegion(world, worldChunk).add(pack(pos));
  }

  public static boolean removePlaced(World world, WorldChunk worldChunk, Vector3i pos) {
    return getRegion(world, worldChunk).remove(pack(pos));
  }

  public static boolean isPlaced(World world, WorldChunk worldChunk, Vector3i pos) {
    return getRegion(world, worldChunk).contains(pack(pos));
  }

  public static void shutdown() {
    REGION_CACHE.asMap().forEach(RegionBlockStorage::saveSync);
  }

  // ========================
  // REGION ACCESS
  // ========================

  private static RegionData getRegion(World world, WorldChunk worldChunk) {
    String key = key(world, worldChunk);

    return REGION_CACHE.get(key, k -> {
      RegionData data = new RegionData();
      loadAsync(world, worldChunk, data);
      return data;
    });
  }

  private static String key(World world, WorldChunk worldChunk) {
    return sanitize(world) + "_" + worldChunk.getX() + "_" + worldChunk.getZ();
  }

  private static String sanitize(World world) {
    return world.getName();
  }

  // ========================
  // IO
  // ========================

  private static void loadAsync(World world, WorldChunk worldChunk, RegionData target) {
    IO_CONTEXT.runAsync(() -> {
      RegionData loaded = loadSync(world, worldChunk);
      target.mergeFrom(loaded);
      return null;
    });
  }

  private static RegionData loadSync(World world, WorldChunk worldChunk) {
    File file = regionFile(world, worldChunk);
    RegionData data = new RegionData();

    if (!file.exists()) return data;

    try (DataInputStream in = new DataInputStream(
      new GZIPInputStream(new FileInputStream(file))
    )) {
      int size = in.readInt();
      for (int i = 0; i < size; i++) {
        data.add(in.readLong());
      }
      data.clearDirty();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return data;
  }

  private static void saveAsync(String key, RegionData data) {
    UtilsAsync.getContext("ZUtils-RegionBlock").runAsync(() -> {
      saveSync(key, data);
      return null;
    });
  }

  private static void saveSync(String key, RegionData data) {
    if (!data.isDirty()) return;

    try {
      String[] split = key.split("_");
      String world = split[0];
      int rx = Integer.parseInt(split[1]);
      int rz = Integer.parseInt(split[2]);

      File worldDir = storageDir.resolve(world).toFile();
      worldDir.mkdirs();

      File file = new File(worldDir, "region_" + rx + "_" + rz + ".dat");

      try (DataOutputStream out = new DataOutputStream(
        new GZIPOutputStream(new FileOutputStream(file))
      )) {
        LongOpenHashSet blocks = data.blocks;
        out.writeInt(blocks.size());
        for (long l : blocks) {
          out.writeLong(l);
        }
      }

      data.clearDirty();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static File regionFile(World world, WorldChunk pos) {
    File worldDir = storageDir.resolve(sanitize(world)).toFile();
    worldDir.mkdirs();

    return new File(
      worldDir,
      "region_" + pos.getX() + "_" + pos.getZ() + ".dat"
    );
  }

  // ========================
  // POSITION PACKING
  // ========================

  private static long pack(Vector3i v) {
    return (((long) v.x & 0x3FFFFFF) << 38)
      | (((long) v.y & 0xFFF) << 26)
      | ((long) v.z & 0x3FFFFFF);
  }

  // ========================
  // DATA CLASS
  // ========================

  private static final class RegionData {
    private final LongOpenHashSet blocks = new LongOpenHashSet();
    private volatile boolean dirty;

    void add(long pos) {
      if (blocks.add(pos)) dirty = true;
    }

    boolean remove(long pos) {
      boolean removed = blocks.remove(pos);
      if (removed) dirty = true;
      return removed;
    }

    boolean contains(long pos) {
      return blocks.contains(pos);
    }

    void mergeFrom(RegionData other) {
      if (blocks.addAll(other.blocks)) dirty = true;
    }

    boolean isDirty() {
      return dirty;
    }

    void clearDirty() {
      dirty = false;
    }
  }
}
