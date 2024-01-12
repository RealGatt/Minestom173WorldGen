package space.gatt.minestom173worldgen;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.*;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

import java.util.Random;
import java.util.UUID;

public class Example {

	public static DimensionType GATT_FULLBRIGHT_173 = DimensionType.builder(NamespaceID.from("gatt:fullbright_beta"))
			.ultrawarm(false)
			.natural(true)
			.piglinSafe(false)
			.respawnAnchorSafe(false)
			.bedSafe(true)
			.raidCapable(false)
			.skylightEnabled(true)
			.ceilingEnabled(false)
			.fixedTime(null)
			.ambientLight(1.0f)
			.height(128)
			.minY(0)
			.logicalHeight(128)
			.infiniburn(NamespaceID.from("minecraft:infiniburn_overworld"))
			.build();

	private static Instance generateInstance() {
		InstanceManager instanceManager = MinecraftServer.getInstanceManager();
		BetaInstanceContainer instanceContainer = new BetaInstanceContainer(UUID.randomUUID(), GATT_FULLBRIGHT_173);
		instanceManager.registerInstance(instanceContainer);
		instanceContainer.setGenerator(new MinestomHook(instanceContainer, new Random().nextInt()));
		instanceContainer.enableAutoChunkLoad(true);
		return instanceContainer;
	}
}
