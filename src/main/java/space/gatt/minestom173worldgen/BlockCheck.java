package space.gatt.minestom173worldgen;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.HashSet;
import java.util.Set;

public class BlockCheck {
	private static Set<Block> NON_BUILDABLE_MATERIALS;

	static {
		NON_BUILDABLE_MATERIALS = new HashSet<>();
		for (Block b : Block.values()) {
			if (b.namespace().value().endsWith("_sapling") ||
					b.namespace().value().endsWith("_button") ||
					b.namespace().value().endsWith("rail") ||
					b.namespace().value().endsWith("torch")
			)
				NON_BUILDABLE_MATERIALS.add(b);

		}
		NON_BUILDABLE_MATERIALS.add(Block.AIR);
		NON_BUILDABLE_MATERIALS.add(Block.CAVE_AIR);
		NON_BUILDABLE_MATERIALS.add(Block.VOID_AIR);
		NON_BUILDABLE_MATERIALS.add(Block.FIRE);
		NON_BUILDABLE_MATERIALS.add(Block.WATER);
		NON_BUILDABLE_MATERIALS.add(Block.LAVA);
		NON_BUILDABLE_MATERIALS.add(Block.NETHER_PORTAL);
		NON_BUILDABLE_MATERIALS.add(Block.SNOW);
		NON_BUILDABLE_MATERIALS.add(Block.REPEATER);
		NON_BUILDABLE_MATERIALS.add(Block.LADDER);
		NON_BUILDABLE_MATERIALS.add(Block.LEVER);
		NON_BUILDABLE_MATERIALS.add(Block.RAIL);
		NON_BUILDABLE_MATERIALS.add(Block.REDSTONE_WIRE);
		NON_BUILDABLE_MATERIALS.add(Block.SUGAR_CANE);
		NON_BUILDABLE_MATERIALS.add(Block.RED_MUSHROOM);
		NON_BUILDABLE_MATERIALS.add(Block.BROWN_MUSHROOM);
		NON_BUILDABLE_MATERIALS.add(Block.DEAD_BUSH);
		NON_BUILDABLE_MATERIALS.add(Block.SHORT_GRASS);
		NON_BUILDABLE_MATERIALS.add(Block.FERN);
		NON_BUILDABLE_MATERIALS.add(Block.WHEAT);
	}

	public static boolean isBuildableMaterial(final Block block) {
		return !NON_BUILDABLE_MATERIALS.contains(block);
	}

	private static final Set<Block> NOT_TREE_BLOCKABLE;

	static {
		NOT_TREE_BLOCKABLE = new HashSet<>();

		for (Block b : Block.values()) {
			if (b.namespace().value().endsWith("_sapling") ||
					b.namespace().value().endsWith("_button") ||
					b.namespace().value().endsWith("rail") ||
					b.namespace().value().endsWith("torch")
			)
				NON_BUILDABLE_MATERIALS.add(b);

		}

		NOT_TREE_BLOCKABLE.add(Block.CACTUS);
		NOT_TREE_BLOCKABLE.add(Block.CAKE);
		NOT_TREE_BLOCKABLE.add(Block.REPEATER);
		NOT_TREE_BLOCKABLE.add(Block.FIRE);
		NOT_TREE_BLOCKABLE.add(Block.WHEAT);
		NOT_TREE_BLOCKABLE.add(Block.DEAD_BUSH);
		NOT_TREE_BLOCKABLE.add(Block.SHORT_GRASS);
		NOT_TREE_BLOCKABLE.add(Block.FERN);
		NOT_TREE_BLOCKABLE.add(Block.RED_MUSHROOM);
		NOT_TREE_BLOCKABLE.add(Block.BROWN_MUSHROOM);
		NOT_TREE_BLOCKABLE.add(Block.WATER);
		NOT_TREE_BLOCKABLE.add(Block.LAVA);
		NOT_TREE_BLOCKABLE.add(Block.LADDER);
		NOT_TREE_BLOCKABLE.add(Block.LEVER);
		NOT_TREE_BLOCKABLE.add(Block.PISTON);
		NOT_TREE_BLOCKABLE.add(Block.STICKY_PISTON);
		NOT_TREE_BLOCKABLE.add(Block.PISTON_HEAD);
		NOT_TREE_BLOCKABLE.add(Block.MOVING_PISTON);
		NOT_TREE_BLOCKABLE.add(Block.NETHER_PORTAL);
		NOT_TREE_BLOCKABLE.add(Block.REDSTONE_WIRE);
		NOT_TREE_BLOCKABLE.add(Block.SUGAR_CANE);
		NOT_TREE_BLOCKABLE.add(Block.SNOW); // TODO SNOW_BLOCK?
		NOT_TREE_BLOCKABLE.add(Block.FARMLAND);
		NOT_TREE_BLOCKABLE.add(Block.COBWEB);

		// new additions
		NOT_TREE_BLOCKABLE.add(Block.GLASS);
		NOT_TREE_BLOCKABLE.add(Block.ICE);
		NOT_TREE_BLOCKABLE.add(Block.SPAWNER);
	}

	public static boolean isSomething(final Block material) {
		// As there is no block with air, mojang opted to use a boolean array to avoid NPE... And to decide that air should return false
		return !material.isAir() && isInTreeBlockable(material);
	}

	public static boolean isInTreeBlockable(final Block material) {
		return !NOT_TREE_BLOCKABLE.contains(material);
	}

	public static boolean checkCactus(final Instance world, final int x, final int y, final int z) {
		if (isBuildableMaterial(world.getBlock(x - 1, y, z, Block.Getter.Condition.TYPE))) {
			return false;
		} else if (isBuildableMaterial(world.getBlock(x + 1, y, z, Block.Getter.Condition.TYPE))) {
			return false;
		} else if (isBuildableMaterial(world.getBlock(x, y, z - 1, Block.Getter.Condition.TYPE))) {
			return false;
		} else if (isBuildableMaterial(world.getBlock(x, y, z + 1, Block.Getter.Condition.TYPE))) {
			return false;
		} else {
			Block l = world.getBlock(x, y - 1, z, Block.Getter.Condition.TYPE);

			return l == Block.CACTUS || l == Block.SAND || l == Block.RED_SAND;
		}
	}

	public static boolean isFlowerPlaceableOntop(final Block type, final Block blockPlacedOn) {
		if (type == Block.WHEAT) {
			return blockPlacedOn == Block.FARMLAND;
		}
		if (type == Block.DEAD_BUSH) {
			return blockPlacedOn == Block.SAND || blockPlacedOn == Block.RED_SAND;
		}
		if (type == Block.RED_MUSHROOM || type == Block.BROWN_MUSHROOM) {
			return isSomething(blockPlacedOn);
		}

		return blockPlacedOn == Block.GRASS_BLOCK || blockPlacedOn == Block.DIRT || blockPlacedOn == Block.FARMLAND;
	}

	public static boolean checkFlowerPlacement(final Instance world, final int x, final int y, final int z, final Block type) {
		if (type == Block.RED_MUSHROOM || type == Block.BROWN_MUSHROOM) {
			// This should also have a Light-Level check, however cbs.

			return y >= 0 && isFlowerPlaceableOntop(type, world.getBlock(x, y - 1, z, Block.Getter.Condition.TYPE));
		}

		// default
		// we can always assume the chunk is loaded when calling
		final Block material = world.getBlock(x, y - 1, z);
		return isFlowerPlaceableOntop(type, material);
	}
}
