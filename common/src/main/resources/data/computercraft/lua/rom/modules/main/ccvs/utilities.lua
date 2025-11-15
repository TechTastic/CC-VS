
-- CREDIT
-- utilities.lua by 19PHOBOSS98 from https://github.com/19PHOBOSS98/LUA_PID_LIBRARY/tree/main (MIT License)
-- https://github.com/19PHOBOSS98/LUA_PID_LIBRARY/blob/main/LICENSE

--PHOBOSS--
--[[ 
function clampB(x, min, max) --benchmark speed: 0.076612 seconds
    return math.max(math.min(x, max), min)
end

function clampC(x, min, max) --benchmark speed: 0.030656 seconds
    return x < min and min or x > max and max or x
end

local n = 1e6
local function benchmarkingTimer(f, n)
  local clock = os.clock
  local before = clock()
  minn = -5
  maxx = 5
  for i=1,n do
    f(i,minn,maxx)
  end
  local after = clock()
  return after-before
end

print(string.format("clamp A took %f seconds", benchmarkingTimer(clampA, n)))
print(string.format("clamp B Took %f seconds", benchmarkingTimer(clampB, n)))
print(string.format("clamp C Took %f seconds", benchmarkingTimer(clampC, n)))
]]--

local utilities = {}

function utilities.clamp(x, min, max)--benchmark speed: 0.027751 seconds
    if x < min then return min end
    if x > max then return max end
    return x
end

--[[
-- fast but we can do without it returning 0
function sign(x)
  return x>0 and 1 or x<0 and -1 or 0
end
]]--

--Thanks to rv55 from: https://stackoverflow.com/questions/1318220/lua-decimal-sign
function utilities.sign(x) --faster, caution: doesn't return 0
  return x<0 and -1 or 1
end

function utilities.clamp_vector3(vec,minn,maxx)
	return vector.new(utilities.clamp(vec.x,minn,maxx),utilities.clamp(vec.y,minn,maxx),utilities.clamp(vec.z,minn,maxx))
end

function utilities.sign_vector3(vec)
	return vector.new(utilities.sign(vec.x),utilities.sign(vec.y),utilities.sign(vec.z))
end

function utilities.abs_vector3(vec)
	return vector.new(math.abs(vec.x),math.abs(vec.y),math.abs(vec.z))
end

function utilities.roundTo(value,place)
	return math.floor(value * place)/place
end

function utilities.roundTo_vector3(value,place)
	return vector.new(math.floor(value.x * place)/place,math.floor(value.y * place)/place,math.floor(value.z * place)/place)
end

function utilities.round_vector3(value)
	return vector.new(math.floor(value.x + 0.5),math.floor(value.y + 0.5),math.floor(value.z + 0.5))
end


--thanks to FrancisPostsHere: https://www.youtube.com/watch?v=ZfRaYTPUHCU
--https://pastebin.pl/view/e157c3e2
function utilities.quadraticSolver(a,b,c)--at^2 + bt + c = 0
	local sol_1=nil
	local sol_2=nil
	
	local discriminator = (b*b) - (4*a*c)
	local discriminator_squareroot = math.sqrt(math.abs(discriminator))
	local denominator = 2*a
	
	if (discriminator==0) then
		sol_1 = -b/d
		return discriminator,sol_1,sol_1
	elseif (discriminator>0) then
		sol_1 = ((-b)+discriminator_squareroot)/denominator
		sol_2 = ((-b)-discriminator_squareroot)/denominator
		return discriminator,sol_1,sol_2
	end
	
	return discriminator,sol_1,sol_2--I would use complex imaginary numbers but... meh
end


--distributed PWM redstone algorithm
--[[Thanks to NikZapp: https://www.youtube.com/channel/UCzlyClqJtuPS3IgHOtdP_Jw]]--
function utilities.pwm()
	return{
	last_output_float_error=vector.new(0,0,0),
	run=function(self,rs)
		pid_out_w_error = rs:add(self.last_output_float_error)
		output = utilities.round_vector3(pid_out_w_error)
		self.last_output_float_error = pid_out_w_error:sub(output)
		return output
	end
	}
end


function utilities.IntegerScroller(value,minimum,maximum)
	return{
		value=value,
		maximum = maximum,
		minimum = minimum,
		override=function(self,new_value)
			value = utilities.clamp(new_value, minimum, maximum)
		end,
		set=function(self,delta)
			value = utilities.clamp(value+delta, minimum, maximum)
		end,
		get=function(self)
			return value
		end
	}
end

return utilities