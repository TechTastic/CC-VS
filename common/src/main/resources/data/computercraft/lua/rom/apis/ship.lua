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