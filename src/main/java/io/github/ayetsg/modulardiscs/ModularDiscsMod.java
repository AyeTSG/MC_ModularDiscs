package io.github.ayetsg.modulardiscs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModularDiscsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("tsg_modulardiscs");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
