package io.github.techtastic.cc_vs.apis

import dan200.computercraft.api.lua.*
import io.github.techtastic.cc_vs.PlatformUtils
import io.github.techtastic.cc_vs.util.CCVSUtils
import io.github.techtastic.cc_vs.util.CCVSUtils.toLua
import io.github.techtastic.cc_vs.util.CCVSUtils.toVector
import org.joml.*
import org.joml.primitives.AABBi
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.util.GameTickOnly
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.api.world.properties.DimensionId
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl
import org.valkyrienskies.core.internal.joints.VSJointAndId
import org.valkyrienskies.core.internal.world.VsiPhysLevel
import org.valkyrienskies.mod.common.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArrayList

open class ShipAPI(val system: IComputerSystem) : ILuaAPI {
    private val dimensionId: DimensionId
        get() = system.level.dimensionId
    @OptIn(GameTickOnly::class)
    val ship: LoadedServerShip
        get() = system.level.getLoadedShipManagingPos(system.position)
            ?: throw LuaException("This computer is not on a Ship!")

    @OptIn(PhysTickOnly::class)
    val joints: CopyOnWriteArrayList<VSJointAndId> = CopyOnWriteArrayList()
    private val startCollisions: ConcurrentHashMap.KeySetView<Map<String, Any>, Boolean> = ConcurrentHashMap.newKeySet()
    private val persistCollisions: ConcurrentHashMap.KeySetView<Map<String, Any>, Boolean> = ConcurrentHashMap.newKeySet()
    private val endCollisions: ConcurrentHashMap.KeySetView<Map<String, Any>, Boolean> = ConcurrentHashMap.newKeySet()

    private val queuedData = ConcurrentLinkedQueue<LuaPhysShip>()

    @OptIn(GameTickOnly::class, PhysTickOnly::class, VsBeta::class)
    override fun startup() {
        ValkyrienSkiesMod.api.physTickEvent.on { event ->
            joints.clear()
            val world = event.world as? VsiPhysLevel
            val ship = system.level.getLoadedShipManagingPos(system.position) ?: return@on
            world?.getJointsFromShip(ship.id)?.forEach { id ->
                world.getJointById(id)?.let { joint -> joints.add(VSJointAndId(id, joint)) }
            }
        }

        ValkyrienSkiesMod.api.collisionStartEvent.on { event ->
            val ship = system.level.getLoadedShipManagingPos(system.position) ?: return@on
            if (this.dimensionId == event.dimensionId && (ship.id == event.shipIdA || ship.id == event.shipIdB))
                startCollisions.add(event.toLua())
        }
        ValkyrienSkiesMod.api.collisionPersistEvent.on { event ->
            val ship = system.level.getLoadedShipManagingPos(system.position) ?: return@on
            if (this.dimensionId == event.dimensionId && (ship.id == event.shipIdA || ship.id == event.shipIdB))
                persistCollisions.add(event.toLua())
        }
        ValkyrienSkiesMod.api.collisionEndEvent.on { event ->
            val ship = system.level.getLoadedShipManagingPos(system.position) ?: return@on
            if (this.dimensionId == event.dimensionId && (ship.id == event.shipIdA || ship.id == event.shipIdB))
                endCollisions.add(event.toLua())
        }

        if (PlatformUtils.exposePhysTick())
            ValkyrienSkiesMod.api.physTickEvent.on { event ->
                val ship = system.level.getLoadedShipManagingPos(system.position) ?: return@on
                event.world.getShipById(ship.id)?.let { queuedData.add(LuaPhysShip(it)) }
            }
        super.startup()
    }

    override fun update() {
        if (startCollisions.isNotEmpty()) {
            system.queueEvent("collisions_started", *this.startCollisions.toTypedArray())
            startCollisions.clear()
        }
        if (persistCollisions.isNotEmpty()) {
            system.queueEvent("collisions_persisted", *this.persistCollisions.toTypedArray())
            persistCollisions.clear()
        }
        if (endCollisions.isNotEmpty()) {
            system.queueEvent("collisions_ended", *this.endCollisions.toTypedArray())
            endCollisions.clear()
        }

        try {
            if (PlatformUtils.exposePhysTick()) {
                system.queueEvent("physics_ticks", *queuedData.toTypedArray())
                queuedData.clear()
            }
        } catch (_: LuaException) {}
        super.update()
    }

    override fun getNames(): Array<out String>? = arrayOf("ship")

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getId(): Long = ship.id

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getMass(): Double = ship.inertiaData.mass

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getMomentOfInertiaTensor(): List<List<Double>> = ship.inertiaData.inertiaTensor.toLua()

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getSlug(): String = ship.slug ?: "no-name"

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getAngularVelocity(): Map<String, Double> = ship.angularVelocity.toLua()

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getQuaternion(): Map<String, Double> = ship.transform.shipToWorldRotation.toLua()

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getScale(): Map<String, Double> = ship.transform.shipToWorldScaling.toLua()

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getShipyardPosition(): Map<String, Double> = ship.transform.positionInShip.toLua()

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getSize(): Map<String, Any> {
        val aabb = ship.shipAABB ?: AABBi(0, 0, 0, 0, 0, 0)
        return mapOf(
            Pair("x", aabb.maxX() - aabb.minX()),
            Pair("y", aabb.maxY() - aabb.minY()),
            Pair("z", aabb.maxZ() - aabb.minZ())
        )
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getVelocity(): Map<String, Double> = ship.velocity.toLua()

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getWorldspacePosition(): Map<String, Double> = ship.transform.positionInWorld.toLua()

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun transformPositionToWorld(args: IArguments): Map<String, Double> {
        val pos =
            if (args.count() == 1)
                Vector3d(args.getTable(0).toVector())
            else
                Vector3d(args.getDouble(0), args.getDouble(1), args.getDouble(2))
        return ship.shipToWorld.transformPosition(pos).toLua()
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun isStatic(): Boolean = ship.isStatic

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun setSlug(name: String) {
        ValkyrienSkiesMod.vsCore.renameShip(ship, name)
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun getTransformationMatrix(): List<List<Double>> {
        val transform = ship.transform.shipToWorld
        val matrix: MutableList<List<Double>> = mutableListOf()

        for (i in 0..3) {
            val row = transform.getRow(i, Vector4d())
            matrix.add(i, listOf(row.x, row.y, row.z, row.w))
        }

        return matrix.toList()
    }

    @OptIn(PhysTickOnly::class)
    @LuaFunction
    fun getJoints(): List<*> {
        return joints.map { combo -> combo.toLua() }.toList()
    }

    @LuaFunction
    fun pullPhysicsTicks(): Array<Any>? {
        if (!PlatformUtils.exposePhysTick())
            throw LuaException("Physics Tick is not exposed! This is a configuration option!")
        return null
    }
    
    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyWorldForce(forceInWorldX: Double, forceInWorldY: Double, forceInWorldZ: Double, args: IArguments) {
        CCVSUtils.verifyAdmin(system)
        var posInWorld: Vector3d? = null
        args.optDouble(3).ifPresent { x ->
            args.optDouble(4).ifPresent { y ->
                args.optDouble(5).ifPresent { z ->
                    posInWorld = Vector3d(x, y, z)
                }
            }
        }
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyWorldForce(ship.id, Vector3d(forceInWorldX, forceInWorldY, forceInWorldZ), posInWorld)
    }
    
    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyWorldTorque(torqueInWorldX: Double, torqueInWorldY: Double, torqueInWorldZ: Double) {
        CCVSUtils.verifyAdmin(system)
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyWorldTorque(ship.id, Vector3d(torqueInWorldX, torqueInWorldY, torqueInWorldZ))
    }
    
    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyModelForce(forceInShipX: Double, forceInShipY: Double, forceInShipZ: Double, args: IArguments) {
        CCVSUtils.verifyAdmin(system)
        var posInShip: Vector3d? = null
        args.optDouble(3).ifPresent { x ->
            args.optDouble(4).ifPresent { y ->
                args.optDouble(5).ifPresent { z ->
                    posInShip = Vector3d(x, y, z)
                }
            }
        }
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyModelForce(ship.id, Vector3d(forceInShipX, forceInShipY, forceInShipZ), posInShip)
    }
    
    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyModelTorque(torqueInShipX: Double, torqueInShipY: Double, torqueInShipZ: Double) {
        CCVSUtils.verifyAdmin(system)
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyModelTorque(ship.id, Vector3d(torqueInShipX, torqueInShipY, torqueInShipZ))
    }
    
    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyWorldForceToModelPos(forceInWorldX: Double, forceInWorldY: Double, forceInWorldZ: Double, posInShipX: Double, posInShipY: Double, posInShipZ: Double) {
        CCVSUtils.verifyAdmin(system)
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyWorldForceToModelPos(ship.id, Vector3d(forceInWorldX, forceInWorldY, forceInWorldZ), Vector3d(posInShipX, posInShipY, posInShipZ))
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyBodyForce(forceInBodyX: Double, forceInBodyY: Double, forceInBodyZ: Double, args: IArguments) {
        CCVSUtils.verifyAdmin(system)
        val posInBody = Vector3d()
        args.optDouble(3).ifPresent { x ->
            args.optDouble(4).ifPresent { y ->
                args.optDouble(5).ifPresent { z ->
                    posInBody.set(x, y, z)
                }
            }
        }
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyBodyForce(ship.id, Vector3d(forceInBodyX, forceInBodyY, forceInBodyZ), posInBody)
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyBodyTorque(torqueInBodyX: Double, torqueInBodyY: Double, torqueInBodyZ: Double) {
        CCVSUtils.verifyAdmin(system)
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyBodyTorque(ship.id, Vector3d(torqueInBodyX, torqueInBodyY, torqueInBodyZ))
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun applyWorldForceToBodyPos(forceInWorldX: Double, forceInWorldY: Double, forceInWorldZ: Double, posInBodyX: Double, posInBodyY: Double, posInBodyZ: Double) {
        CCVSUtils.verifyAdmin(system)
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).applyWorldForceToModelPos(ship.id, Vector3d(forceInWorldX, forceInWorldY, forceInWorldZ), Vector3d(posInBodyX, posInBodyY, posInBodyZ))
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun setStatic(b: Boolean) {
        CCVSUtils.verifyAdmin(system)
        ValkyrienSkiesMod.getOrCreateGTPA(system.level.dimensionId).setStatic(ship.id, b)
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun setScale(scale: Double) {
        CCVSUtils.verifyAdmin(system)
        vsCore.scaleShip(system.level.shipObjectWorld, ship, scale)
    }

    @OptIn(GameTickOnly::class)
    @LuaFunction
    fun teleport(args: IArguments) {
        CCVSUtils.verifyAdmin(system)
        if (!PlatformUtils.canTeleport())
            throw LuaException("Teleporting is Disabled via CC: VS Config!")

        val input = args.getTable(0)

        var pos = ship.transform.positionInWorld
        if (input.containsKey("pos"))
            pos = getVectorFromTable(input, "pos")

        var rot = ship.transform.shipToWorldRotation
        if (input.containsKey("rot"))
            rot = getQuaternionFromTable(input).normalize(Quaterniond())

        var vel = ship.velocity
        if (input.containsKey("vel"))
            vel = getVectorFromTable(input, "vel")

        var omega = ship.angularVelocity
        if (input.containsKey("omega"))
            omega = getVectorFromTable(input, "omega")

        var dimension: String? = null
        if (input.containsKey("dimension"))
            dimension = (input["dimension"] ?: throwMalformedSectionError("dimension")) as String

        var scale = ship.transform.shipToWorldScaling.x()
        if (input.containsKey("scale"))
            scale = (input["scale"] ?: throwMalformedSectionError("scale")) as Double

        val teleportData = ShipTeleportDataImpl(pos, rot, vel, omega, dimension, scale)

        println("Rot: ${teleportData.newRot}\n")

        //vsCore.teleportShip(this.level.shipObjectWorld, getShip(), teleportData)
        system.level.shipObjectWorld.teleportShip(ship, teleportData)
    }

    private fun getVectorFromTable(input: Map<*, *>, section: String): Vector3dc {
        val table = (input[section] ?: throwMalformedSectionError(section)) as Map<*, *>
        return Vector3d(
            (table["x"] ?: throwMalformedFieldError(section, "x")) as Double,
            (table["y"] ?: throwMalformedFieldError(section, "y")) as Double,
            (table["z"] ?: throwMalformedFieldError(section, "z")) as Double
        )
    }

    private fun getQuaternionFromTable(input: Map<*, *>): Quaterniondc {
        val table = (input["rot"] ?: throwMalformedSectionError("rot")) as Map<*, *>
        return Quaterniond(
            (table["x"] ?: throwMalformedFieldError("rot", "x")) as Double,
            (table["y"] ?: throwMalformedFieldError("rot", "y")) as Double,
            (table["z"] ?: throwMalformedFieldError("rot", "z")) as Double,
            (table["w"] ?: throwMalformedFieldError("rot", "w")) as Double
        )
    }

    private fun throwMalformedSectionError(section: String): Nothing =
        throw LuaException("Malformed $section")
    private fun throwMalformedFieldError(section: String, field: String): Nothing =
        throw LuaException("Malformed $field key of $section")
}