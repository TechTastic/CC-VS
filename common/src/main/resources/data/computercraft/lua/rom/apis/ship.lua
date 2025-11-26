--- Ship API
--
-- This API is added by CC: VS and allows CC: Tweaked computers to access information from Valkyrien Skies Ships.
-- @module ship_api

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

--- Gets the constraints on the Ship
-- @function getConstraints
-- @treturn table A table of constraints on the Ship
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
-- @function getOmega
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

--- Physics Ticks Event Data
--
-- This section describes the data returned by the physics ticks event.
-- @section physics_ticks_event_data

--- Gets the Ship's buoyancy factor during the physics tick
-- @function getBuoyancyFactor
-- @treturn number The Ship's buoyancy factor

--- Whether the Ship is static during the physics tick
-- @function isStatic
-- @treturn boolean Whether the Ship is static

--- Whether the Ship is affected by fluid drag during the physics tick
-- @function doFluidDrag
-- @treturn boolean Whether the Ship has fluid drag

--- Gets the Ship's Inertia Data during the physics tick
-- @function getInertiaData
-- @treturn table The Ship's Inertia Data comprised of mass and moment of inertia tensor

--- Gets the Ship's Pose and Velocity during the physics tick
-- @function getPoseVel
-- @treturn table The Ship's Pose and Velocity data including position, rotation, linear velocity, and angular velocity

--- Gets the Force Inducers on the Ship during the physics tick
-- @function getForceInducers
-- @treturn table A table of Force Inducers on the Ship

--- Extended Ship API
--
-- This section contains extended functions for the Ship API that provide additional functionality
-- beyond the basic Ship operations.
-- As of CC: VS 0.5.0, these functions have been rolled into the main Ship API for better usability.
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
-- if the new position intersects with other objects or violates physics constraints.
--
-- Note: This function may cause physics instability if used improperly.
-- Use with caution.
--
-- @function teleport
-- @tparam table The new position and orientation for the Ship
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it OR if this method is disabled in the configuration.

--- Applies an invariant force to the Ship
--
-- An invariant force is applied in world coordinates and does not change with the Ship's rotation.
--
-- @function applyInvariantForce
-- @tparam vector|number x Either a vector representing the force, or the X component of the force
-- @tparam number|nil y The Y component of the force (if x is a number)
-- @tparam number|nil z The Z component of the force (if x is a number)
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies an invariant torque to the Ship
--
-- An invariant torque is applied in world coordinates and does not change with the Ship's rotation.
--
-- @function applyInvariantTorque
-- @tparam vector|number x Either a vector representing the torque, or the X component of the torque
-- @tparam number|nil y The Y component of the torque (if x is a number)
-- @tparam number|nil z The Z component of the torque (if x is a number)
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies an invariant force to the Ship at a specific position
--
-- An invariant force is applied in world coordinates and does not change with the Ship's rotation.
--
-- @function applyInvariantForceToPos
-- @tparam vector|number fx Either a vector representing the force, or the X component of the force
-- @tparam vector|number fy Either a vector representing the position, or the Y component of the force
-- @tparam number|nil fz The Z component of the force (if fx is a number)
-- @tparam number|nil px The X component of the position (if pos is a number)
-- @tparam number|nil py The Y component of the position (if pos is a number)
-- @tparam number|nil pz The Z component of the position (if pos is a number)
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a rotation-dependent force to the Ship
--
-- A rotation-dependent force is applied in the Ship's local coordinate system and changes with the Ship's rotation.
--
-- @function applyRotDependentForce
-- @tparam vector|number x Either a vector representing the force, or the X component of the force
-- @tparam number|nil y The Y component of the force (if x is a number)
-- @tparam number|nil z The Z component of the force (if x is a number)
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a rotation-dependent torque to the Ship
--
-- A rotation-dependent torque is applied in the Ship's local coordinate system and changes with the Ship's rotation.
--
-- @function applyRotDependentTorque
-- @tparam vector|number x Either a vector representing the torque, or the X component of the torque
-- @tparam number|nil y The Y component of the torque (if x is a number)
-- @tparam number|nil z The Z component of the torque (if x is a number)
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a rotation-dependent force to the Ship at a specific position
--
-- A rotation-dependent force is applied in the Ship's local coordinate system and changes with the Ship's rotation.
--
-- @function applyRotDependentForceToPos
-- @tparam vector|number fx Either a vector representing the force, or the X component of the force
-- @tparam vector|number fy Either a vector representing the position, or the Y component of the force
-- @tparam number|nil fz The Z component of the force (if fx is a number)
-- @tparam number|nil px The X component of the position (if pos is a number)
-- @tparam number|nil py The Y component of the position (if pos is a number)
-- @tparam number|nil pz The Z component of the position (if pos is a number)
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Deprecated Methods
--
-- The following methods have been deprecated and are no longer available.
-- Please refer to the updated Ship API documentation for alternative methods.
-- @section deprecated

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

-- Add outdated methods to prompt transition
for _, funct in pairs(deprecatedQuat) do
    env[funct] = function(...)
        error("This method no longer exists! Please utilize the new quaternion API!")
    end
end
env.getRotationMatrix = function(...) error("This method no longer exists! Use getTransformationMatrix instead!") end

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

for k,v in pairs(native) do
    -- Convert functions with vector outputs to actual vectors
    if k == 'getOmega' or k == 'getScale' or k == 'getShipyardPosition' or k == 'getVelocity' or k == 'getWorldspacePosition' or k == 'transformPositionToWorld' then
        env[k] = function(...)
            local result, err = v(...)
            if err then
                error(err)
            end
            return vector.new(result.x, result.y, result.z)
        end
    elseif k == 'getQuaternion' then
        env[k] = function(...)
            local result, err = v(...)
            if err then
                error(err)
            end
            return quaternion.fromComponents(result.x, result.y, result.z, result.w)
        end
    elseif k == 'getTransformationMatrix' then
        env[k] = function(...)
            local result, err = v(...)
            if err then
                error(err)
            end
            return matrix.from2DArray(result)
        end
    elseif k == 'getConstraints' then
        local result, err = native.getTransformationMatrix(...)
        if err then
            error(err)
        end
        for id, constraint in pairs(result) do
            if constraint.localPos0 then
                constraint.localPos0 = vector.new(constraint.localPos0.x, constraint.localPos0.y, constraint.localPos0.z)
            end
            if constraint.localPos1 then
                constraint.localPos1 = vector.new(constraint.localPos1.x, constraint.localPos1.y, constraint.localPos1.z)
            end
            if constraint.localRot0 then
                constraint.localRot0 = quaternion.fromComponents(constraint.localRot0.x, constraint.localRot0.y, constraint.localRot0.z, constraint.localRot0.w)
            end
            if constraint.localRot1 then
                constraint.localRot1 = quaternion.fromComponents(constraint.localRot1.x, constraint.localRot1.y, constraint.localRot1.z, constraint.localRot1.w)
            end
            if constraint.localSlideAxis0 then
                constraint.localSlideAxis0 = vector.new(constraint.localSlideAxis0.x, constraint.localSlideAxis0.y, constraint.localSlideAxis0.z)
            end
            result[id] = constraint
        end
        return result
    elseif k == 'applyInvariantForce' or k == 'applyInvariantTorque' or k == 'applyRotDependentForce' or k == 'applyRotDependentTorque' then
        env[k] = function(...)
            local args = {...}
            local vec = args[1]
            expect(1, vec, "table", "number")
            if type(vec) == "table" and (getmetatable(vec) or {}).__name ~= "vector" then
                expect(1, vec, "vector", "number")
            end
            local err
            if type(vec) == "table" then
                _, err = v(vec.x, vec.y, vec.z)
            else
                _, err = v(...)
            end
            if err then
                error(err)
            end
        end
    elseif k == 'applyInvariantForceToPos' or k == 'applyRotDependentForceToPos' then
        env[k] = function(...)
            local args = {...}
            expect(1, args[1], "table", "number")
            expect(2, args[2], "table", "number")
            local firstVec = args[1]
            if type(firstVec) == "table" and (getmetatable(firstVec) or {}).__name ~= "vector" then
                expect(1, firstVec, "vector", "number")
            end
            local secondVec = args[2]
            if type(secondVec) == "table" and (getmetatable(firstVec) or {}).__name ~= "vector" then
                expect(2, secondVec, "vector", "number")
            end

            local err
            if type(firstVec) == "number" and type(secondVec) == "number" then
                expect(3, args[3], "number")
                expect(4, args[4], "number")
                expect(5, args[5], "number")
                expect(6, args[6], "number")
                _, err = v(...)
            elseif type(firstVec) == "table" and type(secondVec) == "table" then
                _, err = v(firstVec.x, firstVec.y, firstVec.z, secondVec.x, secondVec.y, secondVec.z)
            else
                local argstr = "["
                for i, arg in pairs(args) do
                    local type = type(arg)
                    argstr = argstr .. type
                    if i == #args then
                        argstr = argstr .. "]"
                    else
                        argstr = argstr .. ", "
                    end
                end
                err = ("bad arguments #%d and #%d (%s expected, got %s)"):format(1, 2, "[vector, vector] or [number, number, number, number, number, number]", argstr)
            end
            if err then
                error(err)
            end
        end
    else
        env[k] = v
    end
end