--- A grab bag of common descriptive and inferential statistical functions. Useful for statistical analysis.
--
-- Credit goes to sans.9536 of the Minecraft Computer Mods discord for these functions and the original file.
--
-- @module stats
-- @author sans.9536

local expect = require "cc.expect"
local expect = expect.expect
local stats = {}

--- Basic Descriptive Statistics
--
-- @section basic_descriptive_statistics

--- Computes the sum of numeric values in the dataset
--
-- @tparam number[] data sequential table of numbers
-- @treturn number sum of values (0 if empty)
function stats.sum(data)
    expect(1, data, "table")
    local s = 0
    for _, v in pairs(data) do
        if type(v) ~= "number" then expect(1, data, "sequential table of numbers") end
        s = s + v
    end
    return s
end

--- Computes the arithmetic mean (average) of the dataset
--
-- @tparam number[] data
-- @treturn number|nil mean or nil if data empty
function stats.mean(data)
    local n = #data
    return n > 0 and stats.sum(data) / n or nil
end

--- Computes the minimum value of the dataset
--
-- @tparam number[] data
-- @treturn number|nil minimum value or nil if data empty
function stats.min(data)
    expect(1, data, "table")
    table.sort(data)
    return data[1]
end

--- Computes the maximum value of the dataset
--
-- @tparam number[] data
-- @treturn number|nil maximum value or nil if data empty
function stats.max(data)
    expect(1, data, "table")
    table.sort(data)
    return data[#data]
end

--- Computes population variance by default. Set `is_sample` to true for sample variance
--
-- @tparam number[] data
-- @tparam[opt=false] boolean is_sample true to compute sample variance (divide by n-1)
-- @treturn number|nil variance or nil if not defined
function stats.variance(data, is_sample)
    expect(1, data, "table")
    if is_sample ~= nil then expect(2, is_sample, "boolean") end
    if #data == 0 then return nil end
    local sum_sq = 0
    for _, v in pairs(data) do
        sum_sq = sum_sq + (v - stats.mean(data)) ^ 2
    end
    local denom = (is_sample and #data - 1 or #data)
    if denom <= 0 then return nil end
    return sum_sq / denom
end

--- Computes the standard deviation of the dataset using sample variance
--
-- @tparam number[] data
-- @tparam[opt=false] boolean is_sample true to compute sample stdev
-- @treturn number|nil standard deviation or nil if not defined
function stats.stdev(data, is_sample)
    local v = stats.variance(data, is_sample)
    return v and math.sqrt(v) or nil
end

--- Number of elements in the dataset
--
-- @tparam table data
-- @treturn number size (n)
function stats.size(data)
    expect(1, data, "table")
    return #data
end

--- Computes the range of the dataset
--
-- @tparam number[] data
-- @treturn number|nil range or nil if undefined
function stats.range(data)
    return stats.min(data) and stats.max(data) and stats.max(data) - stats.min(data) or nil
end

--- Computes the median (50th percentile)
-- If even length, returns average of the two middle values
--
-- @tparam number[] data
-- @treturn number|nil median or nil if empty
function stats.median(data)
    expect(1, data, "table")
    if #data == 0 then return nil end
    local t = { table.unpack(data) }
    table.sort(t)
    local n = #t
    if n % 2 == 1 then
        return t[(n + 1) / 2]
    else
        return (t[n / 2] + t[(n / 2) + 1]) / 2
    end
end

--- Computes the mode and returns a sorted array of the most frequent value(s)
--
-- @tparam table data
-- @treturn table list of mode values (empty table if data empty)
function stats.mode(data)
    expect(1, data, "table")
    local counts = {}
    local maxc = 0

    for _, v in pairs(data) do
        counts[v] = (counts[v] or 0) + 1
        if counts[v] > maxc then
            maxc = counts[v]
        end
    end
    if maxc == 0 then
        return {}
    end

    local modes = {}
    for v, c in pairs(counts) do
        if c == maxc then
            table.insert(modes, v)
        end
    end
    table.sort(modes)
    return modes
end

--- Computes the standard error of the mean (commonly called SE)
-- Indicates precision
--
-- @tparam number[] data
-- @treturn number|nil standard error or nil if undefined
function stats.sex(data)
    return #data > 0 and stats.stdev(data, true) / math.sqrt(#data) or nil
end
--- Computes the asymmetry (skewness) of data around its mean
-- Requires at least 3 observations
--
-- @tparam number[] data
-- @treturn number|nil skewness or nil if undefined
function stats.skewness(data)
    expect(1, data, "table")
    local n = #data
    if n < 3 then return nil end
    local mu, sd = stats.mean(data), stats.stdev(data, true)
    if not sd or sd == 0 then return nil end
    local m3 = 0
    for _, v in pairs(data) do
        m3 = m3 + ((v - mu) / sd) ^ 3
    end
    return n / ((n - 1) * (n - 2)) * m3
end

--- Computes the messure of skew (kurtosis) using an excess kurtosis formula variant
-- Requires at least 4 observations and indicates how much peak or tail a distribution would have
--
-- @tparam number[] data
-- @treturn number|nil kurtosis or nil if undefined
function stats.kurtosis(data)
    expect(1, data, "table")
    local n = #data
    if n < 4 then return nil end
    local mu, sd = stats.mean(data), stats.stdev(data, true)
    if not sd or sd == 0 then return nil end
    local m4 = 0
    for _, v in pairs(data) do
        m4 = m4 + ((v - mu) / sd) ^ 4
    end
    return (n * (n + 1) / ((n - 1) * (n - 2) * (n - 3))) * m4
        - (3 * (n - 1) ^ 2) / ((n - 2) * (n - 3))
end

--- Alternative Means
--
-- @section alternative_means

--- Computes the geometric mean
-- Only defined for strictly positive values
-- Useful for multiplicative rates such as combining percentage growth rates
--
-- @tparam number[] data
-- @treturn number|nil geometric mean or nil if any value <= 0 or data empty
function stats.geometric_mean(data)
    expect(1, data, "table")
    local prod, n = 1, #data
    if n == 0 then return nil end
    for _, v in pairs(data) do
        if type(v) ~= "number" or math.abs(v) ~= v then expect(1, data, "sequential table of positive numbers") end
        if v <= 0 then return nil end
        prod = prod * v
    end
    return prod ^ (1 / n)
end

--- Computes the harmonic mean
-- Best used for average rates and ratios, or inverse proportionalities
--
-- @tparam number[] data
-- @treturn number|nil harmonic mean or nil if any value == 0 or data empty
function stats.harmonic_mean(data)
    expect(1, data, "table")
    local sumr, n = 0, #data
    if n == 0 then return nil end
    for _, v in pairs(data) do
        if type(v) ~= "number" or v == 0 or math.abs(v) ~= v then expect(1, data, "sequential table of positive non-zero numbers") end
        if v == 0 then return nil end
        sumr = sumr + 1 / v
    end
    return n / sumr
end

--- Computes the trimmed mean
-- Removes `trim` fraction from each tail and averages the rest
-- Much more reliable measure of central tendency, clamps down outliers by removing a percentage of extremes
--
-- @tparam number[] data
-- @tparam[opt=0.05] number trim fraction to trim from each tail
-- @treturn number|nil trimmed mean or nil if undefined
function stats.trimmed_mean(data, trim)
    if trim ~= nil then
        expect(2, trim, "number")
        if math.abs(trim) ~= trim then
            expect(2, trim, "non-negative number")
        end
    end
    trim = trim or 0.05
    local t = { table.unpack(data) }
    table.sort(t)
    local n = #t
    if n == 0 then return nil end
    local tn = math.floor(trim * n)
    local s = 0
    for i = tn + 1, n - tn do
        if type(t[i]) ~= "number" then expect(1, data, "sequential table of numbers") end
        s = s + t[i]
    end
    local denom = n - 2 * tn
    if denom <= 0 then return nil end
    return s / denom
end

--- Computes the winsorized mean
-- Clamps extreme values to given quantiles before averaging
-- Much more reliable measure of central tendency
--
-- @tparam number[] data
-- @tparam[opt=0.05] number alpha fraction to winsorize in each tail
-- @treturn number|nil winsorized mean or nil if empty
function stats.winsor_mean(data, alpha)
    if alpha ~= nil then
        expect(2, alpha, "number")
        if math.abs(alpha) ~= alpha then
            expect(2, alpha, "non-negative number")
        end
        if alpha > 0.5 then execpt(2, alpha, "number less than or equal to 0.5")
    end
    alpha = alpha or 0.05
    local t = { table.unpack(data) }
    table.sort(t)
    local n = #t
    if n == 0 then return nil end
    if alpha == 0 then return stats.mean(t) end

    local lower = math.floor(alpha * n) + 1
    local upper = math.ceil((1 - alpha) * n)
    local winsor_t = { table.unpack(t) }

    local lower_bound = t[lower]
    local upper_bound = t[upper]
    for i = 1, lower - 1 do winsor_t[i] = lower_bound end
    for i = upper + 1, n do winsor_t[i] = upper_bound end

    return stats.mean(winsor_t)
end

--- Computes the weighted mean
--
-- @tparam number[] data
-- @tparam number[] weights same length as data
-- @treturn number|nil weighted mean or nil if lengths mismatch or zero total weight
function stats.weighted_mean(data, weights)
    expect(1, data, "table")
    expect(2, weights, "table")
    local n = #data
    if n == 0 or #weights ~= n then return nil end
    local num, den = 0, 0
    for i = 1, n do
        if type(data[i]) ~= "number" then expect(1, data, "sequential table of numbers") end
        if type(weights[i]) ~= "number" then expect(2, weights, "sequential table of numbers") end
        num = num + data[i] * weights[i]
        den = den + weights[i]
    end
    return den > 0 and num / den or nil
end

--- Robust Statistics
--
-- @section robust_statistics

--- Computes the median absolute deviation (MAD)
-- Much more reliable measure of variability when outliers are present, from the deviation
--
-- @tparam number[] data
-- @treturn number|nil MAD or nil if empty
function stats.mad_median(data)
    if #data == 0 then return nil end
    local med = stats.median(data)
    local devs = {}
    for _, v in pairs(data) do
        if type(v) ~= "number" then expect(1, data, "sequential table of numbers") end
        table.insert(devs, math.abs(v - med))
    end
    return stats.median(devs)
end

--- Inequality Measures
--
-- @section inequality_measures

--- Computes the gini coefficient for inequality (0..1)
-- Returns 0 for datasets with fewer than 2 or zero mean
-- Represents how unequally distributed a dataset it, best explained by income but works for other sets too
--
-- @tparam number[] data
-- @treturn number gini coefficient (0..1)
function stats.gini(data)
    expect(1, data, "table")
    local n = #data
    if n < 2 then return 0 end
    local t = { table.unpack(data) }
    table.sort(t)
    local mean_val = stats.mean(t)
    if not mean_val or mean_val == 0 then return 0 end

    local cumsum = 0
    for i = 1, n do
        if type(t[i]) ~= "number" then expect(1, data, "sequential table of numbers") end
        cumsum = cumsum + t[i]
    end

    local num = 0
    for i = 1, n do
        num = num + (2 * i - n - 1) * t[i]
    end
    return num / (n * cumsum)
end

--- Bivariate Statistics
--
-- @section bivariate_statistics

--- Computes the sample covariance (uses n-1 denominator)
-- Represents how much the two datasets will vary with each other
--
-- @tparam number[] x
-- @tparam number[] y
-- @treturn number|nil covariance or nil if lengths mismatch or n<=1
function stats.covariance(x, y)
    expect(1, x, "table")
    expect(2, y, "table")
    local n = #x
    if n == 0 or #y ~= n then return nil end
    local mx, my = stats.mean(x), stats.mean(y)
    local cov = 0
    for i = 1, n do
        if type(x[i]) ~= "number" then expect(1, x, "sequential table of numbers") end
        if type(y[i]) ~= "number" then expect(2, y, "sequential table of numbers") end
        cov = cov + (x[i] - mx) * (y[i] - my)
    end
    if n <= 1 then return nil end
    return cov / (n - 1)
end

--- Computes the pearson correlation coefficient
-- Returns 0 if undefined
-- Represents the strength and relationship of the covariances, much more interpretable
--
-- @tparam number[] x
-- @tparam number[] y
-- @treturn number correlation coefficient in [-1,1] or 0 if undefined
function stats.correlation(x, y)
    local cov = stats.covariance(x, y)
    local sx, sy = stats.stdev(x, true), stats.stdev(y, true)
    if not cov or not sx or not sy or sx == 0 or sy == 0 then
        return 0
    end
    return cov / (sx * sy)
end

--- Quantiles and Outliers
--
-- @section quantiles_and_qutliers

--- Computes the percentile interpolation (linear interpolation between order statistics)
-- Finds the percentile of a value p given a dataset, sorts and places in between indices(or on one)
-- p in [0,1]
--
-- @tparam number[] data
-- @tparam number p percentile proportion (0..1)
-- @treturn number|nil percentile value or nil if data empty
function stats.percentile(data, p)
    expect(2, p, "number")
    if p < 0 then expect(2, p, "number greater than or equal to zero") end
    if p > 1 then expect(2, p, "number less than or equal to one") end
    if p == 0 then return stats.min(data) end
    if p == 1 then return stats.max(data) end
    local t = { table.unpack(data) }
    table.sort(t)
    local n = #t
    if n == 0 then return nil end
    local rank = p * (n - 1) + 1
    local f, c = math.floor(rank), math.ceil(rank)
    if type(t[f]) ~= "number" then expect(1, data, "sequential table of numbers") end
    if f == c then
        return t[f]
    else
        if type(t[c]) ~= "number" then expect(1, data, "sequential table of numbers") end
        return t[f] + (rank - f) * (t[c] - t[f])
    end
end

--- Computes the first and third quartiles as table (`{ Q1 = ..., Q3 = ... }`)
-- Quartiles are found by cutting the dataset in half using the median, and finding the median of those sets
--
-- @tparam number[] data
-- @treturn table quartiles
function stats.quartiles(data)
    return {
        Q1 = stats.percentile(data, 0.25),
        Q3 = stats.percentile(data, 0.75)
    }
end

--- Computes the interquartile range Q3 - Q1
-- The distance between first and third quartiles
--
-- @tparam number[] data
-- @treturn number|nil IQR or nil if undefined
function stats.iqr(data)
    local q = stats.quartiles(data)
    if not q.Q1 or not q.Q3 then return nil end
    return q.Q3 - q.Q1
end

--- Computes a list of all outliers in the dataset
-- Outliers are considered as such if they are more than 150% of the IQR away from either the first or third quartiles
--
-- @tparam number[] data
-- @treturn number[] list of outlier values (empty if none)
function stats.outliers(data)
    local q = stats.quartiles(data)
    local iqr = stats.iqr(data)
    if not iqr then return {} end
    local low, high = q.Q1 - 1.5 * iqr, q.Q3 + 1.5 * iqr
    local outs = {}
    for _, v in pairs(data) do
        if v < low or v > high then
            table.insert(outs, v)
        end
    end
    return outs
end

--- Utility Functions
--
-- @section utility_functions

--- Find index of value in an array-like table (linear search)
--
-- @tparam table tbl
-- @param value
-- @treturn number|nil index or nil if not found
function stats.find(tbl, value)
    expect(1, tbl, "table")
    for i=1, #tbl do
        if tbl[i] == value then
            return i
        end
    end
    return nil
end

--- Generates n quantitiative data points between a and b
--
-- @tparam number n number of points
-- @tparam[opt=0] number a lower bound
-- @tparam[opt=1] number b upper bound
-- @treturn number[] array of random values
function stats.testdata(n, a, b)
    expect(1, n, "number")
    expect(2, a, "number")
    expect(3, b, "number")
    if n < 1 then expect(1, n, "number greater than or equal to one")
    local result = {}
    a = a or 0
    b = b or 1
    for i = 1, n do
        result[i] = a + math.random() * (b - a)
    end
    return result
end

--- Linear Regression
--
-- @section linear_regression

--- Computes a simple least-squares linear regression (y ~ a + b x)
-- Minimizes squares to find the line that is the least distance squared from all data points
--
-- @tparam number[] x independent variable values
-- @tparam number[] y dependent variable values
-- @treturn table|nil `{ slope = number, intercept = number }` or nil if undefined
function stats.linReg(x, y)
    expect(1, x, "table")
    expect(2, y, "table")
    local n = #x
    if n == 0 or #y ~= n then return nil end

    local mx, my = stats.mean(x), stats.mean(y)
    local num, den = 0, 0
    for i = 1, n do
        if type(x[i]) ~= "number" then expect(1, x, "sequential table of numbers") end
        if type(y[i]) ~= "number" then expect(2, y, "sequential table of numbers") end
        local dx = x[i] - mx
        num = num + dx * (y[i] - my)
        den = den + dx * dx
    end
    if den == 0 then return nil end

    local slope = num / den
    local intercept = my - slope * mx
    return { slope = slope, intercept = intercept }
end

--- Predicts the y value for a given linear regression model at a given position
--
-- @tparam table model `{ slope, intercept }`
-- @tparam number xval value of x
-- @treturn number|nil predicted y or nil if model missing
function stats.linRegPred(model, xval)
    expect(1, model, "table")
    expect(2, xval, "number")
    expect(1, model.slope, "number")
    expect(1, model.intercept, "number")
    if not model then return nil end
    return model.slope * xval + model.intercept
end

--- Computes the coefficient of determination R^2 for linear model
-- Represents how much variation in variable y can be attributed to variable x
--
-- @tparam number[] x
-- @tparam number[] y
-- @tparam table model linear model returned by linReg
-- @treturn number|nil R^2 (1 if total variance is zero)
function stats.r2(x, y, model)
    expect(1, x, "table")
    expect(2, y, "table")
    expect(3, model, "table")
    expect(3, model.slope, "number")
    expect(3, model.intercept, "number")
    local n = #y
    if n == 0 or not model then return nil end

    local ymean = stats.mean(y)
    local ssres, sstot = 0, 0
    for i = 1, n do
        if type(x[i]) ~= "number" then expect(1, x, "sequential table of numbers") end
        if type(y[i]) ~= "number" then expect(2, y, "sequential table of numbers") end
        local ypred = stats.linRegPred(model, x[i])
        ssres = ssres + (y[i] - ypred) ^ 2
        sstot = sstot + (y[i] - ymean) ^ 2
    end
    if sstot == 0 then
        return 1
    else
        return 1 - ssres / sstot
    end
end

--- Hyposthesis Testing - Student's t Distribution
--
-- @section hyposthesis_testing_students_t_distribution

--- Calculate cumulative distribution function for Student's t distribution
-- Uses numerical approximation for the incomplete beta function using relationship to incomplete beta function and continued fraction approximation
--
-- @tparam number t t-statistic value
-- @tparam number df degrees of freedom
-- @treturn number probability `P(T <= t)`
function stats.tCDF(t, df)
    expect(1, t, "number")
    expect(2, dt, "number")
    if df < 0.5 then expect(2, dt, "number greater than or equal to 0.5") end
    
    if df > 1000 then
        local x = t / math.sqrt(df / (df - 2))
        return stats.normalCDF(x)
    end
    
    local x = df / (df + t * t)
    local a = df / 2
    local b = 0.5
    
    local beta_inc = stats.incopmleteBeta(x, a, b)
    
    if t >= 0 then
        return 1 - 0.5 * beta_inc
    else
        return 0.5 * beta_inc
    end
end

--- Calculate normal (Gaussian) cumulative distribution function
-- Helper function for tCDF when df is large using error function approximation
--
-- @tparam number x value
-- @treturn number probability `P(X <= x)` for standard normal
function stats.normalCDF(x)
    expect(1, x, "number")
    local function erf(x)
        local a1 =  0.254829592
        local a2 = -0.284496736
        local a3 =  1.421413741
        local a4 = -1.453152027
        local a5 =  1.061405429
        local p  =  0.3275911
        
        local sign = x < 0 and -1 or 1
        x = math.abs(x)
        
        local t = 1.0 / (1.0 + p * x)
        local y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * math.exp(-x * x)
        
        return sign * y
    end
    
    return 0.5 * (1 + erf(x / math.sqrt(2)))
end

--- Calculate incomplete beta function I_x(a,b)
-- Helper function for tCDF calculation using continued fraction approximation
--
-- @tparam number x upper limit of integration (0 <= x <= 1)
-- @tparam number a first shape parameter
-- @tparam number b second shape parameter
-- @treturn number incomplete beta function value
function stats.incopmleteBeta(x, a, b)
    expect(1, x, "number")
    expect(2, a, "number")
    expect(3, b, "number")
    if x < 0 or x > 1 then expect(1, x, "number between zero and one inclusive") end
    
    local function betaCF(x, a, b)
        local max_iter = 200
        local epsilon = 1e-10
        
        local qab = a + b
        local qap = a + 1
        local qam = a - 1
        local c = 1
        local d = 1 - qab * x / qap
        
        if math.abs(d) < 1e-30 then d = 1e-30 end
        d = 1 / d
        local h = d
        
        for m = 1, max_iter do
            local m2 = 2 * m
            local aa = m * (b - m) * x / ((qam + m2) * (a + m2))
            d = 1 + aa * d
            if math.abs(d) < 1e-30 then d = 1e-30 end
            c = 1 + aa / c
            if math.abs(c) < 1e-30 then c = 1e-30 end
            d = 1 / d
            h = h * d * c
            
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2))
            d = 1 + aa * d
            if math.abs(d) < 1e-30 then d = 1e-30 end
            c = 1 + aa / c
            if math.abs(c) < 1e-30 then c = 1e-30 end
            d = 1 / d
            local del = d * c
            h = h * del
            
            if math.abs(del - 1) < epsilon then
                return h
            end
        end
        
        return h
    end
    
    local function logBeta(a, b)
        return stats.logGamma(a) + stats.logGamma(b) - stats.logGamma(a + b)
    end
    
    local bt = math.exp(a * math.log(x) + b * math.log(1 - x) - logBeta(a, b))
    
    if x < (a + 1) / (a + b + 2) then
        return bt * betaCF(x, a, b) / a
    else
        return 1 - bt * betaCF(1 - x, b, a) / b
    end
end

--- Calculate logarithm of gamma function
-- Helper function for incopmleteBeta using Lanczos approximation coefficients
--
-- @tparam number x input value
-- @treturn number `log(Gamma(x))`
function stats.logGamma(x)
    expect(1, x, "number")
    local coef = {
        76.18009172947146,
        -86.50532032941677,
        24.01409824083091,
        -1.231739572450155,
        0.1208650973866179e-2,
        -0.5395239384953e-5
    }
    
    local y = x
    local tmp = x + 5.5
    tmp = tmp - (x + 0.5) * math.log(tmp)
    local ser = 1.000000000190015
    
    for i = 1, 6 do
        y = y + 1
        ser = ser + coef[i] / y
    end
    
    return -tmp + math.log(2.5066282746310005 * ser / x)
end

--- Hypothesis Testing - t-tests
--
-- @section hypothesis_testing_t_tests

--- Perform one-sample t-test
-- Tests whether sample mean significantly differs from a hypothesized population mean
-- Returns p-value: if p < 0.05, difference is statistically significant
--
-- @tparam number[] data sample values
-- @tparam number mu0 hypothesized population mean
-- @treturn table results table with t (test statistic), df (degrees of freedom), and p (p-value)
function stats.oneSampleTTest(data, mu0)
    expect(1, data, "table")
    expect(2, mu0, "number")
    local n = #data
    if n < 2 then return {t = 0, df = 0, p = 1} end
    local m = stats.mean(data)
    local se = stats.sex(data)
    if not se or se == 0 then return {t = 0, df = 0, p = 1} end
    local t = (m - mu0) / se
    local df = n - 1
    local p = 2 * (1 - stats.tCDF(math.abs(t), df))
    return {t = t, df = df, p = p}
end

--- Perform two-sample t-test
-- Tests whether means of two independent samples significantly differ
-- Returns p-value: if p < 0.05, difference is statistically significant
--
-- @tparam number[] x first sample
-- @tparam number[] y second sample
-- @tparam boolean[opt=true] equal_var assume equal variances (default true)
-- @treturn table results table with t (test statistic), df (degrees of freedom), and p (p-value)
function stats.twoSampleTTest(x, y, equal_var)
    expect(1, x, "table")
    expect(2, y, "table")
    if equal_var ~= nil then expect(3, equal_var, "boolean") end
    local nx, ny = #x, #y
    if nx < 2 or ny < 2 then return {t = 0, df = 0, p = 1} end
    local mx, my = stats.mean(x), stats.mean(y)
    local vx, vy = stats.variance(x, true), stats.variance(y, true)
    equal_var = equal_var ~= false
    
    local se, df
    if equal_var then
        local pv = ((nx - 1) * vx + (ny - 1) * vy) / (nx + ny - 2)
        se = math.sqrt(pv * (1 / nx + 1 / ny))
        df = nx + ny - 2
    else
        se = math.sqrt(vx / nx + vy / ny)
        local num = (vx / nx + vy / ny)^2
        local den = (vx^2 / (nx^2 * (nx - 1))) + (vy^2 / (ny^2 * (ny - 1)))
        df = num / den
    end
    
    if se == 0 then return {t = 0, df = 0, p = 1} end
    local t = (mx - my) / se
    local p = 2 * (1 - stats.tCDF(math.abs(t), df))
    return {t = t, df = df, p = p}
end

--- Perform paired t-test
-- Tests whether the mean difference between paired observations is significant
-- Useful for before/after comparisons on the same subjects
--
-- @tparam number[] before values before treatment
-- @tparam number[] after values after treatment
-- @treturn table results table with t (test statistic), df (degrees of freedom), and p (p-value)
function stats.pairTTest(before, after)
    expect(1, before, "table")
    expect(2, after, "table")
    local n = #before
    if n < 2 or #after ~= n then return {t = 0, df = 0, p = 1} end
    local diffs = {}
    for i = 1, n do
        if type(before[i]) ~= "number" then expect(1, before, "sequential table of numbers") end
        if type(after[i]) ~= "number" then expect(2, after, "sequential table of numbers") end
        diffs[i] = before[i] - after[i]
    end
    return stats.oneSampleTTest(diffs, 0)
end

--- Perform t-test on linear regression slope
-- Checks a dataset against it's linear regression
--
-- @tparam number[] x independent variable values
-- @tparam number[] y dependent variable values
-- @treturn table results table with t (test statistic), df (degrees of freedom), and p (p-value)
function stats.linRegTTest(x, y)
    expect(1, x, "table")
    expect(2, y, "table")
    local model = stats.linReg(x, y)
    if not model then return {t = 0, df = 0, p = 1} end
    
    local n = #x
    local slope_se = stats.stdev(y, true) / math.sqrt(stats.sum(function()
        local mx = stats.mean(x)
        local sxx = 0
        for i = 1, n do
        if type(x[i]) ~= "number" then expect(1, x, "sequential table of numbers") end
            sxx = sxx + (x[i] - mx)^2
        end
        return sxx
    end))
    
    local t = model.slope / slope_se
    local df = n - 2
    local p = 2 * (1 - stats.tCDF(math.abs(t), df))
    
    return {
        t = t,
        df = df,
        p = p,
        slope = model.slope,
        slope_se = slope_se,
        r_squared = stats.r2(x, y, model)
    }
end

return stats
