--- A basic quaternion type and some common quaternion operations. This may be useful
-- when working with rotation in regards to physics (such as those from the
-- @{ship} API).
--
-- An introduction to quaternions can be found on [Wikipedia][wiki].
--
-- [wiki]: https://en.wikipedia.org/wiki/Quaternion
--
-- Special thanks to getItemFromBlock and Shlomo for sharing their own quaternion handling code.
--
-- @module quaternion
-- @since 0.3.0

--- A quaternion, with `v` and `a` values.
--
-- This is suitable for representing rotation.
--
-- @type Quaternion
local quaternion = {

    --- Adds two quaternions together.
    --
    -- @tparam Quaternion self The first quaternion to add.
    -- @tparam Quaternion o The second quaternion to add.
    -- @treturn Quaternion The resulting quaternion
    -- @usage q1:add(q2)
    -- @usage q1 + q2
    add = function(self, o)
        return quaternion.new(
            self.v:add(o.v),
            self.a + o.a
        )
    end,

    --- Subtracts two quaternions together.
    --
    -- @tparam Quaternion self The first quaternion to subtract from.
    -- @tparam Quaternion o The second quaternion to subtract.
    -- @treturn Quaternion The resulting quaternion
    -- @usage q1:sub(q2)
    -- @usage q1 - q2
    sub = function(self, o)
        return quaternion.new(
            self.v:sub(o.v),
            self.a - o.a
        )
    end,

    --- Multiplies a quaternion by a scalar value, another quaternion, or a vector.
    --
    -- @tparam Quaternion self The quaternion to multiply.
    -- @tparam number, Quaternion, or Vector m The scalar value, quaternion, or vector to multiply with.
    -- @treturn Quaternion or Vector The resulting quaternion or rotated vector
    -- Note: If using a scalar value, the resulting quaternion will be non-normalized.
    -- @usage q:mul(3)
    -- @usage q * 3
    -- @usage q1:mul(q2)
    -- @usage q1 * q2
    -- @usage q:mul(v)
    -- @usage q * v
    mul = function(self, m)
        if type(m) == "table" then
            if getmetatable(m).__index == getmetatable(self).__index then
                -- Quaternion * Quaternion
                return quaternion.new(
                    m.v * self.a,
                    self.v * m.a,
                    self.v:cross(m.v),
                    self.a * m.a + -(self.v:dot(m.v))
                ):normalize()
            else
                -- Quaternion * Vector
                m_q = quaternion.new(m, 0)
                return (self * m_q * self:conjugate()).v
            end
        end
        if type(m) == "number" then
            -- Quaternion * Scalar
            return quaternion.new(
                self.v * m,
                self.a * m
            )
        end

        error("Invalid Argument! Takes a scalar value, a quaternion, or a vector to be rotated.")
    end,

    --- Divides a quaternion by another quaternion.
    --
    -- @tparam Quaternion self The quaternion to divide.
    -- @tparam Quaternion or number o The quaternion or scalar number to divide with.
    -- @treturn Quaternion The resulting quaternion
    -- Note: If using a scalar value, the resulting quaternion will be non-normalized.
    -- @usage q1:div(q2)
    -- @usage q1 / q2
    -- @usage q:div(2)
    -- @usage q / 2
    div = function(self, o)
        if type(m) == "table" and getmetatable(m).__index == getmetatable(self).__index then
            return self * o:inverse()
        end
        if type(m) == "number" then
            return self * (1 / m)
        end
        error("Invalid Argument! Takes a scalar value or a quaternion.")
    end,

    --- Negates a quaternion.
    --
    -- @tparam Quaternion self The quaternion to negate.
    -- @treturn Quaternion The resulting negated quaternion
    -- @usage q:unm()
    -- @usage -q
    unm = function(self)
        return self * -1
    end,

    --- Creates a string representation of the quaternion in the form of w + xi + yj + zk.
    --
    -- @tparam Quaternion self The quaternion to stringify.
    -- @treturn string The resulting string
    -- @usage q:tostring()
    -- @usage q .. ""
    tostring = function(self)
        return self.a.." + "..self.v.x.."i + "..self.v.y.."j + "..self.v.z.."k"
    end,

    --- Determines if the given quaternions are equal.
    --
    -- @tparam Quaternion self The quaternion to test against.
    -- @tparam Quaternion o The quaternion to test.
    -- @treturn boolean The resulting boolean
    -- @usage q1:equals(q2)
    -- @usage q1 == q2
    equals = function(self, o)
        return self.v == o.v and self.a == o.a
    end,

    --- Finds the conjugate of the quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn Quaternion The resulting conjugate
    -- @usage q:conjugate()
	conjugate = function(self)
		return quaternion.new(-self.v, self.a)
	end,

    --- Normalizes the quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn Quaternion The resulting normalized quaternion
    -- @usage q:normalize()
	normalize = function(self)
		local l = #self
		return quaternion.new(self.v / l, self.a / l)
	end,

    --- Finds the inverse of the quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn Quaternion The resulting inverse
    -- @usage q:inverse()
	inverse = function(self)
		if #self ^ 2 < 1e-5 then
			return self
		end
		return self:conjugate():normalize()
	end,

    --- Spherical Linear Interpolation between the given quaternions.
    --
    -- @tparam Quaternion self The origin quaternion.
    -- @tparam Quaternion 0 The target quaternion.
    -- @tparam number alpha The target step.
    -- @treturn Quaternion The resulting quaternion
    -- @usage q1:slerp(q2, alpha)
	slerp = function(self, o, alpha)
        self = self:normalize()
        o = o:normalize()
        local cos_half_theta = self.a * o.a + self.v.x * o.v.x + self.v.y * o.v.y + self.v.z * o.v.z;
        if cos_half_theta < 0 then
            o = -o;
            cos_half_theta = -cos_half_theta;
        end
        if math.abs(cos_half_theta) >= 1 then
            return self
        end
        local half_theta = math.acos(cos_half_theta);
        local sin_half_theta = math.sqrt(1 - cos_half_theta * cos_half_theta);
        if math.abs(sin_half_theta) < 0.001 then
            return self * 0.5 + o * 0.5;
        end
        local ratio_a = math.sin((1 - alpha) * half_theta) / sin_half_theta;
        local ratio_b = math.sin(alpha * half_theta) / sin_half_theta;
        return self * ratio_a + o * ratio_b;
    end,

    --- Gets the angle from the given quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn number The resulting angle
    -- @usage q:get_angle()
    get_angle = function(self)
        self = self:normalize()
        return 2 * math.acos(self.a)
    end,

    --- Gets the axis from the given quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn Vector The resulting axis
    -- @usage q:get_axis()
    get_axis = function(self)
        self = self:normalize()
        local factor = math.sqrt(1 - self.a * self.a)
        if factor == 0 then
            factor = 1
        end
        return self.v / factor
    end,

    --- Gets the roll, pitch, and yaw from the given quaternion in radians.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn number Roll
    -- @treturn number Pitch
    -- @treturn number Yaw
    -- @usage q:to_euler()
    to_euler = function(self)
        self = self:normalize()
        -- roll
        local roll = math.atan2(2 * (self.a * self.v.x + self.v.y * self.v.z), 1 - 2 * (self.v.x * self.v.x + self.v.y * self.v.y))

        local sin_pitch = 2 * (self.a * self.v.y - self.v.z * self.v.x)
        local pitch
        if math.abs(sin_pitch) >= 1 then
            pitch = math.pi / 2 * math.sign(sin_pitch)
        else
            pitch = math.asin(sin_pitch)
        end

        local yaw = math.atan2(2 * (self.a * self.v.z + self.v.x * self.v.y), 1 - 2 * (self.v.y * self.v.y + self.v.z * self.v.z))

        return roll, pitch, yaw
    end,

	length = function(self)
		return math.sqrt(self.a ^ 2 + self.v.x ^ 2 + self.v.y ^ 2 + self.v.z ^ 2)
	end,

	is_nan = function(self)
        return not (self.a ~= self.a or self.v.x ~= self.v.x or self.v.y ~= self.v.y or self.v.z ~= self.v.z)
    end
}

local vmetatable = {
    __index = quaternion,
    __add = quaternion.add,
    __sub = quaternion.sub,
    __mul = quaternion.mul,
    __div = quaternion.div,
    __unm = quaternion.unm,
    __len = quaternion.length,
    __tostring = quaternion.tostring,
    __eq = quaternion.equals,
}

function new(vec, w)
	return setmetatable({
        v = vec or vector.new(),
        a = tonumber(w) or 1,
    }, vmetatable)
end

function from_axis_angle(axis, angle)
    if not axis then
        axis = vector.new()
    else
        axis = axis:normalize()
    end
    angle = angle or 0
    local h_angle = angle / 2;
    return new(axis * math.sin(h_angle), math.cos(h_angle));
end

function from_euler(roll, pitch, yaw)
    roll = roll or 0
    pitch = pitch or 0
    yaw = yaw or 0
    return from_axis_angle(vector.new(1, 0, 0), roll) * from_axis_angle(vector.new(0, 1, 0), pitch) * from_axis_angle(vector.new(0, 0, 1), yaw)
end

function from_components(x, y, z, w)
    x = x or 0
    y = y or 0
    z = z or 0
    return new(vector.new(x, y, z), w)
end

function identity()
    return new()
end