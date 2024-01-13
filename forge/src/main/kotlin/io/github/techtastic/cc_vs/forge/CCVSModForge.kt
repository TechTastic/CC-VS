package io.github.techtastic.cc_vs.forge

import io.github.techtastic.cc_vs.CCVSMod
import io.github.techtastic.cc_vs.CCVSMod.init
import io.github.techtastic.cc_vs.CCVSMod.initClient
import io.github.techtastic.cc_vs.forge.config.CCVSConfig
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(CCVSMod.MOD_ID)
class CCVSModForge {
    init {
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CCVSConfig.SPEC, "${CCVSMod.MOD_ID}-config.toml")

        init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        initClient()
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
