if not ship then
    error("Cannot load command API on normal computer")
end

local native = ship.native or ship

local function outdatedQuat(...) error("This method no longer exists! Please utilize the new quaternion API!") end

local env = _ENV
env["getEulerAnglesZYX"] = outdatedQuat
env["getEulerAnglesZXY"] = outdatedQuat
env["getEulerAnglesYXZ"] = outdatedQuat
env["getEulerAnglesXYZ"] = outdatedQuat
env["getRoll"] = outdatedQuat
env["getYaw"] = outdatedQuat
env["getPitch"] = outdatedQuat
env["getRotationMatrix"] = function(...) error("This metohd no longer exists! Use getTransformationMatrix instead!") end
for k, v in pairs(native) do
    if k == "getOmega" or k == "getScale" or k == "getShipyardPosition" or k == "getVelocity" or k == "getWorldspacePosition" or k == "transformPositionToWorld" then
        env[k] = function(...)
            local result, err = v(...)
            if result then
                return vector.new(result.x, result.y, result.z)
            end
            error(err)
        end
    elseif k == "getQuaternion" then
        env[k] = function(...)
            local result, err = v(...)
            if result then
                return quaternion.fromComponents(result.x, result.y, result.z, result.w)
            end
            error(err)
        end
    else
        env[k] = v
    end
end