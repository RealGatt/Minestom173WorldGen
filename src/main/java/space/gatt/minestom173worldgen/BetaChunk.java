package space.gatt.minestom173worldgen;

import lombok.Getter;
import lombok.Setter;
import net.gatt.minestomholder.entities.EHologram;
import space.gatt.minestom173worldgen.events.ChunkPopulatedEvent;
import space.gatt.minestom173worldgen.events.ChunkUnloadAttemptEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.DynamicChunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.utils.chunk.ChunkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BetaChunk extends DynamicChunk {
	public BetaChunk(@NotNull Instance instance, int chunkX, int chunkZ) {
		super(instance, chunkX, chunkZ);
	}

	private long timeSinceLastPlayer = 0;

	@Setter @Getter
	private boolean populated = false;

	@Setter
	private long markedForPopulation = 0L;

	public boolean isMarkedForPopulation() {
		return System.currentTimeMillis() - markedForPopulation < 1000 * 60;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
	}
	/**
	 * Sets the chunk as "unloaded".
	 */
	@Override
	protected void unload() {
		this.loaded = false;
	}

	@Override
	public boolean addViewer(@NotNull Player player) {
		// this should very rarely be needed. but it is :(
		if (!isPopulated() && !isMarkedForPopulation()) doPopulate("addViewer");
		return super.addViewer(player);
	}

	public void doPopulate() {
		doPopulate("no idea");
	}
	public synchronized void doPopulate(String reason) {
		if (!reason.equalsIgnoreCase("force") && (isMarkedForPopulation() || isPopulated())) return;
		setMarkedForPopulation(System.currentTimeMillis());

		var chunks = new ArrayList<CompletableFuture<Chunk>>();
		ChunkUtils.forChunksInRange(getChunkX(), getChunkZ(), 1, (x, z) ->
				chunks.add(((BetaInstanceContainer)getInstance()).loadChunk(x, z, false))
		);
		CompletableFuture.supplyAsync(() -> {
			CompletableFuture.allOf(chunks.toArray(CompletableFuture[]::new)).join();

			((BetaInstanceContainer)instance).getChunkProviderGenerate().populateChunk(this);

			// effectively invalidate the chunk cache
			super.setBiome(0, 1, 0, super.getBiome(0, 1, 0));

			setPopulated(true);
			setMarkedForPopulation(0);
			EventDispatcher.call(new ChunkPopulatedEvent(this.instance, this));
			return true;
		}).completeOnTimeout(false, 60, TimeUnit.SECONDS)
			.thenAcceptAsync((v)->{
				if (!isPopulated() && !v) {
					// it failed... LETS RERUN!
					setMarkedForPopulation(0);
					this.doPopulate("retry attempt");
					System.out.println("Population failed for Chunk. Timeout. Rerunning!");
				}
		}).handle((res, ex)->{
			ex.printStackTrace();// it failed... LETS RERUN!
			setMarkedForPopulation(0);
			this.doPopulate("retry attempt");
			System.out.println("Population failed for Chunk. Error. Rerunning!");
			return null;
		});
	}

	private EHologram debugHologram = null;

	@Override
	public void tick(long time) {
		if (debugHologram == null) {
			debugHologram = new EHologram(
				new Pos(
						(double) (getChunkX() * 16) - 8,
						100,
						(double) (getChunkZ() * 16) - 8
				),
				getInstance(),
				Component.text(this.getViewers().size())
			);
			debugHologram.setFollowPlayers(true);
		} else {
			if (!this.getViewers().isEmpty()) {
				if (!debugHologram.isSpawned()) debugHologram.spawn();
			} else {
				debugHologram.remove();
				debugHologram = null;
				return;
			}

			if (isPopulated()) {
				debugHologram.setLine(Component.text("POPULATED").color(NamedTextColor.GREEN));
			} else if (isMarkedForPopulation() && !isPopulated()) {
				debugHologram.setLine(Component.text("MARKED").color(NamedTextColor.YELLOW));
			} else {
				debugHologram.setLine(Component.text("SAD FACE").color(NamedTextColor.RED));
			}
		}
		if (!getViewers().isEmpty() && !isPopulated() && !isMarkedForPopulation() && isLoaded()) {
			doPopulate();
		}
		super.tick(time);
		if (getViewers().isEmpty() && isPopulated() && isLoaded() && !isMarkedForPopulation()) {
			timeSinceLastPlayer++;
			if (timeSinceLastPlayer > 20*60) { // 30 seconds
				// fire an ChunkUnloadAttemptEvent
				EventDispatcher.callCancellable(new ChunkUnloadAttemptEvent(this.instance, this), ()->{
					getInstance().unloadChunk(this);
				});
			}
		} else {
			timeSinceLastPlayer = 0;
		}
	}
}
