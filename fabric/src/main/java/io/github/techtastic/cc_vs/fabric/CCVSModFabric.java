package io.github.techtastic.cc_vs.fabric;

import fuzs.forgeconfigapiport.impl.config.ForgeConfigRegistryImpl;
import io.github.techtastic.cc_vs.CCVSMod;
import io.github.techtastic.cc_vs.fabric.config.CCVSConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

import static io.github.techtastic.cc_vs.CCVSMod.MOD_ID;

public class CCVSModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();

        ForgeConfigRegistryImpl.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, CCVSConfig.SPEC, MOD_ID + "-config.toml");

        CCVSMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            CCVSMod.initClient();
        }
    }
}
