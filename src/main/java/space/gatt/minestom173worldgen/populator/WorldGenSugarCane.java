package space.gatt.minestom173worldgen.populator;

import space.gatt.minestom173worldgen.BetaChunk;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class WorldGenSugarCane extends WorldGenerator {

	@Override
	public boolean populate(BetaChunk chunk, Random random, int centerX, int centerY, int centerZ) {
		for (int l = 0; l < 20; ++l) {
			int i1 = centerX + random.nextInt(4) - random.nextInt(4);
			int j1 = centerY;
			int k1 = centerZ + random.nextInt(4) - random.nextInt(4);

			final Block blockOn = chunk.getInstance().getBlock(i1, centerY, k1, Block.Getter.Condition.TYPE);
			if (!blockOn.isAir()) return false;

			final Block below = chunk.getInstance().getBlock(i1, centerY - 1, k1, Block.Getter.Condition.TYPE);
			boolean canPlace = below == Block.SUGAR_CANE || below == Block.GRASS_BLOCK || below == Block.DIRT || below == Block.SAND;
			if (!canPlace) return false;

			if (chunk.getInstance().getBlock(i1, centerY, k1, Block.Getter.Condition.TYPE).isAir() &&
				chunk.getInstance().getBlock(i1 - 1, centerY - 1, k1, Block.Getter.Condition.TYPE) == Block.WATER ||
				chunk.getInstance().getBlock(i1 + 1, centerY - 1, k1, Block.Getter.Condition.TYPE) == Block.WATER ||
				chunk.getInstance().getBlock(i1, centerY - 1, k1 - 1, Block.Getter.Condition.TYPE) == Block.WATER ||
				chunk.getInstance().getBlock(i1, centerY - 1, k1 + 1, Block.Getter.Condition.TYPE) == Block.WATER) {
				int maxHeightModifier = 2 + random.nextInt(random.nextInt(3) + 1);

				for (int heightModifier = 0; heightModifier < maxHeightModifier; ++heightModifier) {
						chunk.getInstance().setBlock(i1, j1 + heightModifier, k1, Block.SUGAR_CANE, false);
				}
			}
		}

		return true;
	}
}
