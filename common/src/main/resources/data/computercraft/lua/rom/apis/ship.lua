if not ship then
    error("Cannot load command API on normal computer")
end

local native = ship.native or ship

local env = _ENV
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