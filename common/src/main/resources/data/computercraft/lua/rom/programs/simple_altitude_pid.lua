-- CREDIT
-- Thanks to 19PHOBOSS98 for allowing me to add his PID controller setups to my project!
-- https://github.com/19PHOBOSS98/LUA_PID_LIBRARY/tree/main (MIT License)
-- https://github.com/19PHOBOSS98/LUA_PID_LIBRARY/blob/main/LICENSE

local args = {...}
local pidcontrollers = require "ccvs.pidcontrollers"

local target_altitude = tonumber(args[1]) or error("Missing Target Altitude!")
local output_side = args[2] or error("Missing Output Side!")
local P = tonumber(args[3] or 1)
local I = tonumber(args[4] or 1)
local D = tonumber(args[5] or 1)

--used for integral clamping--
local minimum_value = 0 
local maximum_value = 15 --redstone
--used for integral clamping--

local continuous_scalar_pid = pidcontrollers.PID_Continuous_Scalar(P, I, D, minimum_value, maximum_value)

local function getError()
    return target_altitude - ship.getWorldspacePosition().y
end

local error_value = getError()

while true do
	pid_value = continuous_scalar_pid:run(error_value)

	error_value = getError()
	
	redstone.setAnalogOutput(output_side, pid_value)
	
	os.sleep()
end