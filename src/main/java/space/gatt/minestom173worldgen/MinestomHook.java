package space.gatt.minestom173worldgen;

import space.gatt.minestom173worldgen.events.ChunkPopulatedEvent;
import space.gatt.minestom173worldgen.overworld.ChunkProviderGenerate;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.bossbar.BossBarManager;
import net.minestom.server.instance.generator.GenerationUnit;
import net.minestom.server.instance.generator.Generator;
import org.jetbrains.annotations.NotNull;

public class MinestomHook implements Generator {
	private final ChunkProviderGenerate chunkProviderGenerate;
	private BossBar bossBar;
	private int populatedChunks = 0;
	private int chunksToLoad = 0;

	public MinestomHook(BetaInstanceContainer instance, long seed) {
//		seed = 9876543210L;
		System.out.println("SEED " + seed);
		bossBar = BossBar.bossBar(Component.text("Generating World"), 0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);

		this.chunkProviderGenerate = new ChunkProviderGenerate(instance, seed);
		instance.setChunkProviderGenerate(this.chunkProviderGenerate);

		BossBarManager bossBarManager = MinecraftServer.getBossBarManager();
		bossBarManager.addBossBar(MinecraftServer.getConnectionManager().getOnlinePlayers(), bossBar);

		MinecraftServer.getGlobalEventHandler().addListener(ChunkPopulatedEvent.class, event -> {
			populatedChunks++;
			bossBar.progress(Math.min((float) populatedChunks / (float) chunksToLoad, 1f));
			if (populatedChunks > chunksToLoad) {
				bossBar.color(BossBar.Color.RED);
			} else {
				bossBar.color(BossBar.Color.BLUE);
			}
			bossBar.name(Component.text("Generating World (" + populatedChunks + "/" + chunksToLoad + ")"));
		});
	}

	@Override
	public synchronized void generate(@NotNull GenerationUnit unit) {
		int chunkX = unit.absoluteStart().chunkX();
		int chunkZ = unit.absoluteStart().chunkZ();
		chunksToLoad++;
		bossBar.progress(Math.min((float) populatedChunks / (float) chunksToLoad, 1f));
		bossBar.name(Component.text("Generating World (" + populatedChunks + "/" + chunksToLoad + ")"));
		if (populatedChunks > chunksToLoad) {
			bossBar.color(BossBar.Color.RED);
		} else {
			bossBar.color(BossBar.Color.BLUE);
		}
		this.chunkProviderGenerate.generateUnpopulatedChunkData(unit, chunkX, chunkZ);
	}
}
