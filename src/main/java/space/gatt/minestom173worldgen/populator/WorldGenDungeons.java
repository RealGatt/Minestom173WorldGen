package space.gatt.minestom173worldgen.populator;

import space.gatt.minestom173worldgen.BetaChunk;
import space.gatt.minestom173worldgen.BlockCheck;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import space.gatt.minestom173worldgen.blockhandlers.ChestHandler;

import java.util.Random;

public class WorldGenDungeons  extends WorldGenerator {
	@Override
	public boolean populate(BetaChunk chunk, Random random, int centerX, int centerY, int centerZ) {
		byte b0 = 3;
		int l = random.nextInt(2) + 2;
		int i1 = random.nextInt(2) + 2;
		int j1 = 0;

		int k1;
		int l1;
		int i2;

		for (k1 = centerX - l - 1; k1 <= centerX + l + 1; ++k1) {
			for (l1 = centerY - 1; l1 <= centerY + b0 + 1; ++l1) {
				for (i2 = centerZ - i1 - 1; i2 <= centerZ + i1 + 1; ++i2) {
					Block material = chunk.getInstance().getBlock(k1, l1, i2);

					if (l1 == centerY - 1 && !BlockCheck.isBuildableMaterial(material)) {
						return false;
					}

					if (l1 == centerY + b0 + 1 && BlockCheck.isBuildableMaterial(material)) {
						return false;
					}

					if ((k1 == centerX - l - 1 ||
							k1 == centerX + l + 1 ||
							i2 == centerZ - i1 - 1 ||
							i2 == centerZ + i1 + 1)
							&& l1 == centerY &&
							chunk.getInstance().getBlock(k1, l1, i2, Block.Getter.Condition.TYPE).isAir() &&
							chunk.getInstance().getBlock(k1, l1 + 1, i2, Block.Getter.Condition.TYPE).isAir()) {
						++j1;
					}
				}
			}
		}

		if (j1 >= 1 && j1 <= 5) {
			for (k1 = centerX - l - 1; k1 <= centerX + l + 1; ++k1) {
				for (l1 = centerY + b0; l1 >= centerY - 1; --l1) {
					for (i2 = centerZ - i1 - 1; i2 <= centerZ + i1 + 1; ++i2) {
						if (k1 != centerX - l - 1 && l1 != centerY - 1 && i2 != centerZ - i1 - 1 && k1 != centerX + l + 1 && l1 != centerY + b0 + 1 && i2 != centerZ + i1 + 1) {
							chunk.getInstance().setBlock(k1, l1, i2, Block.AIR);
						} else if (l1 >= 0 && !BlockCheck.isBuildableMaterial(chunk.getInstance().getBlock(k1, l1 - 1, i2, Block.Getter.Condition.TYPE))) {
							chunk.getInstance().setBlock(k1, l1, i2, Block.AIR);
						} else if (BlockCheck.isBuildableMaterial(chunk.getInstance().getBlock(k1, l1, i2, Block.Getter.Condition.TYPE))) {
							if (l1 == centerY - 1 && random.nextInt(4) != 0) {
								chunk.getInstance().setBlock(k1, l1, i2, Block.MOSSY_COBBLESTONE);
							} else {
								chunk.getInstance().setBlock(k1, l1, i2, Block.COBBLESTONE);
							}
						}
					}
				}
			}

			k1 = 0;

			while (k1 < 2) {
				l1 = 0;

				while (true) {
					if (l1 < 3) {
						chestBreak: {
							i2 = centerX + random.nextInt(l * 2 + 1) - l;
							int j2 = centerZ + random.nextInt(i1 * 2 + 1) - i1;

							if (chunk.getInstance().getBlock(i2, centerY, j2, Block.Getter.Condition.TYPE).isAir()) {
								int k2 = 0;

								if (BlockCheck.isBuildableMaterial(chunk.getInstance().getBlock(i2 - 1, centerY, j2, Block.Getter.Condition.TYPE))) {
									++k2;
								}

								if (BlockCheck.isBuildableMaterial(chunk.getInstance().getBlock(i2 + 1, centerY, j2, Block.Getter.Condition.TYPE))) {
									++k2;
								}

								if (BlockCheck.isBuildableMaterial(chunk.getInstance().getBlock(i2, centerY, j2 - 1, Block.Getter.Condition.TYPE))) {
									++k2;
								}

								if (BlockCheck.isBuildableMaterial(chunk.getInstance().getBlock(i2, centerY, j2+1, Block.Getter.Condition.TYPE))) {
									++k2;
								}

								if (k2 == 1) {

									ItemStack[] itemStacks = new ItemStack[27];
									for (int l2 = 0; l2 < 8; ++l2) {
										ItemStack itemstack = this.getRandomItem(random);
										if (itemstack != null) {
											itemStacks[random.nextInt(27)] = itemstack;
										}
									}
//
									chunk.getInstance().setBlock(i2, centerY, j2, Block.CHEST.
											withHandler(new ChestHandler(itemStacks)));
									break chestBreak;
								}
							}

							++l1;
							continue;
						}
					}

					++k1;
					break;
				}
			}


			chunk.getInstance().setBlock(centerX, centerY, centerZ, Block.SPAWNER, true);
			// set the spawner stuff here

			return true;
		} else {
			return false;
		}
	}

	private ItemStack getRandomItem(Random random) {
		int i = random.nextInt(11);

		return i == 0 ? ItemStack.builder(Material.SADDLE).build() :
			(i == 1 ? ItemStack.builder(Material.IRON_INGOT).amount(random.nextInt(4) + 1).build() :
			(i == 2 ? ItemStack.builder(Material.BREAD).build() :
			(i == 3 ? ItemStack.builder(Material.WHEAT).amount(random.nextInt(4) + 1).build() :
			(i == 4 ? ItemStack.builder(Material.GUNPOWDER).amount(random.nextInt(4) + 1).build() :
			(i == 5 ? ItemStack.builder(Material.STRING).amount(random.nextInt(4) + 1).build() :
			(i == 6 ? ItemStack.builder(Material.BUCKET).build() :
			(i == 7 && random.nextInt(100) == 0 ? ItemStack.builder(Material.GOLDEN_APPLE).build() :
			(i == 8 && random.nextInt(2) == 0 ? ItemStack.builder(Material.REDSTONE).amount(random.nextInt(4) + 1).build() :
			(i == 9 && random.nextInt(10) == 0 ?
			ItemStack.builder(random.nextInt(2) == 0 ? Material.MUSIC_DISC_13 : Material.MUSIC_DISC_CAT).build() : (i == 10
			? ItemStack.builder(Material.COCOA_BEANS).build() : null))))))))));
	}

	private EntityType getRandomSpawnerType(Random random) {
		return switch (random.nextInt(7)) {
			case 0 -> EntityType.SKELETON;
			case 1, 2 -> EntityType.ZOMBIE;
			case 3, 4 -> EntityType.SPIDER;
			case 5 -> EntityType.CREEPER; // tee
			case 6 -> EntityType.ENDERMAN; // hee
			default -> throw new IllegalStateException("Shouldn't happen");
		};
	}
}
