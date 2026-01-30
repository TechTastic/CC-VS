--- This API is added by CC: VS and allows CC: Tweaked computers to access information from Valkyrien Skies' Drag Controller.
--
-- @module drag

--- Gets the total drag force applied to this ship, in the form of a scaled vector
-- @function getDragForce
-- @treturn vector The total drag force applied to this ship
-- @raise This method errors if there is no Ship associated with the computer

--- Gets the total lift force applied to this ship, in the form of a scaled vector
-- @function getLiftForce
-- @treturn vector The total lift force applied to this ship
-- @raise This method errors if there is no Ship associated with the computer

--- Enables drag on this body if it were previously disabled
-- @function enableDrag
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Disables drag on this body, meaning it will not be affected by drag forces. Additionally, wind forces will default to the fallback Weather2 Compat implementation
-- @function disableDrag
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Enables lift on this body if it were previously disabled
-- @function enableLift
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Disables lift on this body, meaning it will not be affected by lift forces or torques
-- @function disableLift
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Enables rotational drag on this body if it were previously disabled
-- @function enableRotDrag
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Disables rotational drag on this body, meaning it will not be affected by rotational drag forces or torques
-- @function disableRotDrag
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Sets the constant wind direction for this ship
-- @function setWindDirection
-- @tparam vector direction The new constant wind direction
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Sets the constant wind speed for this ship
-- @function setWindSpeed
-- @tparam number speed The new constant wind speed, clamped to 0 minimum
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

--- Applies a wind impulse to the ship in the specified direction with the given speed. Speed should not be negative. Please flip the direction vector instead
-- @function applyWindImpulse
-- @tparam vector direction The direction of the wind impulse, should be a normalized vector
-- @tparam number speed The speed of the wind impulse, should be a positive value
-- @raise This method errors if there is no Ship associated with the computer OR if the computer is not a Command Computer and the configuration disallows it.

if not drag then
    error("Cannot load Drag API on computer")
end

local native = drag.native or drag
local expect = dofile("rom/modules/main/cc/expect.lua").expect

local env = _ENV

for k,v in pairs(native) do
    if k == "getDragForce" or k == "getLiftForce" then
        env[k] = function(...)
            local result, err = v(...)
            if err then
                error(err)
            end
            return vector.new(result.x, result.y, result.z)
        end
    elseif k == "setWindDirection" then
        env[k] = function(...)
            local args = {...}
            local direction = args[1]
            expect(1, direction, "table")
            if (getmetatable(direction) or {}).__name ~= "vector" then
                expect(1, direction, "vector")
            end
            local _, err = v(direction.x, direction.y, direction.z)
            if err then
                error(err)
            end
        end
    elseif k == "applyWindImpulse" then
        env[k] = function(...)
            local args = {...}
            local direction = args[1]
            local speed = args[2]
            expect(1, direction, "table")
            expect(2, speed, "number")
            if (getmetatable(direction) or {}).__name ~= "vector" then
                expect(1, direction, "vector")
            end
            if direction:length() ~= 1 then
                expect(1, direction, "normalized vector")
            end
            local _, err = v(direction.x, direction.y, direction.z, speed)
            if err then
                error(err)
            end
        end
    else
        env[k] = v
    end
end