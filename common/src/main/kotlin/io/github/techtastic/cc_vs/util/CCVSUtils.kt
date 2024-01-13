package io.github.techtastic.cc_vs.util

import dan200.computercraft.shared.computer.core.ComputerFamily
import dan200.computercraft.shared.computer.core.ServerComputer
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.apis.ExtendedShipAPI
import io.github.techtastic.cc_vs.apis.ShipAPI
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.api.ships.ServerShip

object CCVSUtils {
    fun applyShipAPIsToComputer(computer: ServerComputer, level: ServerLevel, ship: ServerShip?) {
        if (ship == null)
            return

        if (!PlatformUtils.isCommandOnly() || computer.family == ComputerFamily.COMMAND)
            computer.addAPI(ExtendedShipAPI(computer.apiEnvironment, ship, level))
        else
            computer.addAPI(ShipAPI(computer.apiEnvironment, ship))
    }
}