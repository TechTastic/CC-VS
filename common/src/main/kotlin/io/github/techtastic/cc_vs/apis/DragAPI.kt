package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.IComputerSystem
import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import io.github.techtastic.cc_vs.util.CCVSUtils.verifyAdmin
import org.joml.*
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.mod.common.*
import java.lang.Math

open class DragAPI(val system: IComputerSystem) : ILuaAPI {
    @OptIn(GameTickOnly::class)
    val ship: LoadedServerShip
        get() = system.level.getLoadedShipManagingPos(system.position)
            ?: throw LuaException("This computer is not on a Ship!")

    override fun getNames(): Array<out String>? = arrayOf("drag")

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun getDragForce() = ship.dragController?.getDragForce()?.toLua()

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun getLiftForce() = ship.dragController?.getLiftForce()?.toLua()

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun enableDrag() {
        verifyAdmin(system)
        ship.dragController?.enableDrag()
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun disableDrag() {
        verifyAdmin(system)
        ship.dragController?.disableDrag()
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun enableLift() {
        verifyAdmin(system)
        ship.dragController?.enableLift()
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun disableLift() {
        verifyAdmin(system)
        ship.dragController?.disableLift()
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun enableRotDrag() {
        verifyAdmin(system)
        ship.dragController?.enableRotDrag()
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun disableRotDrag() {
        verifyAdmin(system)
        ship.dragController?.disableRotDrag()
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun setWindDirection(x: Double, y: Double, z: Double) {
        verifyAdmin(system)
        ship.dragController?.setWindDirection(x, y, z)
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun setWindSpeed(speed: Double) {
        verifyAdmin(system)
        ship.dragController?.setWindSpeed(speed)
    }

    @OptIn(VsBeta::class, GameTickOnly::class)
    @LuaFunction
    fun applyWindImpulse(args: IArguments) {
        verifyAdmin(system)
        val x = args.getDouble(0)
        val y = args.getDouble(1)
        val z = args.getDouble(2)
        val speed = args.getDouble(4).coerceAtMost(0.0)
        ship.dragController?.applyWindImpulse(Vector3d(x, y, z), speed)
    }
}