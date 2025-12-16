--- This API is added by CC: VS and allows CC: Tweaked computers to access information from Valkyrien Skies' Aerodynamics Utilities.
--
-- @module aerodynamics

--- Gets the default maximum constant atmospheric height
-- @function getDefaultMax
-- @treturn number The default maximum atmospheric height constant

--- Gets the default sea level constant for atmospheric calculations
-- @function getDefaultSeaLevel
-- @treturn number The default sea level constant

--- Gets the drag coefficient constant for atmospheric calculations
-- @function getDragCoefficient
-- @treturn number The drag coefficient constant

--- Gets the gravitational acceleration constant for atmospheric calculations
-- @function getGravitationalAcceleration
-- @treturn number The gravitational acceleration constant

--- Gets the universal gas constant for atmospheric calculations
-- @function getUniversalGasConstant
-- @treturn number The universal gas constant

--- Gets the air molar mass constant for atmospheric calculations
-- @function getAirMolarMass
-- @treturn number The air molar mass constant

--- Gets the atmospheric parameters for the dimension
-- @function getAtmosphericParameters
-- @treturn table A table with the maxY, sealevel, and gravity keys

--- Gets the density of air at a Y value adapted to a real life altitude, where Y = 60 is sea level, and Y = 320 is the top of the Troposphere
-- @function getAirDensity
-- @tparam number|nil y The Y level, defaults to the computer's World Space position
-- @treturn number The air density at the given Y value, in kg/m^3

--- Gets the air pressure at Y
-- @function getAirPressure
-- @tparam number|nil y The Y level, defaults to the computer's World Space position
-- @treturn number The air pressure at the given Y value, in Pascals (Pa)

--- Gets the air temperature at Y
-- @function getAirTemperature
-- @tparam number|nil y The Y level, defaults to the computer's World Space position
-- @treturn number he air temperature at the given Y value, in Kelvin (K)

if not aerodynamics then
    error("Cannot load Aerodynamics API on computer")
end

local native = aero.native or aero
local env = _ENV

for k,v in pairs(native) do
    env[k] = v
end