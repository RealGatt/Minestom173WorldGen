package space.gatt.minestom173worldgen;

import space.gatt.minestom173worldgen.populator.WorldGenBigTree;
import space.gatt.minestom173worldgen.populator.WorldGenForest;
import space.gatt.minestom173worldgen.populator.WorldGenTrees;
import space.gatt.minestom173worldgen.populator.WorldGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeManager;

import java.util.Random;

public enum BiomeBase {
	JUNGLE(Biome.builder()
			.category(Biome.Category.JUNGLE)
			.downfall(0.89f)
			.precipitation(Biome.Precipitation.RAIN)
			.temperature(0.95f)
			.name(NamespaceID.from("beta:jungle"))
			.build()),
	SWAMPLAND(Biome.builder()
			.category(Biome.Category.SWAMP)
			.downfall(0.89f)
			.precipitation(Biome.Precipitation.RAIN)
			.temperature(0.95f)
			.name(NamespaceID.from("beta:swampland"))
			.build()),
	SEASONAL_FOREST(Biome.builder()
			.category(Biome.Category.FOREST)
			.downfall(0.5f)
			.precipitation(Biome.Precipitation.RAIN)
			.temperature(0.6f)
			.name(NamespaceID.from("beta:seasonal_forest"))
			.build()),
	FOREST(Biome.builder()
			.category(Biome.Category.FOREST)
			.downfall(0.5f)
			.precipitation(Biome.Precipitation.RAIN)
			.temperature(0.6f)
			.name(NamespaceID.from("beta:forest"))
			.build()) {
		@Override
		public WorldGenerator getTreeGenerator(Random random) {
			return random.nextInt(5) == 0 ? new WorldGenForest() : (random.nextInt(3) == 0 ? new WorldGenBigTree() : new WorldGenTrees());
		}
	},
	SAVANNA(Biome.builder()
			.category(Biome.Category.SAVANNA)
			.downfall(0.0f)
			.precipitation(Biome.Precipitation.NONE)
			.temperature(0.8f)
			.name(NamespaceID.from("beta:savanna"))
			.build()),
	SHRUBLAND(Biome.builder()
			.category(Biome.Category.PLAINS)
			.name(NamespaceID.from("beta:shrubland"))
			.temperature(0.8F)
			.downfall(0.4F)
			.depth(0.125F)
			.scale(0.05F)
			.build()),
	TAIGA(Biome.builder()
			.category(Biome.Category.TAIGA)
			.downfall(0.5f)
			.precipitation(Biome.Precipitation.SNOW)
			.temperature(0.01f)
			.name(NamespaceID.from("beta:snow"))
			.temperatureModifier(Biome.TemperatureModifier.FROZEN)
			.build()) {
		@Override
		public WorldGenerator getTreeGenerator(Random random) {
			return  new WorldGenTrees();
		}
	},
	DESERT(Biome.builder()
			.name(NamespaceID.from("beta:desert"))
			.category(Biome.Category.DESERT)
			.temperature(0.75f)
			.downfall(0.25f)
			.precipitation(Biome.Precipitation.NONE)
			.build(), Block.SAND, Block.SAND),
	PLAINS(Biome.builder()
			.category(Biome.Category.NONE)
			.name(NamespaceID.from("beta:plains"))
			.temperature(0.8F)
			.downfall(0.4F)
			.depth(0.125F)
			.scale(0.05F)
			.build()),
	TUNDRA(Biome.builder()
			.category(Biome.Category.TAIGA)
			.downfall(0.5f)
			.precipitation(Biome.Precipitation.SNOW)
			.temperature(0.01f)
			.name(NamespaceID.from("beta:tundra"))
			.temperatureModifier(Biome.TemperatureModifier.FROZEN)
			.build());

	public static void init() {
		BiomeManager bm = MinecraftServer.getBiomeManager();
		for (BiomeBase bb : BiomeBase.values()) {
			bm.addBiome(bb.biome);
		}
	}

	public final Biome biome;
	public final Block top;
	public final Block bottom;

	BiomeBase(Biome bukkitBiome) {
		this(bukkitBiome, Block.GRASS_BLOCK, Block.DIRT);
	}

	BiomeBase(Biome bukkitBiome, Block top, Block bottom) {
		this.biome = bukkitBiome;
		this.top = top;
		this.bottom = bottom;
	}

	static final BiomeBase[] LOOKUP = new BiomeBase[64 * 64];

	static {
		for (int i = 0; i < 64; ++i) {
			for (int k = 0; k < 64; ++k) {
				LOOKUP[i + k * 64] = getByRainTempUncached((float)i / 63.0F, (float)k / 63.0F);
			}
		}
	}

	public static BiomeBase getByRainTempUncached(float f, float f1) {
		f1 *= f;
		return f < 0.1F ? TUNDRA : (f1 < 0.2F ? (f < 0.5F ? TUNDRA : (f < 0.95F ? SAVANNA : DESERT)) : (f1 > 0.5F && f < 0.7F ? SWAMPLAND : (f < 0.5F ? TAIGA : (f < 0.97F ? (f1 < 0.35F ? SHRUBLAND : FOREST) : (f1 < 0.45F ? PLAINS : (f1 < 0.9F ? SEASONAL_FOREST : JUNGLE))))));
	}

	public static BiomeBase a(double temp, double rain) {
		int i = (int)(temp * 63.0D);
		int j = (int)(rain * 63.0D);

		return LOOKUP[i + j * 64];
	}

	public WorldGenerator getTreeGenerator(Random random) {
		return random.nextInt(10) == 0 ? new WorldGenBigTree() : new WorldGenTrees();
	}
}
