package space.gatt.minestom173worldgen;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.generator.GenerationUnit;

import java.util.Random;

public class MapGenBase {
	protected final int offset = 8;
	protected final Random random = new Random();
	protected final long seed;

	public MapGenBase(long seed) {
		this.seed = seed;
	}

	public void generate(Instance instance, int cx, int cz, GenerationUnit chunkData) {
		int k = this.offset;

		this.random.setSeed(seed);
		long l = this.random.nextLong() / 2L * 2L + 1L;
		long i1 = this.random.nextLong() / 2L * 2L + 1L;

		for (int j1 = cx - k; j1 <= cx + k; ++j1) {
			for (int k1 = cz - k; k1 <= cz + k; ++k1) {
				this.random.setSeed((long) j1 * l + (long) k1 * i1 ^ seed);
				this.generate(instance, j1, k1, cx, cz, chunkData);
			}
		}
	}

	protected void generate(Instance instance, int i, int j, int k, int l, GenerationUnit chunkData) {}
}
