package io.github.techtastic.cc_vs.forge.config

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue

object CCVSConfig {
    val BUILDER = ForgeConfigSpec.Builder()
    val SPEC: ForgeConfigSpec

    val CAN_TELEPORT: ConfigValue<Boolean>
    val COMMAND_ONLY: ConfigValue<Boolean>

    init {
        BUILDER.push("CC: VS Mod Config")

        CAN_TELEPORT = BUILDER.comment("Is ExtendedShipAPI.teleport() enabled?", "Enable at your own Risk!")
                .worldRestart().translation("config.cc_vs.can_teleport").define("can_teleport", false)

        COMMAND_ONLY = BUILDER.comment("Is ExtendedShipAPI only available for the Command Computer?",
                "This API is more powerful than the normal and advanced ShipAPI.").worldRestart()
                .translation("config.cc_vs.command_only").define("command_only", true)

        BUILDER.pop()
        SPEC = BUILDER.build()
    }
}