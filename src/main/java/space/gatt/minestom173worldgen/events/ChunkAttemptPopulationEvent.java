package space.gatt.minestom173worldgen.events;

import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public class ChunkAttemptPopulationEvent implements InstanceEvent, CancellableEvent {

	private final Instance instance;
	private final Chunk chunk;
	private boolean cancelled;

	public ChunkAttemptPopulationEvent(@NotNull Instance instance, @NotNull Chunk chunk) {
		this.instance = instance;
		this.chunk = chunk;
		this.cancelled = false;
	}

	@Override
	public @NotNull Instance getInstance() {
		return instance;
	}

	/**
	 * Gets the chunk X.
	 *
	 * @return the chunk X
	 */
	public int getChunkX() {
		return chunk.getChunkX();
	}

	/**
	 * Gets the chunk Z.
	 *
	 * @return the chunk Z
	 */
	public int getChunkZ() {
		return chunk.getChunkZ();
	}

	/**
	 * Gets the chunk.
	 *
	 * @return the chunk.
	 */
	public @NotNull Chunk getChunk() {
		return chunk;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}