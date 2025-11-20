package io.github.techtastic.cc_vs.forge.config

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue

object CCVSConfig {
    val BUILDER = ForgeConfigSpec.Builder()
    val SPEC: ForgeConfigSpec

    val CAN_TELEPORT: ConfigValue<Boolean>
    val COMMAND_ONLY: ConfigValue<Boolean>
    val EXPOSE_PHYS_TICK: ConfigValue<Boolean>

    init {
        BUILDER.push("CC: VS Mod Config")

        CAN_TELEPORT = BUILDER
                .comment("Is ExtendedShipAPI.teleport() enabled?", "Enable at your own Risk!")
                .worldRestart()
                .translation("config.cc_vs.can_teleport")
                .define("can_teleport", false)
        COMMAND_ONLY = BUILDER
                .comment(
                    "Is ExtendedShipAPI only available for the Command Computer?",
                    "This API is more powerful than the normal and advanced ShipAPI."
                )
                .worldRestart()
                .translation("config.cc_vs.command_only")
                .define("command_only", true)
        EXPOSE_PHYS_TICK = BUILDER
                .comment("Expose PhysShipImpl data from the previous physics ticks on game tick via CC: Tweaked's events?")
                .worldRestart()
                .translation("config.cc_vs.expose_phys_tick")
                .define("expose_phys_tick", false)

        BUILDER.pop()
        SPEC = BUILDER.build()
    }
}