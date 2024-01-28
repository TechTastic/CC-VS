package io.github.techtastic.cc_vs.forge.mixin;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import io.github.techtastic.cc_vs.util.CCVSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(TileTurtle.class)
public class MixinTileTurtle {
    @Inject(
            method = "createComputer",
            at = @At("RETURN"),
            remap = false
    )
    private void cc_vs$addShipAPI(int id, CallbackInfoReturnable<ServerComputer> cir) {
        ServerComputer computer = cir.getReturnValue();
        ServerLevel level = computer.getLevel();
        BlockPos pos = computer.getPosition();
        ServerShip ship = VSGameUtilsKt.getShipObjectManagingPos(level, pos);

        CCVSUtils.INSTANCE.applyShipAPIsToComputer(computer, level, ship);
    }
}
