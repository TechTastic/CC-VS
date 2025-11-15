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
            return vector.new(result.x, result.y, result.z)
        end
        error(err)
    end
end

-- Convert ship.getQuaternion to output a proper quaternion
env.getQuaternion = function(...)
    local result, err = native.getQuaternion(...)
    if result then
        return quaternion.fromComponents(result.x, result.y, result.z, result.w)
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