package space.gatt.minestom173worldgen.populator;

import space.gatt.minestom173worldgen.BetaChunk;
import space.gatt.minestom173worldgen.BlockCheck;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class WorldGenGrass extends WorldGenerator{

	private final Block blockToSet;

	public WorldGenGrass(Block block) {
		this.blockToSet = block;
	}

	@Override
	public boolean populate(BetaChunk chunk, Random random, int centerX, int centerY, int centerZ) {

		Block l;
		for (boolean flag = false; (l = chunk.getInstance().getBlock(centerX, centerY, centerZ, Block.Getter.Condition.TYPE)).isAir() || l.namespace().value().contains("_leaves") && centerY > 0; --centerY) {;
		}

		for (int i1 = 0; i1 < 128; ++i1) {
			int j1 = centerX + random.nextInt(8) - random.nextInt(8);
			int k1 = centerY + random.nextInt(4) - random.nextInt(4);
			int l1 = centerZ + random.nextInt(8) - random.nextInt(8);

			Block checkType = chunk.getInstance().getBlock(j1, k1, l1, Block.Getter.Condition.TYPE);
			if (checkType.isAir() && BlockCheck.checkFlowerPlacement(chunk.getInstance(), j1, k1, l1, this.blockToSet)) {
				chunk.getInstance().setBlock(j1, k1, l1, this.blockToSet, false);
			}
		}

		return true;
	}
}
