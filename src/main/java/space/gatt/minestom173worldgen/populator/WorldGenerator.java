package space.gatt.minestom173worldgen.populator;

import space.gatt.minestom173worldgen.BetaChunk;

import java.util.Random;

public abstract class WorldGenerator {

	public WorldGenerator() {}

	public boolean populate(BetaChunk chunk, Random random, int centerX, int centerY, int centerZ) {
		return false;
	}


	public void scale(double scaleX, double scaleY, double scaleZ) {}
}
