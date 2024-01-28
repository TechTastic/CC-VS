package io.github.techtastic.cc_vs.fabric.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CCVSConfig {
    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<Boolean> CAN_TELEPORT;
    public static ForgeConfigSpec.ConfigValue<Boolean> COMMAND_ONLY;

    static {
        BUILDER.push("CC: VS Mod Config");

        CAN_TELEPORT = BUILDER.comment("Is ExtendedShipAPI.teleport() enabled?", "Enable at your own Risk!")
                .worldRestart().translation("config.cc_vs.can_teleport").define("can_teleport", false);
        COMMAND_ONLY = BUILDER.comment("Is ExtendedShipAPI only available for the Command Computer?",
                "This API is more powerful than the normal and advanced ShipAPI.").worldRestart()
                .translation("config.cc_vs.command_only").define("command_only", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}