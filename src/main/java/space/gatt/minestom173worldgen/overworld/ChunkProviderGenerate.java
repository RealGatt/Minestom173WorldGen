package space.gatt.minestom173worldgen.overworld;

import space.gatt.minestom173worldgen.*;
import space.gatt.minestom173worldgen.noise.NoiseGeneratorOctaves;
import space.gatt.minestom173worldgen.populator.*;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.generator.GenerationUnit;

import java.util.Random;

public class ChunkProviderGenerate {

	private final Random random;

	private final NoiseGeneratorOctaves terrainNoise2Generator;
	private final NoiseGeneratorOctaves terrainNoise3Generator;
	private final NoiseGeneratorOctaves terrainNoise1Generator;
	private final NoiseGeneratorOctaves sandAndGravelNoiseGenerator;
	private final NoiseGeneratorOctaves stoneNoiseGenerator;
	private final NoiseGeneratorOctaves terrainNoise4Generator;
	private final NoiseGeneratorOctaves terrainNoise5Generator;
	private final NoiseGeneratorOctaves treeCountNoise;

	private final WorldChunkManager worldChunkManager;

	private double[] terrainNoise;
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	private double[] stoneNoise = new double[256];
	private double[] terrainNoise1;
	private double[] terrainNoise2;
	private double[] terrainNoise3;
	private double[] terrainNoise4;
	private double[] terrainNoise5;
	private double[] snowNoise;

	private final MapGenBase caveGenerator;

	private BiomeBase[] biomeNoiseCache;
	private final long seed;
	private final Instance instance;
	private final GeneratorAssistance generatorAssistance;

	public ChunkProviderGenerate(Instance instance, long seed) {
		generatorAssistance = new GeneratorAssistance();
		this.caveGenerator = new MapGenCaves(seed, generatorAssistance);
		this.instance = instance;
		this.seed = seed;
		this.worldChunkManager = new WorldChunkManager(seed);
		this.random = new Random(seed);
		this.terrainNoise2Generator = new NoiseGeneratorOctaves(this.random, 16);
		this.terrainNoise3Generator = new NoiseGeneratorOctaves(this.random, 16);
		this.terrainNoise1Generator = new NoiseGeneratorOctaves(this.random, 8);
		this.sandAndGravelNoiseGenerator = new NoiseGeneratorOctaves(this.random, 4);
		this.stoneNoiseGenerator = new NoiseGeneratorOctaves(this.random, 4);
		this.terrainNoise4Generator = new NoiseGeneratorOctaves(this.random, 10);
		this.terrainNoise5Generator = new NoiseGeneratorOctaves(this.random, 16);
		this.treeCountNoise = new NoiseGeneratorOctaves(this.random, 8);
	}

	public void generateBareTerrain(int chunkX, int chunkZ, GenerationUnit chunkData, double[] temperatures) {
		byte sectionCount = 4;
		byte b1 = 64;
		int xLen = sectionCount + 1;
		byte yLen = 17;
		int l = sectionCount + 1;

		this.terrainNoise = this.generateTerrainNoise(this.terrainNoise, chunkX * sectionCount, chunkZ * sectionCount, xLen, yLen, l);

		for (int section1 = 0; section1 < sectionCount; ++section1) {
			for (int section2 = 0; section2 < sectionCount; ++section2) {
				for (int blockPosition = 0; blockPosition < 16; ++blockPosition) {
					double d0 = 0.125D;
					double d1 = this.terrainNoise[((section1) * l + section2) * yLen + blockPosition];
					double d2 = this.terrainNoise[((section1) * l + section2 + 1) * yLen + blockPosition];
					double d3 = this.terrainNoise[((section1 + 1) * l + section2) * yLen + blockPosition];
					double d4 = this.terrainNoise[((section1 + 1) * l + section2 + 1) * yLen + blockPosition];
					double d5 = (this.terrainNoise[((section1) * l + section2) * yLen + blockPosition + 1] - d1) * d0;
					double d6 = (this.terrainNoise[((section1) * l + section2 + 1) * yLen + blockPosition + 1] - d2) * d0;
					double d7 = (this.terrainNoise[((section1 + 1) * l + section2) * yLen + blockPosition + 1] - d3) * d0;
					double d8 = (this.terrainNoise[((section1 + 1) * l + section2 + 1) * yLen + blockPosition + 1] - d4) * d0;

					for (int l1 = 0; l1 < 8; ++l1) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;

						for (int i2 = 0; i2 < 4; ++i2) {
							int index = i2 + section1 * 4 << 11 | section2 * 4 << 7 | blockPosition * 8 + l1;

							// index = x << 11 | z << 7 | y where x and z are in [0, 15] and y is in [0, 127]

							short dimensionHeight = (short) instance.getDimensionType().getMaxY();
							double d14 = 0.25D;
							double d15 = d10;
							double d16 = (d11 - d10) * d14;

							for (int k2 = 0; k2 < 4; ++k2) {
								double d17 = temperatures[(section1 * 4 + i2) * 16 + section2 * 4 + k2];
								Block blockData = Block.AIR;

								if (blockPosition * 8 + l1 < b1) {
									if (d17 < 0.5D && blockPosition * 8 + l1 >= b1 - 1) {
										blockData = Block.ICE;
									} else {
										blockData = Block.WATER;
									}
								}

								if (d15 > 0.0D) {
									blockData = Block.STONE;
								}

								generatorAssistance.setBlock(chunkData, index, blockData);
								index += dimensionHeight;
								d15 += d16;
							}

							d10 += d12;
							d11 += d13;
						}

						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}

	// turns base terrain into the biome dependent terrain
	public void generateBiomeTerrain(int chunkX, int chunkZ, GenerationUnit chunkData, BiomeBase[] biomeCache) {
		byte b0 = 64;
		double d0 = 0.03125D;

		this.sandNoise = this.sandAndGravelNoiseGenerator.generateNoise(this.sandNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 0.0D, 16, 16, 1, d0, d0, 1.0D);
		this.gravelNoise = this.sandAndGravelNoiseGenerator.generateNoise(this.gravelNoise, (double) (chunkX * 16), 109.0134D, (double) (chunkZ * 16), 16, 1, 16, d0, 1.0D, d0);
		this.stoneNoise = this.stoneNoiseGenerator.generateNoise(this.stoneNoise, (double) (chunkX * 16), (double) (chunkZ * 16), 0.0D, 16, 16, 1, d0 * 2.0D, d0 * 2.0D, d0 * 2.0D);

		for (int chunkBlockZ = 0; chunkBlockZ < 16; ++chunkBlockZ) { // l
			for (int chunkBlockX = 0; chunkBlockX < 16; ++chunkBlockX) { // k

				BiomeBase biomebase = biomeCache[chunkBlockZ + chunkBlockX * 16];
				boolean isSand = this.sandNoise[chunkBlockZ + chunkBlockX * 16] + this.random.nextDouble() * 0.2D > 0.0D;
				boolean isGravel = this.gravelNoise[chunkBlockZ + chunkBlockX * 16] + this.random.nextDouble() * 0.2D > 3.0D;
				int stoneHeight = (int) (this.stoneNoise[chunkBlockZ + chunkBlockX * 16] / 3.0D + 3.0D + this.random.nextDouble() * 0.25D);

				int topBlock = -1;
				Block topBlockType = biomebase.top;
				Block bottomBlockType = biomebase.bottom;

				for (int chunkBlockY = 127; chunkBlockY >= 0; --chunkBlockY) {
					if (chunkBlockY <= this.random.nextInt(5)) {
						generatorAssistance.setBlock(chunkData, chunkBlockX, chunkBlockY, chunkBlockZ, Block.BEDROCK);
					} else {
						Block b3 = generatorAssistance.getType(chunkData, chunkBlockX, chunkBlockY, chunkBlockZ);
						if (b3.isAir()) {
							topBlock = -1;
						} else if (b3 == Block.STONE) {
							if (topBlock == -1) {
								if (stoneHeight <= 0) {
									topBlockType = Block.AIR;
									bottomBlockType = Block.STONE;
								} else if (chunkBlockY >= b0 - 4 && chunkBlockY <= b0 + 1) {
									topBlockType = biomebase.top;
									bottomBlockType = biomebase.bottom;

									if (isGravel)
										bottomBlockType = Block.GRAVEL;


									if (isSand) {
										topBlockType = Block.SAND;
										bottomBlockType = Block.SAND;
									}
								}

								if (chunkBlockY < b0 && topBlockType == Block.AIR) {
									topBlockType = Block.WATER;
								}

								topBlock = stoneHeight;

								if (chunkBlockY >= b0 - 1) {
									generatorAssistance.setBlock(chunkData, chunkBlockX, chunkBlockY, chunkBlockZ, topBlockType);

								} else {
									generatorAssistance.setBlock(chunkData, chunkBlockX, chunkBlockY, chunkBlockZ, bottomBlockType);
								}

							} else if (topBlock > 0) {
								--topBlock;
								generatorAssistance.setBlock(chunkData, chunkBlockX, chunkBlockY, chunkBlockZ, bottomBlockType);
								if (topBlock == 0 && bottomBlockType == Block.SAND) {
									topBlock = this.random.nextInt(4);
									bottomBlockType = Block.SANDSTONE;
								}
							}
						}
					}
				}
			}
		}
	}

	private double[] generateTerrainNoise(double[] noise, int fromX, int fromZ, int xLen, int yLen, int zLen) {
		if (noise == null) {
			noise = new double[xLen * yLen * zLen];
		}

		double d0 = 684.412D;
		double d1 = 684.412D;
		double[] adouble1 = this.worldChunkManager.temperature;
		double[] adouble2 = this.worldChunkManager.rain;

		this.terrainNoise4 = this.terrainNoise4Generator.generateNoise(this.terrainNoise4, fromX, fromZ, xLen, zLen, 1.121D, 1.121D, 0.5D);
		this.terrainNoise5 = this.terrainNoise5Generator.generateNoise(this.terrainNoise5, fromX, fromZ, xLen, zLen, 200.0D, 200.0D, 0.5D);
		this.terrainNoise1 = this.terrainNoise1Generator.generateNoise(this.terrainNoise1, (double) fromX, (double) 0, (double) fromZ, xLen, yLen, zLen, d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
		this.terrainNoise2 = this.terrainNoise2Generator.generateNoise(this.terrainNoise2, (double) fromX, (double) 0, (double) fromZ, xLen, yLen, zLen, d0, d1, d0);
		this.terrainNoise3 = this.terrainNoise3Generator.generateNoise(this.terrainNoise3, (double) fromX, (double) 0, (double) fromZ, xLen, yLen, zLen, d0, d1, d0);
		int k1 = 0;
		int l1 = 0;
		int i2 = 16 / xLen;

		for (int j2 = 0; j2 < xLen; ++j2) {
			int k2 = j2 * i2 + i2 / 2;

			for (int l2 = 0; l2 < zLen; ++l2) {
				int i3 = l2 * i2 + i2 / 2;
				double d2 = adouble1[k2 * 16 + i3];
				double d3 = adouble2[k2 * 16 + i3] * d2;
				double d4 = 1.0D - d3;

				d4 *= d4;
				d4 *= d4;
				d4 = 1.0D - d4;
				double d5 = (this.terrainNoise4[l1] + 256.0D) / 512.0D;

				d5 *= d4;
				if (d5 > 1.0D) {
					d5 = 1.0D;
				}

				double d6 = this.terrainNoise5[l1] / 8000.0D;

				if (d6 < 0.0D) {
					d6 = -d6 * 0.3D;
				}

				d6 = d6 * 3.0D - 2.0D;
				if (d6 < 0.0D) {
					d6 /= 2.0D;
					if (d6 < -1.0D) {
						d6 = -1.0D;
					}

					d6 /= 1.4D;
					d6 /= 2.0D;
					d5 = 0.0D;
				} else {
					if (d6 > 1.0D) {
						d6 = 1.0D;
					}

					d6 /= 8.0D;
				}

				if (d5 < 0.0D) {
					d5 = 0.0D;
				}

				d5 += 0.5D;
				d6 = d6 * (double) yLen / 16.0D;
				double d7 = (double) yLen / 2.0D + d6 * 4.0D;

				++l1;

				for (int j3 = 0; j3 < yLen; ++j3) {
					double d8 = 0.0D;
					double d9 = ((double) j3 - d7) * 12.0D / d5;

					if (d9 < 0.0D) {
						d9 *= 4.0D;
					}

					double d10 = this.terrainNoise2[k1] / 512.0D;
					double d11 = this.terrainNoise3[k1] / 512.0D;
					double d12 = (this.terrainNoise1[k1] / 10.0D + 1.0D) / 2.0D;

					if (d12 < 0.0D) {
						d8 = d10;
					} else if (d12 > 1.0D) {
						d8 = d11;
					} else {
						d8 = d10 + (d11 - d10) * d12;
					}

					d8 -= d9;
					if (j3 > yLen - 4) {
						double d13 = (double) ((float) (j3 - (yLen - 4)) / 3.0F);

						d8 = d8 * (1.0D - d13) + -10.0D * d13;
					}

					noise[k1] = d8;
					++k1;
				}
			}
		}

		return noise;
	}

	public synchronized void generateUnpopulatedChunkData(GenerationUnit chunkData, int chunkX, int chunkZ) {
		Point start = chunkData.absoluteStart();
		this.random.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		this.biomeNoiseCache = this.worldChunkManager.getBiomeNoise(this.biomeNoiseCache, chunkX * 16, chunkZ * 16, 16, 16);
		for (int x = 0; x <= 15; ++x) {
			for (int z = 0; z <= 15; ++z) {
				BiomeBase b = this.biomeNoiseCache[x | (z << 4)];
				for (int y = 0 ; y < instance.getDimensionType().getMaxY(); ++y) {
					chunkData.modifier().setBiome(
							start.add(x, 0, z).withY(y), b.biome
					);
				}
			}
		}

		this.generateBareTerrain(chunkX, chunkZ, chunkData, this.worldChunkManager.temperature);
		this.generateBiomeTerrain(chunkX, chunkZ, chunkData, this.biomeNoiseCache);
		this.caveGenerator.generate(this.instance, chunkX, chunkZ, chunkData);
	}

	public synchronized void populateChunk(BetaChunk chunk) {
		int chunkX = chunk.getChunkX();
		int chunkZ = chunk.getChunkZ();

		int blockX = chunkX * 16;
		int blockZ = chunkZ * 16;

		BiomeBase biomebase = this.worldChunkManager.getBiome(blockX + 16, blockZ + 16);

		this.random.setSeed(this.seed);
		long i1 = this.random.nextLong() / 2L * 2L + 1L;
		long j1 = this.random.nextLong() / 2L * 2L + 1L;

		this.random.setSeed((long) chunkX * i1 + (long) chunkZ * j1 ^ this.seed);
		double d0 = 0.25D;
		int centerX;
		int centerY;
		int centerZ;

		if (this.random.nextInt(4) == 0) {
			centerX = blockX + this.random.nextInt(16)+ 8;
			centerY = this.random.nextInt(128);
			centerZ = blockZ + this.random.nextInt(16)+ 8;
			(new WorldGenLakes(Block.WATER)).populate(chunk, this.random, centerX, centerY, centerZ);
		}

		if (this.random.nextInt(8) == 0) {
			centerX = blockX + this.random.nextInt(16)+ 8;
			centerY = this.random.nextInt(this.random.nextInt(120) + 8);
			centerZ = blockZ + this.random.nextInt(16)+ 8;
			if (centerY < 64 || this.random.nextInt(10) == 0) {
				(new WorldGenLakes(Block.LAVA)).populate(chunk, this.random, centerX, centerY, centerZ);
			}
		}

		int j2;

		for (centerX = 0; centerX < 8; ++centerX) {
			centerY = blockX + this.random.nextInt(16) + 8;
			centerZ = this.random.nextInt(128);
			j2 = blockZ + this.random.nextInt(16) + 8;
			(new WorldGenDungeons()).populate(chunk, this.random, centerY, centerZ, j2); //sometimes y isn't y...
		}
//
		for (centerX = 0; centerX < 10; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(128);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenClay(32)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 20; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(128);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.DIRT, 32)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 10; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(128);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.GRAVEL, 32)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 20; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(128);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.COAL_ORE, 16)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 20; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(64);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.IRON_ORE, 8)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 2; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(32);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.GOLD_ORE, 8)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 8; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(16);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.REDSTONE_ORE, 7)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 1; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(16);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.DIAMOND_ORE, 7)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		for (centerX = 0; centerX < 1; ++centerX) {
			centerY = blockX + this.random.nextInt(16);
			centerZ = this.random.nextInt(16) + this.random.nextInt(16);
			j2 = blockZ + this.random.nextInt(16);
			(new WorldGenMinable(Block.LAPIS_ORE, 6)).populate(chunk, this.random, centerY, centerZ, j2);
		}

		d0 = 0.5D;
		centerX = (int) ((this.treeCountNoise.generateNoiseForCoordinate((double) blockX * d0, (double) blockZ * d0) / 8.0D + this.random.nextDouble() * 4.0D + 4.0D) / 3.0D);
		centerY = 0;
		if (this.random.nextInt(10) == 0) {
			++centerY;
		}

		if (biomebase == BiomeBase.FOREST) {
			centerY += centerX + 5;
		}

		if (biomebase == BiomeBase.JUNGLE) {
			centerY += centerX + 5;
		}

		if (biomebase == BiomeBase.SEASONAL_FOREST) {
			centerY += centerX + 2;
		}

		if (biomebase == BiomeBase.TAIGA) {
			centerY += centerX + 5;
		}

		if (biomebase == BiomeBase.DESERT) {
			centerY -= 20;
		}

		if (biomebase == BiomeBase.TUNDRA) {
			centerY -= 20;
		}

		if (biomebase == BiomeBase.PLAINS) {
			centerY -= 20;
		}

		int k2;

		for (centerZ = 0; centerZ < centerY; ++centerZ) {
			j2 = blockX + this.random.nextInt(16); //+ 8;
			k2 = blockZ + this.random.nextInt(16); //+ 8;
			WorldGenerator worldgenerator = biomebase.getTreeGenerator(this.random);

			worldgenerator.scale(1.0D, 1.0D, 1.0D);
			worldgenerator.populate(chunk, this.random, j2, generatorAssistance.getHighestPointYAt(chunk.getInstance(), j2, k2), k2);
		}

		byte b0 = 0;

		if (biomebase == BiomeBase.FOREST) {
			b0 = 2;
		}

		if (biomebase == BiomeBase.SEASONAL_FOREST) {
			b0 = 4;
		}

		if (biomebase == BiomeBase.TAIGA) {
			b0 = 2;
		}

		if (biomebase == BiomeBase.PLAINS) {
			b0 = 3;
		}

		int l2;
		int i3;

		for (j2 = 0; j2 < b0; ++j2) {
			k2 = blockX + this.random.nextInt(16)+ 8;
			i3 = this.random.nextInt(128);
			l2 = blockZ + this.random.nextInt(16)+ 8;
			(new WorldGenFlowers(Block.DANDELION)).populate(chunk, this.random, k2, i3, l2);
		}

		byte b1 = 0;

		if (biomebase == BiomeBase.FOREST) {
			b1 = 2;
		}

		if (biomebase == BiomeBase.JUNGLE) {
			b1 = 10;
		}

		if (biomebase == BiomeBase.SEASONAL_FOREST) {
			b1 = 2;
		}

		if (biomebase == BiomeBase.TAIGA) {
			b1 = 1;
		}

		if (biomebase == BiomeBase.PLAINS) {
			b1 = 10;
		}

		int j3;
		int k3;

		for (k2 = 0; k2 < b1; ++k2) {
			Block b2 = Block.SHORT_GRASS;

			if (biomebase == BiomeBase.JUNGLE && this.random.nextInt(3) != 0) {
				b2 = Block.FERN;
			}

			l2 = blockX + this.random.nextInt(16) + 8;
			k3 = this.random.nextInt(128);
			j3 = blockZ + this.random.nextInt(16) + 8;
			(new WorldGenGrass(b2)).populate(chunk, this.random, l2, k3, j3);
		}

		b1 = 0;
		if (biomebase == BiomeBase.DESERT) {
			b1 = 2;
		}

		for (k2 = 0; k2 < b1; ++k2) {
			i3 = blockX + this.random.nextInt(16) + 8;
			l2 = this.random.nextInt(128);
			k3 = blockZ + this.random.nextInt(16) + 8;
			(new WorldGenGrass(Block.DEAD_BUSH)).populate(chunk, this.random, i3, l2, k3);
		}
//
		if (this.random.nextInt(2) == 0) {
			k2 = blockX + this.random.nextInt(16)+ 8;
			i3 = this.random.nextInt(128);
			l2 = blockZ + this.random.nextInt(16)+ 8;
			(new WorldGenFlowers(Block.POPPY)).populate(chunk, this.random, k2, i3, l2);
		}

		if (this.random.nextInt(4) == 0) {
			k2 = blockX + this.random.nextInt(16); //+ 8;
			i3 = this.random.nextInt(128);
			l2 = blockZ + this.random.nextInt(16)+ 8;
			(new WorldGenFlowers(Block.BROWN_MUSHROOM)).populate(chunk, this.random, k2, i3, l2);
		}

		if (this.random.nextInt(8) == 0) {
			k2 = blockX + this.random.nextInt(16)+ 8;
			i3 = this.random.nextInt(128);
			l2 = blockZ + this.random.nextInt(16) ; //+ 8;
			(new WorldGenFlowers(Block.RED_MUSHROOM)).populate(chunk, this.random, k2, i3, l2);
		}

		for (k2 = 0; k2 < 10; ++k2) {
			i3 = blockX + this.random.nextInt(16) + 8;
			l2 = this.random.nextInt(128);
			k3 = blockZ + this.random.nextInt(16) + 8;
			(new WorldGenSugarCane()).populate(chunk, this.random, i3, l2, k3);
		}
//
//		if (this.random.nextInt(32) == 0) {
//			k2 = blockX + this.random.nextInt(16) + 8;
//			i3 = this.random.nextInt(128);
//			l2 = blockZ + this.random.nextInt(16) + 8;
//			(new WorldGenPumpkin173()).populate(blockAccess, this.random, k2, i3, l2);
//		}

		k2 = 0;
		if (biomebase == BiomeBase.DESERT) {
			k2 += 10;
		}

		for (i3 = 0; i3 < k2; ++i3) {
			l2 = blockX + this.random.nextInt(16) + 8;
			k3 = this.random.nextInt(128);
			j3 = blockZ + this.random.nextInt(16) + 8;
			(new WorldGenCactus()).populate(chunk, this.random, l2, k3, j3);
		}

		for (i3 = 0; i3 < 50; ++i3) {
			l2 = blockX + this.random.nextInt(16) + 8;
			k3 = this.random.nextInt(this.random.nextInt(120) + 8);
			j3 = blockZ + this.random.nextInt(16) + 8;
			(new WorldGenLiquids(Block.LAVA)).populate(chunk, this.random, l2, k3, j3);
		}

		for (i3 = 0; i3 < 20; ++i3) {
			l2 = blockX + this.random.nextInt(16) + 8;
			k3 = this.random.nextInt(this.random.nextInt(this.random.nextInt(112) + 8) + 8);
			j3 = blockZ + this.random.nextInt(16) + 8;
			(new WorldGenLiquids(Block.LAVA)).populate(chunk, this.random, l2, k3, j3);
		}

		this.snowNoise = this.worldChunkManager.createNoise(this.snowNoise, blockX + 8, blockZ + 8, 16, 16);
		for (i3 = blockX + 8; i3 < blockX + 8 + 16; ++i3) {
			for (l2 = blockZ + 8; l2 < blockZ + 8 + 16; ++l2) {
				k3 = i3 - (blockX + 8);
				j3 = l2 - (blockZ + 8);
				int l3 = generatorAssistance.getHighestPointYAt(chunk.getInstance(), i3, l2);
				double d1 = this.snowNoise[k3 * 16 + j3] - (double) (l3 - 64) / 64.0D * 0.3D;

				Block below = chunk.getInstance().getBlock(i3, l3 - 1, l2);

				if (d1 < 0.5D && l3 > 0 && l3 < 128 &&
						chunk.getInstance().getBlock(i3, l3, l2).isAir() &&
						below.isSolid() &&
						below != Block.ICE) {
					chunk.getInstance().setBlock(i3, l3, l2, Block.SNOW, false);
				}
			}
		}

	}
}
