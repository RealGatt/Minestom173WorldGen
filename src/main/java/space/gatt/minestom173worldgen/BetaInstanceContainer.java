package space.gatt.minestom173worldgen;

import lombok.Getter;
import lombok.Setter;
import space.gatt.minestom173worldgen.events.ChunkAttemptPopulationEvent;
import space.gatt.minestom173worldgen.overworld.ChunkProviderGenerate;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.instance.InstanceChunkUnloadEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.network.packet.server.play.UnloadChunkPacket;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.utils.async.AsyncUtils;
import net.minestom.server.world.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import space.vectrix.flare.fastutil.Long2ObjectSyncMap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static net.minestom.server.utils.chunk.ChunkUtils.getChunkIndex;
import static net.minestom.server.utils.chunk.ChunkUtils.isLoaded;

public class BetaInstanceContainer extends InstanceContainer {

	private BossBar chunkBossBar;

	@Getter	@Setter
	private ChunkProviderGenerate chunkProviderGenerate;

	private final Map<Long, CompletableFuture<Chunk>> loadingChunks = new ConcurrentHashMap<>();
	private final Long2ObjectSyncMap<Chunk> chunks = Long2ObjectSyncMap.hashmap();

	public BetaInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType) {
		super(uniqueId, dimensionType);
		setChunkSupplier(BetaChunk::new);
		setupBossBar();
	}

	public BetaInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType, @NotNull NamespaceID dimensionName) {
		super(uniqueId, dimensionType, dimensionName);
		setChunkSupplier(BetaChunk::new);
		setupBossBar();
	}

	public BetaInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType, @Nullable IChunkLoader loader) {
		super(uniqueId, dimensionType, loader);
		setChunkSupplier(BetaChunk::new);
		setupBossBar();
	}

	public BetaInstanceContainer(@NotNull UUID uniqueId, @NotNull DimensionType dimensionType, @Nullable IChunkLoader loader, @NotNull NamespaceID dimensionName) {
		super(uniqueId, dimensionType, loader, dimensionName);
		setChunkSupplier(BetaChunk::new);
		setupBossBar();
	}

	protected void setupBossBar() {
		chunkBossBar = BossBar.bossBar(Component.text("Chunk Count: %"),
				1f, BossBar.Color.PINK, BossBar.Overlay.PROGRESS);
	}

	private CompletableFuture<Chunk> loadOrRetrieve(int chunkX, int chunkZ, boolean doPopulate, Supplier<CompletableFuture<Chunk>> supplier) {
		final Chunk chunk = getChunk(chunkX, chunkZ, doPopulate);
		if (chunk != null) {
			if (doPopulate && (chunk instanceof BetaChunk beta && !beta.isPopulated())) {
				EventDispatcher.callCancellable(new ChunkAttemptPopulationEvent(this, chunk), ()->
						((BetaChunk) chunk).doPopulate("loadOrRetrieve @ 80")
				);
			}
			// Chunk already loaded
			return CompletableFuture.completedFuture(chunk);
		}
		return supplier.get();
	}

	@Override
	public @NotNull CompletableFuture<Chunk> loadChunk(int chunkX, int chunkZ) {
		return loadChunk(chunkX, chunkZ, true);
	}

	public @NotNull CompletableFuture<Chunk> loadChunk(int chunkX, int chunkZ, boolean doPopulate) {
		return loadOrRetrieve(chunkX, chunkZ, doPopulate, () -> retrieveChunk(chunkX, chunkZ, doPopulate));
	}

	public @NotNull CompletableFuture<Chunk> loadOptionalChunk(int chunkX, int chunkZ, boolean doPopulate) {
		return loadOrRetrieve(chunkX, chunkZ, doPopulate, () -> hasEnabledAutoChunkLoad() ? retrieveChunk(chunkX, chunkZ, doPopulate) : AsyncUtils.empty());
	}

	@Override
	public @NotNull CompletableFuture<Chunk> loadOptionalChunk(int chunkX, int chunkZ) {
		// if its optional we dont want to populate it randomly
		return loadOptionalChunk(chunkX, chunkZ, true);
	}

	public void loadChunkIfUnloaded(int chunkX, int chunkZ, boolean doPopulate) {
		if (!isChunkLoaded(chunkX, chunkZ)) return;
		loadOrRetrieve(chunkX, chunkZ, doPopulate, () -> retrieveChunk(chunkX, chunkZ, doPopulate));
	}

	private void cacheChunk(@NotNull Chunk chunk) {
		this.chunks.put(getChunkIndex(chunk), chunk);
		var dispatcher = MinecraftServer.process().dispatcher();
		dispatcher.createPartition(chunk);
	}

	@Override
	protected @NotNull CompletableFuture<@NotNull Chunk> retrieveChunk(int chunkX, int chunkZ) {
		return retrieveChunk(chunkX, chunkZ, true);
	}

	public Chunk getChunk(int chunkX, int chunkZ, boolean doPopulate) {
		if (!chunks.containsKey(getChunkIndex(chunkX, chunkZ))) return null;
		Chunk chunk = chunks.get(getChunkIndex(chunkX, chunkZ));
		if (doPopulate && (chunk instanceof BetaChunk beta && !beta.isPopulated())) {
			System.out.println("Got a getChunk that needs population");
			EventDispatcher.callCancellable(new ChunkAttemptPopulationEvent(this, chunk), ()->
					((BetaChunk) chunk).doPopulate("getChunk 130")
			);
		}
		return chunk;
	}

	@Override
	public Chunk getChunk(int chunkX, int chunkZ) {
		return getChunk(chunkX, chunkZ, false);
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return getChunk(chunkX, chunkZ, false) != null;
	}

	@Override
	public boolean isChunkLoaded(Point point) {
		return isChunkLoaded(point.chunkX(), point.chunkZ());
	}

	@Override
	public void tick(long time) {
		super.tick(time);
		MinecraftServer.getBossBarManager().addBossBar(MinecraftServer.getConnectionManager().getOnlinePlayers(), chunkBossBar);
		chunkBossBar.name(Component.text("Chunks in Mem. " + chunks.size()));
	}

	@Override
	public synchronized void unloadChunk(@NotNull Chunk chunk) {
		if (!isLoaded(chunk)) return;
		final int chunkX = chunk.getChunkX();
		final int chunkZ = chunk.getChunkZ();
		chunk.sendPacketToViewers(new UnloadChunkPacket(chunkX, chunkZ));
		EventDispatcher.call(new InstanceChunkUnloadEvent(this, chunk));
		// Remove all entities in chunk
		getEntityTracker().chunkEntities(chunkX, chunkZ, EntityTracker.Target.ENTITIES).forEach(Entity::remove);
		// Clear cache
		this.chunks.remove(getChunkIndex(chunkX, chunkZ));
		((BetaChunk)chunk).unload();
		if (getChunkLoader() != null) {
			getChunkLoader().unloadChunk(chunk);
		}
		var dispatcher = MinecraftServer.process().dispatcher();
		dispatcher.deletePartition(chunk);
	}

	protected @NotNull CompletableFuture<@NotNull Chunk> retrieveChunk(int chunkX, int chunkZ, boolean doPopulate) {
		CompletableFuture<Chunk> completableFuture = new CompletableFuture<>();
		final long index = getChunkIndex(chunkX, chunkZ);
		final CompletableFuture<Chunk> prev = loadingChunks.putIfAbsent(index, completableFuture);

		if (prev != null) return prev;
		final IChunkLoader loader = getChunkLoader();
		final Runnable retriever = () -> loader.loadChunk(this, chunkX, chunkZ)
			.thenCompose(chunk -> {
				if (chunk != null) {
					// Chunk has been loaded from storage
					return CompletableFuture.completedFuture(chunk);
				} else {
					// Loader couldn't load the chunk, generate it
					return createChunk(chunkX, chunkZ);
				}
			})
			// cache the retrieved chunk
			.thenAcceptAsync(chunk -> {

				cacheChunk(chunk);
				((BetaChunk)chunk).onLoad();
				EventDispatcher.call(new InstanceChunkLoadEvent(this, chunk));

				if (doPopulate && !((BetaChunk) chunk).isPopulated()) {
					EventDispatcher.callCancellable(new ChunkAttemptPopulationEvent(this, chunk), ()->
							((BetaChunk) chunk).doPopulate("retrieveChunk")
					);
				}

				final CompletableFuture<Chunk> future = this.loadingChunks.remove(index);
				assert future == completableFuture : "Invalid future: " + future;
				completableFuture.complete(chunk);
			})
			.exceptionally(throwable -> {
				MinecraftServer.getExceptionManager().handleException(throwable);
				return null;
			});
		if (loader.supportsParallelLoading()) {
			CompletableFuture.runAsync(retriever);
		} else {
			retriever.run();
		}
		return completableFuture;
	}
}
