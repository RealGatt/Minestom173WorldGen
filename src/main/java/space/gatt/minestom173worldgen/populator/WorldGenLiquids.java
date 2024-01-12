package space.gatt.minestom173worldgen.populator;

import space.gatt.minestom173worldgen.BetaChunk;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class WorldGenLiquids extends WorldGenerator {

	private final Block blockToSet;

	public WorldGenLiquids(Block block) {
		this.blockToSet = block;
	}

	@Override
	public boolean populate(BetaChunk chunk, Random random, int centerX, int centerY, int centerZ) {
		if (chunk.getInstance().getBlock(centerX, centerY + 1, centerZ, Block.Getter.Condition.TYPE) != Block.STONE) {
			return false;
		} else if (chunk.getInstance().getBlock(centerX, centerY - 1, centerZ, Block.Getter.Condition.TYPE) != Block.STONE) {
			return false;
		} else if (!chunk.getInstance().getBlock(centerX, centerY, centerZ, Block.Getter.Condition.TYPE).isAir()
				&& chunk.getInstance().getBlock(centerX, centerY, centerZ, Block.Getter.Condition.TYPE) != Block.STONE) {
			return false;
		} else {
			int l = 0;

			if (chunk.getInstance().getBlock(centerX - 1, centerY, centerZ) == Block.STONE) {
				++l;
			}

			if (chunk.getInstance().getBlock(centerX + 1, centerY, centerZ) == Block.STONE) {
				++l;
			}

			if (chunk.getInstance().getBlock(centerX, centerY, centerZ - 1) == Block.STONE) {
				++l;
			}

			if (chunk.getInstance().getBlock(centerX, centerY, centerZ + 1) == Block.STONE) {
				++l;
			}

			int i1 = 0;

			if (chunk.getInstance().getBlock(centerX - 1, centerY, centerZ).isAir()) {
				++i1;
			}

			if (chunk.getInstance().getBlock(centerX + 1, centerY, centerZ).isAir()) {
				++i1;
			}

			if (chunk.getInstance().getBlock(centerX, centerY, centerZ - 1).isAir()) {
				++i1;
			}

			if (chunk.getInstance().getBlock(centerX, centerY, centerZ + 1).isAir()) {
				++i1;
			}

			if (l == 3 && i1 == 1) {
				chunk.getInstance().setBlock(centerX, centerY, centerZ, this.blockToSet, true); // maybe one day someone will write a water physics engine
			}

			return true;
		}
	}
}
