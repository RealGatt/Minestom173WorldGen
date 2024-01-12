package space.gatt.minestom173worldgen.populator;

import space.gatt.minestom173worldgen.BetaChunk;
import space.gatt.minestom173worldgen.BlockCheck;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class WorldGenCactus extends WorldGenerator {

	@Override
	public boolean populate(BetaChunk chunk, Random random, int centerX, int centerY, int centerZ) {
		for (int l = 0; l < 10; ++l) {
			int i1 = centerX + random.nextInt(8) - random.nextInt(8);
			int j1 = centerY + random.nextInt(4) - random.nextInt(4);
			int k1 = centerZ + random.nextInt(8) - random.nextInt(8);

			Block checkType = chunk.getInstance().getBlock(i1, j1, k1);

			if (checkType.isAir()) {
				int randomHeight = 1 + random.nextInt(random.nextInt(3) + 1);

				for (int heightModifier = 0; heightModifier < randomHeight; ++heightModifier) {
					if (BlockCheck.checkCactus(chunk.getInstance(), i1, j1 + heightModifier, k1)) {
						chunk.getInstance().setBlock(i1, j1+heightModifier, k1, Block.CACTUS, false);
					}
				}
			}
		}

		return true;
	}
}
