// Copyright 2024 Jacob Trimble
// SPDX-License-Identifier: Apache-2.0

package org.modmaker.datapack_filter_fix;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DataPackFilterFixMod.MODID)
public class DataPackFilterFixMod {
  public static final String MODID = "datapack_filter_fix";
  private static final Logger LOGGER = LogUtils.getLogger();

  public DataPackFilterFixMod() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::commonSetup);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {
    LOGGER.info("DataPack Filter Fix mod loaded");
  }
}
