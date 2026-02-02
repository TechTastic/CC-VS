--- This API is added by CC: VS and allows CC: Tweaked computers to access information from Valkyrien Skies Ships.
--
-- This library also includes [CC: Advanced Math][advanced-math] which provides `quaternion` and `matrix` APIs.
--
-- [advanced-math]: https://techtastic.github.io/Advanced-Math/
--
-- @module ship

--- Gets the Ship's ID
-- @function getId
-- @treturn number The Ship ID
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's unique Slug
-- @function getSlug
-- @treturn string The Ship's Slug
-- @see setSlug
-- @raise This method errors if there is no Ship associated with the computer

--- Sets the Ship's unique Slug
-- @function setSlug
-- @tparam string name The Ship's new Slug
-- @see getSlug
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's total mass
-- @function getMass
-- @treturn number The Ship's mass
-- @raise This method errors if there is no Ship associated with the computer

--- Gets whether the Ship is static
-- @function isStatic
-- @treturn boolean Whether the Ship is static
-- @see setStatic
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the joints on the Ship
-- @function getJoints
-- @treturn table A table of joints on the Ship
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's Center of Mass in ship-local coordinates
-- @function getShipyardPosition
-- @treturn vector The Ship's Center of Mass in the Shipyard
-- @see getWorldspacePosition
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's Center of Mass in world coordinates
-- @function getWorldspacePosition
-- @treturn vector The Ship's Center of Mass in Worldspace
-- @see getShipyardPosition
-- @raise This method errors if there is no Ship associated with the computer

--- Transforms a position from ship-local coordinates to world coordinates
-- @function transformPositionToWorld
-- @tparam vector pos The position in ship-local coordinates
-- @treturn vector The position in world coordinates
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's linear velocity
-- @function getVelocity
-- @treturn vector The Ship's linear velocity
-- @see getOmega
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's angular velocity
-- @function getAngularVelocity
-- @treturn vector The Ship's angular velocity
-- @see getVelocity
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's scale
-- @function getScale
-- @treturn vector The Ship's scale
-- @see setScale
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's Quaternion
-- @function getQuaternion
-- @treturn quaternion The Ship's Quaternion
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Ship's Transformation Matrix
-- @function getTransformationMatrix
-- @treturn matrix The Ship's Transformation Matrix
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the Moment of Inertia Tensor to be saved
-- @function getMomentOfInertiaTensorToSave
-- @treturn matrix The Ship's Moment of Inertia Tensor
-- @see getMomentOfInertiaTensor
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the current Moment of Inertia Tensor
-- @function getMomentOfInertiaTensor
-- @treturn matrix The Ship's Moment of Inertia Tensor
-- @see getMomentOfInertiaTensorToSave
-- @raise This method errors if there is no Ship associated with the computer

--- Pulls physics ticks for the Ship
--
-- This function blocks until the next physics tick event for the Ship is available.
-- It returns the event name and the physics ticks information.
--
-- @function pullPhysicsTicks
-- @treturn string The event name
-- @treturn ... Physics ticks information
-- @raise This method errors if there is no Ship associated with the computer

--- Extended Ship API
--
-- @section extended_ship_api

--- Sets the Ship to be static or dynamic
-- @function setStatic
-- @tparam boolean isStatic Whether the Ship should be static
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Sets the Ship's scale
-- @function setScale
-- @tparam number scale The new scale for the Ship
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Teleports the Ship to a new position and orientation
--
-- This function instantly moves the Ship to the specified position and orientation.
-- It does not take into account any physics simulation and may result in unexpected behavior
-- if the new position intersects with other objects or violates physics joints.
--
-- Note: This function may cause physics instability if used improperly.
-- Use with caution.
--
-- @function teleport
-- @tparam table data The new position and orientation for the Ship
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it OR if this method is disabled in the configuration.

--- Applies a force in World Space to a ship at a World Space position. A World Space force is independent of the ship's transform, and is always global; for example, up in World Space is ALWAYS (0, 1, 0) (as in, towards the sky), regardless of the ship's orientation.
--
-- @function applyWorldForce
-- @tparam vector force The force vector in World Space
-- @tparam vector|nil position The position in World Space where the force is applied. Defaults to the ship's center of mass in World Space
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a torque in World Space to a ship at a World Space position. A World Space torque is independent of the ship's transform, and is always global; for example, up in World Space is ALWAYS (0, 1, 0) (as in, towards the sky), regardless of the ship's orientation.
--
-- @function applyWorldTorque
-- @tparam vector torque The torque vector in World Space
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a force in Model Space to a ship at a Model Space position. A Model Space force is relative to the ship's transform, meaning that it rotates and scales with the ship; for example, a ship rotated on its side applying a force pointing to (0, 1, 0) in Model Space would be perpendicular to World Space up.
--
-- This is useful for a Thruster or similar block that should apply a force relative to the ship's orientation
--
-- @function applyModelForce
-- @tparam vector force The force vector in Model Space
-- @tparam vector|nil position The position in Model Space where the force is applied. Defaults to the ship's center of mass in Model Space
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a torque in Model Space to a ship at a Model Space position. A Model Space torque is relative to the ship's transform, meaning that it rotates and scales with the ship
--
-- @function applyModelTorque
-- @tparam vector torque The torque vector in Model Space
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a force in World Space to a ship at a Model Space position. A World Space force is independent of the ship's transform, and is always global; for example, up in World Space is ALWAYS (0, 1, 0) (as in, towards the sky), regardless of the ship's orientation.
--
-- This is useful for a balloon or similar block that should apply a force relative to the world, such as always pushing up against gravity.
--
-- @function applyWorldForceToModelPos
-- @tparam vector force The force vector in World Space
-- @tparam vector position The position in Model Space where the force is applied
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a force in Body Space to a ship at a Body Space position. A Body Space force is positionally relative to the ship's Center of Mass, and applies relative to the ship's transform, meaning that it rotates and scales with the ship
--
-- @function applyBodyForce
-- @tparam vector force The force vector in Body Space
-- @tparam vector|nil position The position in Body Space where the force is applied. Defaults to (0,0,0), the ship's center of mass
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a torque in Body Space to a ship at a Body Space position. A Body Space torque is positionally relative to the ship's Center of Mass, and applies relative to the ship's transform, meaning that it rotates and scales with the ship
--
-- @function applyBodyTorque
-- @tparam vector torque The torque vector in Body Space
-- @tparam vector|nil position The position in Body Space where the force is applied. Defaults to (0,0,0), the ship's center of mass
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a force in World Space to a ship at a Body Space position. A World Space force is independent of the ship's transform, and is always global; for example, up in World Space is ALWAYS (0, 1, 0) (as in, towards the sky), regardless of the ship's orientation
--
-- @function applyWorldForceToBodyPos
-- @tparam vector force The force vector in World Space
-- @tparam vector|nil position The position in Body Space where the force is applied
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Deprecated Methods
--
-- @section deprecated

--- Gets the Ship's Omega (angular velocity)
-- @function getOmega
-- @raise This method no longer exists! Use getAngularVelocity instead!

--- Gets the Ship's Euler Angles in ZYX order
-- @function getEulerAnglesZYX
-- @raise This method no longer exists! Please utilize the new quaternion API!

--- Gets the Ship's Euler Angles in ZXY order
-- @function getEulerAnglesZXY
-- @raise This method no longer exists! Please utilize the new quaternion API!

--- Gets the Ship's Euler Angles in YXZ order
-- @function getEulerAnglesYXZ
-- @raise This method no longer exists! Please utilize the new quaternion API!

--- Gets the Ship's Euler Angles in XYZ order
-- @function getEulerAnglesXYZ
-- @raise This method no longer exists! Please utilize the new quaternion API!

--- Gets the Ship's Roll angle
-- @function getRoll
-- @raise This method no longer exists! Please utilize the new quaternion API!

--- Gets the Ship's Yaw angle
-- @function getYaw
-- @raise This method no longer exists! Please utilize the new quaternion API!

--- Gets the Ship's Pitch angle
-- @function getPitch
-- @raise This method no longer exists! Please utilize the new quaternion API!

--- Gets the Ship's Rotation Matrix
-- @function getRotationMatrix
-- @raise This method no longer exists! Use getTransformationMatrix instead!

--- Applies an Invariant Force to the Ship
-- @function applyInvariantForce
-- @raise This method no longer exists! Use applyWorldForceToBodyPos instead!

--- Applies an Invariant Torque to the Ship
-- @function applyInvariantTorque
-- @raise This method no longer exists! Use applyWorldTorque instead!

--- Applies an Invariant Force to a position offset from the Ship's center of mass
-- @function applyInvariantForceToPos
-- @raise This method no longer exists! Use applyBodyForce instead!

--- Applies a Rotation-Dependent Force to the Ship
-- @function applyRotDependentForce
-- @raise This method no longer exists! Use applyBodyForce instead!

--- Applies a Rotation-Dependent Torque to the Ship
-- @function applyRotDependentTorque
-- @raise This method no longer exists! Use applyBodyTorque instead!

--- Applies a Rotation-Dependent Force to a position offset from the Ship's center of mass
-- @function applyRotDependentForceToPos
-- @raise This method no longer exists! Use applyBodyForce instead!

--- Physics Ticks Event Data
--
-- @section PhysicsTicksEventData

--- Gets the Ship's buoyancy factor during the physics tick
-- @function getBuoyancyFactor
-- @treturn number The Ship's buoyancy factor
-- @export

--- Whether the Ship is static during the physics tick
-- @function isStatic
-- @treturn boolean Whether the Ship is static
-- @export

--- Whether the Ship is affected by fluid drag during the physics tick
-- @function doFluidDrag
-- @treturn boolean Whether the Ship has fluid drag
-- @export

--- Gets the Ship's Inertia Data during the physics tick
-- @function getInertiaData
-- @treturn table The Ship's Inertia Data comprised of mass and moment of inertia tensor
-- @export

--- Gets the Ship's Pose and Velocity during the physics tick
-- @function getPoseVel
-- @treturn table The Ship's Pose and Velocity data including position, rotation, linear velocity, and angular velocity
-- @export

--- Gets the Force Inducers on the Ship during the physics tick
-- @function getForceInducers
-- @treturn table A table of Force Inducers on the Ship
-- @export

if not ship then
    error("Cannot load Ship API on computer")
end

local native = ship.native or ship
local expect = dofile("rom/modules/main/cc/expect.lua").expect

local deprecatedQuat = {
    "getEulerAnglesZYX",
    "getEulerAnglesZXY",
    "getEulerAnglesYXZ",
    "getEulerAnglesXYZ",
    "getRoll",
    "getYaw",
    "getPitch"
}

local env = _ENV

for k,v in pairs(native) do
    if k == "getAngularVelocity" or k == "getScale" or k == "getShipyardPosition" or k == "getVelocity" or k == "getWorldspacePosition" or k == "transformPositionToWorld" then
        env[k] = function(...)
            local result, err = v(...)
            if err then
                error(err)
            end
            return vector.new(result.x, result.y, result.z)
        end
    elseif k == "getQuaternion" then
        env[k] = function(...)
            local result, err = v(...)
            if err then
                error(err)
            end
            return quaternion.fromComponents(result.x, result.y, result.z, result.w)
        end
    elseif k == "getTransformationMatrix" then
        env[k] = function(...)
            local result, err = v(...)
            if err then
                error(err)
            end
            return matrix.from2DArray(result)
        end
    elseif k == "getJoints" then
        env[k] = function(...)
            local result, err = native.getJoints(...)
            if err then
                error(err)
            end
            for id, joint in pairs(result) do
                joint.pose0.pos = vector.new(joint.pose0.pos.x, joint.pose0.pos.y, joint.pose0.pos.z)
                joint.pose0.rot = quaternion.fromComponents(joint.pose0.rot.x, joint.pose0.rot.y, joint.pose0.rot.z, joint.pose0.rot.w)
                joint.pose1.pos = vector.new(joint.pose1.pos.x, joint.pose1.pos.y, joint.pose1.pos.z)
                joint.pose1.rot = quaternion.fromComponents(joint.pose1.rot.x, joint.pose1.rot.y, joint.pose1.rot.z, joint.pose1.rot.w)
                if joint.type == "D6" then
                    if joint.drivePosition then
                        joint.drivePosition.pose.pos = vector.new(joint.drivePosition.pose.pos.x, joint.drivePosition.pose.pos.y, joint.drivePosition.pose.pos.z)
                        joint.drivePosition.pose.rot = quaternion.fromComponents(joint.drivePosition.pose.rot.x, joint.drivePosition.pose.rot.y, joint.drivePosition.pose.rot.z, joint.drivePosition.pose.rot.w)
                    end
                    if joint.driveVelocity then
                        joint.driveVelocity.linear = vector.new(joint.driveVelocity.linear.x, joint.driveVelocity.linear.y, joint.driveVelocity.linear.z)
                        joint.driveVelocity.angular = vector.new(joint.driveVelocity.angular.x, joint.driveVelocity.angular.y, joint.driveVelocity.angular.z)
                    end
                end
                result[id] = joint
            end
            return result
        end
    elseif k == "applyWorldTorque" or k == "applyModelTorque" or k == "applyBodyTorque" then
        env[k] = function(...)
            local args = {...}
            local torque = args[1]
            expect(1, torque, "table")
            if (getmetatable(torque) or {}).__name ~= "vector" then
                expect(1, torque, "vector")
            end
            local _, err = v(torque.x, torque.y, torque.z)
            if err then
                error(err)
            end
        end
    elseif k == "applyWorldForce" or k == "applyModelForce" or k == "applyBodyForce" then
        env[k] = function(...)
            local args = {...}
            local force = args[1]
            local pos = args[2]
            expect(1, force, "table")
            expect(2, pos, "table", "nil")
            if (getmetatable(force) or {}).__name ~= "vector" then
                expect(1, force, "vector")
            end
            if type(pos) == "table" and (getmetatable(pos) or {}).__name ~= "vector" then
                expect(2, pos, "vector", "nil")
            end
            local err
            if pos then
                _, err = v(force.x, force.y, force.z, pos.x, pos.y, pos.z)
            else
                _, err = v(force.x, force.y, force.z, nil, nil, nil)
            end
            if err then
                error(err)
            end
        end
    elseif k == "applyWorldForceToModelPos" or k == "applyWorldForceToBodyPos" then
        env[k] = function(...)
            local args = {...}
            local force = args[1]
            local pos = args[2]
            expect(1, force, "table")
            expect(2, pos, "table")
            if (getmetatable(force) or {}).__name ~= "vector" then
                expect(1, force, "vector")
            end
            if (getmetatable(pos) or {}).__name ~= "vector" then
                expect(2, pos, "vector")
            end
            local _, err = v(force.x, force.y, force.z, pos.x, pos.y, pos.z)
            if err then
                error(err)
            end
        end
    else
        env[k] = v
    end
end

for _, funct in pairs(deprecatedQuat) do
    env[funct] = function(...)
        error("This method no longer exists! Please utilize the new quaternion API!")
    end
end
env.getRotationMatrix = function(...) error("This method no longer exists! Use getTransformationMatrix instead!") end
env.applyInvariantForce = function(...) error("This method no longer exists! Use applyWorldForceToBodyPos instead!") end
env.applyInvariantForceToPos = function(...) error("This method no longer exists! Use applyWorldForceToBodyPos instead!") end
env.applyInvariantTorque = function(...) error("This method no longer exists! Use applyWorldTorque instead!") end
env.applyRotDependentForce = function(...) error("This method no longer exists! Use applyBodyForce instead!") end
env.applyRotDependentForceToPos = function(...) error("This method no longer exists! Use applyBodyForce instead!") end
env.applyRotDependentTorque = function(...) error("This method no longer exists! Use applyBodyTorque instead!") end
env.getOmega = function(...) error("This method no longer exists! Use getAngularVelocity instead!") end

env.pullPhysicsTicks = function(...)
    local _, err = native.pullPhysicsTicks(...)
    if err then
        error(err)
    end
    local event = table.pack(os.pullEvent("physics_ticks"))
    for k,v in pairs(event) do
        if type(v) == "table" then
            local result, _ = v.getPoseVel()
            v.getPoseVel = function()
                result.vel = vector.new(result.vel.x, result.vel.y, result.vel.z)
                result.omega = vector.new(result.omega.x, result.omega.y, result.omega.z)
                result.pos = vector.new(result.pos.x, result.pos.y, result.pos.z)
                result.rot = quaternion.fromComponents(result.rot.x, result.rot.y, result.rot.z, result.rot.w)
                return result
            end
        end
    end
    return table.unpack(event)
end