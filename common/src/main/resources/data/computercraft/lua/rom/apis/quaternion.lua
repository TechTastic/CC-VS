--- A basic quaternion type and some common quaternion operations. This may be useful
-- when working with rotation in regards to physics (such as those from the
-- @{ship} API).
--
-- An introduction to quaternions can be found on [Wikipedia][wiki].
--
-- [wiki]: https://en.wikipedia.org/wiki/Quaternion
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
    -- @usage q:mul(3)
    -- @usage q * 3
    -- @usage q1:mul(q2)
    -- @usage q1 * q2
    -- @usage q:mul(v)
    -- @usage q * v
    mul = function(self, m)
        if type(m) == "table" then
            if getmetatable(o) == quaternion then
                -- Quaternion * Quaternion
                return quaternion.new(
                    m.v:mul(self.a),
                    self.v:mul(m.a),
                    self.v:cross(m.v),
                    self.a * m.a + -(self.v:dot(m.v))
                )
            else
                -- Quaternion * Vector
                m_q = quaternion.new(m, 0)
                return (self * m_q * self:conjugate()).v
            end
        else
            -- Quaternion * Scalar
            return quaternion.new(
                self.v * m,
                self.a * m
            )
        end
    end,

    --- Divides a quaternion by another quaternion.
    --
    -- @tparam Quaternion self The quaternion to divide.
    -- @tparam Quaternion o The quaternion to divide with.
    -- @treturn Quaternion The resulting quaternion
    -- @usage q1:div(q2)
    -- @usage q1 / q2
    div = function(self, o)
        return self * o:inverse()
    end,

    unm = function(self)
        return self:mul(-1)
    end,

    idiv = function(self, o)
        quotient = self / o
        return quaternion.new(
            vector.new(
                math.floor(quotient.x),
                math.floor(quotient.y),
                math.floor(quotient.z)
            ),
            math.floor(quotient.a)
        )
    end,

    tostring = function(self)
        return self.a.." + "..self.v.x.."i + "..self.v.y.."j + "..self.v.z.."k"
    end,

    equals = function(self, o)
        return self.v == o.v and self.a == o.a
    end,

    --- Finds the conjugate of the quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn Quaternion The resulting conjugate
    -- @usage q:conjugate()
	conjugate = function(self)
		return quaternion.new(-self.v,self.a)
	end,

    --- Normalizes the quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn Quaternion The resulting normalized quaternion
    -- @usage q:normalize()
	normalize = function(self)
		local l = self:length()
		return quaternion.new(self.v:div(l), self.a/l)
	end,

    --- Finds the inverse of the quaternion.
    --
    -- @tparam Quaternion self The quaternion.
    -- @treturn Quaternion The resulting inverse
    -- @usage q:inverse()
	inverse = function(self)
		if self:lengthSq() < 1e-5 then
			return self
		end
		local tmp = self:conjugate()
		return tmp:normalize()
	end,

	lengthSq = function(self)
		return self.v.x*self.v.x + self.v.y*self.v.y + self.v.z*self.v.z + self.a*self.a
	end,

	len = function(self)
		return math.sqrt(self:lengthSq())
	end,
}

local vmetatable = {
    __index = quaternion,
    __add = quaternion.add,
    __sub = vector.sub,
    __mul = quaternion.mul,
    __div = quaternion.div,
    __unm = quaternion.unm,
    __idiv = quaternion.idiv
    __len = quaternion.len
    __tostring = quaternion.tostring,
    __eq = quaternion.equals,
}

function new(vec, w)
	return setmetatable({
        v = vec or vector.new(),
        a = tonumber(w) or 1,
    }, vmetatable)
end