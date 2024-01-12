package space.gatt.minestom173worldgen.populator;

import space.gatt.minestom173worldgen.BetaChunk;
import space.gatt.minestom173worldgen.BlockCheck;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class WorldGenForest extends WorldGenerator{

	@Override
	public boolean populate(BetaChunk chunk, Random random, int centerX, int centerY, int centerZ) {
		int l = random.nextInt(3) + 4;
		boolean flag = true;
//		System.out.println("Placing tree at " + centerX + " " + centerY + " " + centerZ);

		if (centerY >= 1 && centerY + l + 1 <= 128) {
			int i1;
			int j1;
			int k1;
			int l1;
			Block type;

			for (i1 = centerY; i1 <= centerY + 1 + l; ++i1) {
				byte b0 = 1;

				if (i1 == centerY) {
					b0 = 0;
				}

				if (i1 >= centerY + 1 + l - 2) {
					b0 = 2;
				}

				for (j1 = centerX - b0; j1 <= centerX + b0 && flag; ++j1) {
					for (k1 = centerZ - b0; k1 <= centerZ + b0 && flag; ++k1) {
						if (i1 >= 0 && i1 < 128) {
							type = chunk.getInstance().getBlock(j1, i1, k1, Block.Getter.Condition.TYPE);
							if (!type.isAir() && !type.namespace().value().contains("_leaves")) {
								return false;
							}
						} else {
							flag = false;
							return false;
						}
					}
				}
			}

			type = chunk.getInstance().getBlock(centerX, centerY - 1, centerZ, Block.Getter.Condition.TYPE);
			if ((type == Block.GRASS_BLOCK || type == Block.DIRT) && centerY < 128 - l - 1) {
				chunk.getInstance().setBlock(centerX, centerY - 1, centerZ, Block.DIRT, true);

				int i2;

				for (i2 = centerY - 3 + l; i2 <= centerY + l; ++i2) {
					j1 = i2 - (centerY + l);
					k1 = 1 - j1 / 2;

					for (l1 = centerX - k1; l1 <= centerX + k1; ++l1) {
						int j2 = l1 - centerX;

						for (int k2 = centerZ - k1; k2 <= centerZ + k1; ++k2) {
							int l2 = k2 - centerZ;

							Block typeCheck = chunk.getInstance().getBlock(l1, i2, k2, Block.Getter.Condition.TYPE);
							if ((Math.abs(j2) != k1 || Math.abs(l2) != k1 || random.nextInt(2) != 0 && j1 != 0)
									&& !BlockCheck.isSomething(typeCheck)) {
								chunk.getInstance().setBlock(l1, i2, k2, Block.BIRCH_LEAVES, true);
							}
						}
					}
				}

				for (i2 = 0; i2 < l; ++i2) {
					type = chunk.getInstance().getBlock(centerX, centerY + i2, centerZ, Block.Getter.Condition.TYPE);
					if (type.isAir() || type.namespace().value().contains("_leaves")) {
						chunk.getInstance().setBlock(centerX, centerY + i2, centerZ, Block.BIRCH_LOG, true);
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
