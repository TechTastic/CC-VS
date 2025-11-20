if not ship then
    error("Cannot load API on computer")
end

local native = ship.native or ship

local deprecatedQuat = {
    "getEulerAnglesZYX",
    "getEulerAnglesZXY",
    "getEulerAnglesYXZ",
    "getEulerAnglesXYZ",
    "getRoll",
    "getYaw",
    "getPitch"
}
local vectorFunctions = {
    "getOmega",
    "getScale",
    "getShipyardPosition",
    "getVelocity",
    "getWorldspacePosition",
    "transformPositionToWorld"
}

local env = _ENV

local function toVector(v)
    return vector.new(v.x, v.y, v.z)
end

local function toQuaternion(q)
    return quaternion.fromComponents(q.x, q.y, q.z, q.w)
end

-- Add outdated methods to prompt transition
for _, funct in pairs(deprecatedQuat) do
    env[funct] = function(...)
        error("This method no longer exists! Please utilize the new quaternion API!")
    end
end
env.getRotationMatrix = function(...) error("This method no longer exists! Use getTransformationMatrix instead!") end

-- Convert functions with vector outputs to actual vectors
for _, funct in pairs(vectorFunctions) do
    env[funct] = function(...)
        local result, err = native[funct](...)
        if result then
            return toVector(result)
        end
        error(err)
    end
end

-- Convert ship.getQuaternion to output a proper quaternion
env.getQuaternion = function(...)
    local result, err = native.getQuaternion(...)
    if result then
        return toQuaternion(result)
    end
    error(err)
end

-- Convert ship.getTransformationMatrix to output a proper quaternion
env.getTransformationMatrix = function(...)
    local result, err = native.getTransformationMatrix(...)
    if result then
        return matrix.from2DArray(result)
    end
    error(err)
end

-- Convert ship.getConstraints to output proper stuff
env.getConstraints = function(...)
    local result, err = native.getTransformationMatrix(...)
    if result then
        for id, constraint in pairs(result) do
            if constraint.localPos0 then
                constraint.localPos0 = toVector(constraint.localPos0)
            end
            if constraint.localPos1 then
                constraint.localPos1 = toVector(constraint.localPos1)
            end
            if constraint.localRot0 then
                constraint.localRot0 = toQuaternion(constraint.localRot0)
            end
            if constraint.localRot1 then
                constraint.localRot1 = toQuaternion(constraint.localRot1)
            end
            if constraint.localSlideAxis0 then
                constraint.localSlideAxis0 = toVector(localSlideAxis0)
            end
            result[id] = constraint
        end
        return result
    end
    error(err)
end

-- Catch "physics_ticks" event and convert all vectos and quaternions in each output to proper versions
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
               result.vel = toVector(result.vel)
               result.omega = toVector(result.omega)
               result.pos = toVector(result.pos)
               result.rot = toQuaternion(result.rot)
               return result
           end
       end
   end
   return table.unpack(event)
end