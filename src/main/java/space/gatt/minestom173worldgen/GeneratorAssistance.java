package space.gatt.minestom173worldgen;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

import java.util.HashMap;

public class GeneratorAssistance {

	private final HashMap<String, Block> blockPointMap = new HashMap<>();
	private final HashMap<String, BiomeBase> biomePointMap = new HashMap<>();

	private void setBlockPoint(Point point, Block block) {
		String pointStr = "x:" + point.blockX() + "y:" + point.blockY() + "z:" + point.blockZ();
		blockPointMap.put(pointStr, block);
	}
	private Block getBlockPoint(Point point) {
		String pointStr = "x:" + point.blockX() + "y:" + point.blockY() + "z:" + point.blockZ();
		if (!blockPointMap.containsKey(pointStr))
			return Block.AIR;

		return blockPointMap.getOrDefault(pointStr, Block.AIR);
	}

	public void setBiome(int chunkX, int chunkZ, BiomeBase biome) {
		String pointStr = "x:" + chunkX + "z:" + chunkZ;
		biomePointMap.put(pointStr, biome);
	}
	public BiomeBase getBiome(int chunkX, int chunkZ) {
		String pointStr = "x:" + chunkX + "z:" + chunkZ;
		if (!biomePointMap.containsKey(pointStr))
			return null;

		return biomePointMap.getOrDefault(pointStr, null);
	}

	public GeneratorAssistance() {
	}

	public Block getType(GenerationUnit chunkData, final int x,  final int y,  final int z) {
		Point start = chunkData.absoluteStart();
		Point blockPosition = start.add(x, 0, z).withY(y);
		return getBlockPoint(blockPosition);
	}

	public void setBlock(final GenerationUnit chunkData, final int index, final Block blockData) {

		// index = x << 11 | z << 7 | y where x and z are in [0, 15] and y is in [0, 127]
		final int z = index >> 7 & 0xF;
		final int x = index >> 11 & 0xF;
		final int y = index & 0x7F;

		setBlock(chunkData, x, y, z, blockData);
	}

	public void setBlock(final GenerationUnit chunkData, final int x, int y, final int z, final Block blockData) {
		Point start = chunkData.absoluteStart();
		Point blockPosition = start.add(x, 0, z).withY(y);

		chunkData.modifier().setBlock(blockPosition, blockData);
		setBlockPoint(blockPosition, blockData);
	}

	public Pos getHighestPointYAt(final GenerationUnit chunkData, int x, int z) {
		int y = 0;
		for (int i = 128; i > 0; --i) {
			if (getType(chunkData, x, i, z) != Block.AIR) {
				y = i;
				return new Pos(x, y, z);
			}
		}
		return new Pos(x, y, z);
	}

	public int getHighestPointYAt(final Instance instance, int x, int z) {
		int y = 0;
		for (int i = 128; i > 0; --i) {
			if (instance.getBlock(x, i, z) != Block.AIR) {
				y = i;
				return y + 1;
			}
		}
		return y;
	}
}
