package space.gatt.minestom173worldgen;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;
import space.vectrix.flare.fastutil.Long2ObjectSyncMap;

import java.util.HashMap;

import static net.minestom.server.utils.chunk.ChunkUtils.getBlockIndex;

public class GeneratorAssistance {

	private final Long2ObjectSyncMap<Block> blockPositions = Long2ObjectSyncMap.hashmap();

	private void setBlockPoint(Point point, Block block) {
		int blockIndex = getBlockIndex(
				point.blockX(), point.blockY(), point.blockZ()
		);
		blockPositions.put(blockIndex, block);
	}
	private Block getBlockPoint(Point point) {

		int blockIndex = getBlockIndex(
				point.blockX(), point.blockY(), point.blockZ()
		);
		if (!blockPositions.containsKey(blockIndex))
			return Block.AIR;
		return blockPositions.getOrDefault(blockIndex, Block.AIR);
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
