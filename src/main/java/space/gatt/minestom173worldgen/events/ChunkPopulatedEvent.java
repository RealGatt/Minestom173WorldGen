package space.gatt.minestom173worldgen.events;

import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

public class ChunkPopulatedEvent implements InstanceEvent {

	private final Instance instance;
	private final Chunk chunk;

	public ChunkPopulatedEvent(@NotNull Instance instance, @NotNull Chunk chunk) {
		this.instance = instance;
		this.chunk = chunk;
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
}