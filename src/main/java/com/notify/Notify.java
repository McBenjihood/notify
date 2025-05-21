package com.notify;

import com.notify.items.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Notify implements ModInitializer {

	public static final String MOD_ID = "notify";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {


		ModItems.registerModItems();
		LOGGER.info("Hello Fabric world!");
	}


}

