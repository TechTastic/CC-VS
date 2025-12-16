package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.IComputerSystem
import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaFunction
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.AerodynamicUtils
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.mod.api.positionToWorld
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.dimensionId

class AerodynamicsAPI(private val system: IComputerSystem): ILuaAPI {
    val dimensionId: DimensionId
        get() = system.level.dimensionId

    @OptIn(GameTickOnly::class, VsBeta::class)
    val utils: AerodynamicUtils?
        get() = ValkyrienSkiesMod.api.getServerShipWorld()?.aerodynamicUtils

    override fun getNames() = arrayOf("aerodynamics", "aero")

    @OptIn(VsBeta::class)
    val defaultMax: Double
        @LuaFunction
        get() = AerodynamicUtils.DEFAULT_MAX

    @OptIn(VsBeta::class)
    val defaultSeaLevel: Double
        @LuaFunction
        get() = AerodynamicUtils.DEFAULT_SEA_LEVEL

    @OptIn(VsBeta::class)
    val dragCoefficient: Double
        @LuaFunction
        get() = AerodynamicUtils.DRAG_COEFFICIENT

    @OptIn(VsBeta::class)
    val gravitationalAcceleration: Double
        @LuaFunction
        get() = AerodynamicUtils.GRAVITATIONAL_ACCELERATION

    @OptIn(VsBeta::class)
    val universalGasConstant: Double
        @LuaFunction
        get() = AerodynamicUtils.UNIVERSAL_GAS_CONSTANT

    @OptIn(VsBeta::class)
    val airMolarMass: Double
        @LuaFunction
        get() = AerodynamicUtils.AIR_MOLAR_MASS

    @OptIn(VsBeta::class)
    @LuaFunction
    fun getAtmosphericParameters(): Map<String, Double>? {
        utils?.let { utils ->
            val (maxY, seaLevel, gravity) = utils.getAtmosphereForDimension(dimensionId)
            return mapOf(
                "maxY" to maxY,
                "seaLevel" to seaLevel,
                "gravity" to gravity
            )
        }
        return null
    }

    @OptIn(VsBeta::class)
    @LuaFunction
    fun getAirDensity(args: IArguments): Double? {
        val y = args.optDouble(0, system.level.positionToWorld(system.position.center).y)
        return utils?.getAirDensityForY(y, dimensionId)
    }

    @OptIn(VsBeta::class)
    @LuaFunction
    fun getAirPressure(args: IArguments): Double? {
        val y = args.optDouble(0, system.level.positionToWorld(system.position.center).y)
        return utils?.getAirPressureForY(y, dimensionId)
    }

    @OptIn(VsBeta::class)
    @LuaFunction
    fun getAirTemperature(args: IArguments): Double? {
        val y = args.optDouble(0, system.level.positionToWorld(system.position.center).y)
        return utils?.getAirTemperatureForY(y, dimensionId)
    }
}