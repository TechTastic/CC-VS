using CCSharp.Attributes;
using CCSharp.ComputerCraft;
using CCSharp.AdvancedMath;

namespace CCSharp.CCVS;

public class Ship
{
    public Long Id { 
        [LuaMethod("ship.getId")] get;
    }

    public string Slug {
        [LuaMethod("ship.getSlug")] get;
        [LuaMethod("ship.setSlug")] set;
    }

    public double Mass {
        [LuaMethod("ship.getMass")] get;
    }

    public bool Static {
        [LuaMethod("ship.isStatic")] get;
        [LuaMethod("ship.setStatic")] set;
    }

    public Dictionary<Long, Constraint> Constraints {
        [LuaMethod("ship.getConstraints")] get;
    }

    public Vector3 ShipyardPosition {
        [LuaMethod("ship.getShipyardPosition")] get;
    }

    public Vector3 WorldspacePosition {
        [LuaMethod("ship.getWorldspacePosition")] get;
    }

    public Vector3 LinearVelocity {
        [LuaMethod("ship.getVelocity")] get;
    }

    public Vector3 AngularVelocity {
        [LuaMethod("ship.getOmega")] get;
    }

    public Vector3 Scale {
        [LuaMethod("ship.getScale")] get;
    }

    public Quaternion Quaternion {
        [LuaMethod("ship.getQuaternion")] get;
    }

    public Matrix TransformationMatrix {
        [LuaMethod("ship.getTransformationMatrix")] get;
    }

    public Matrix MomentOfInertiaTensorToSave {
        [LuaMethod("ship.getMomentOfInertiaTensorToSave")] get;
    }

    public Matrix MomentOfInertiaTensor {
        [LuaMethod("ship.getMomentOfInertiaTensor")] get;
    }

    [LuaMethod("ship.transformPositionToWorld")]
    public static Vector3 transformPositionToWorld(Vector3 pos) => default;

    [LuaMethod("ship.pullPhysicsTicks")]
    public static (string name, PhysicsTick[] ticks) PullPhysicsTicks() => default;

    [LuaTableTypeCheck(TableAccessor = "getBuoyantFactor"), LuaTableTypeCheck(TableAccessor = "isStatic"), LuaTableTypeCheck(TableAccessor = "doFluidDrag"), LuaTableTypeCheck(TableAccessor = "getInertia"), LuaTableTypeCheck(TableAccessor = "getPoseVel"), LuaTableTypeCheck(TableAccessor = "getForceInducers")]
    class PhysicsTick
    {
        public double BuoyantFactor {
            [LuaMethod("getBuoyantFactor")] get;
        }

        public bool Static {
            [LuaMethod("isStatic")] get;
        }

        public bool FluidDrag {
            [LuaMethod("doFluidDrag")] get;
        }

        public Inertia Inertia {
            [LuaMethod("getInertia")] get;
        }

        public PoseVel PoseVel {
            [LuaMethod("getPoseVel")] get;
        }

        public string[] ForceInducers {
            [LuaMethod("getForceInducers")] get;
        }

        [LuaTableTypeCheck(TableAccessor = "momentOfInertiaTensor"), LuaTableTypeCheck(TableAccessor = "mass")]
        class Inertia
        {
            [LuaProperty("momentOfInertiaTensor")] public Matrix MomentOfInertiaTensor { get; set; }
            [LuaProperty("mass")] public double Mass { get; set; }
        }

        [LuaTableTypeCheck(TableAccessor = "vel"), LuaTableTypeCheck(TableAccessor = "omega"), LuaTableTypeCheck(TableAccessor = "pos"), LuaTableTypeCheck(TableAccessor = "rot")]
        class PoseVel
        {
            [LuaProperty("vel")] public Vector3 LinearVelocity { get; set; }
            [LuaProperty("omega")] public Vector3 AngularVelocity { get; set; }
            [LuaProperty("pos")] public Vector3 CenterOfMass { get; set; }
            [LuaProperty("rot")] public Quaternion Rotation { get; set; }
        }

    }

    [LuaMethod("ship.setScale")]
    public static void SetScale(double scale) => default;

    [LuaMethod("ship.teleport")]
    public static void Teleport(object data) => default;

    [LuaMethod("ship.applyInvariantForce")]
    public static void ApplyInvariantForce(Vector3 force) => default;
    [LuaMethod("ship.applyInvariantForce")]
    public static void ApplyInvariantForce(double forceX, double forceY, double forceZ) => default;

    [LuaMethod("ship.applyInvariantTorque")]
    public static void ApplyInvariantTorque(Vector3 torque) => default;
    [LuaMethod("ship.applyInvariantTorque")]
    public static void ApplyInvariantTorque(double torqueX, double torqueY, double torqueZ) => default;

    [LuaMethod("ship.applyInvariantForceToPos")]
    public static void ApplyInvariantForceToPosition(Vector3 force, Vector3 pos) => default;
    [LuaMethod("ship.applyInvariantForceToPos")]
    public static void ApplyInvariantForceToPosition(double forceX, double forceY, double forceZ, double posX, double posY, double posZ) => default;

    [LuaMethod("ship.applyRotDependentForce")]
    public static void ApplyRotDependentForce(Vector3 force) => default;
    [LuaMethod("ship.applyRotDependentForce")]
    public static void ApplyRotDependentForce(double forceX, double forceY, double forceZ) => default;

    [LuaMethod("ship.applyRotDependentTorque")]
    public static void ApplyRotDependentTorque(Vector3 torque) => default;
    [LuaMethod("ship.applyRotDependentTorque")]
    public static void ApplyRotDependentTorque(double torqueX, double torqueY, double torqueZ) => default;

    [LuaMethod("ship.applyRotDependentForceToPos")]
    public static void ApplyRotDependentForceToPosition(Vector3 force, Vector3 pos) => default;
    [LuaMethod("ship.applyRotDependentForceToPos")]
    public static void ApplyRotDependentForceToPosition(double forceX, double forceY, double forceZ, double posX, double posY, double posZ) => default;

    [LuaTableTypeCheck(TableAccessor = "shipId0"), LuaTableTypeCheck(TableAccessor = "shipId1"), LuaTableTypeCheck(TableAccessor = "type"), LuaTableTypeCheck(TableAccessor = "compliance")]
    class Constraint
    {
        [LuaEnum(typeof(ConstraintType))]
        public enum ConstraintType
        {
            [LuaEnumValue("attachment")] Attachment,
            [LuaEnumValue("fixed_attachment_orientation")] FixedAttachmentOrientation,
            [LuaEnumValue("fixed_orientation")] FixedOrientation,
            [LuaEnumValue("hinge_orientation")] HingeOrientation,
            [LuaEnumValue("hinge_swing_limits")] HingeSwingLimits,
            [LuaEnumValue("hinge_target_angle")] HingeTargetAngle,
            [LuaEnumValue("pos_damping")] PosDamping,
            [LuaEnumValue("rope")] Rope,
            [LuaEnumValue("rot_damping")] RotDamping,
            [LuaEnumValue("Slide")] Slide,
            [LuaEnumValue("spherical_swing_limits")] SphericalSwingLimits,
            [LuaEnumValue("spherical_twist_limits")] SphericalTwistLimits
        }

        [LuaProperty("shipId0")] public Long FirstShipID { get; set; }
        [LuaProperty("shipId1")] public Long SecondShipID { get; set; }
        [LuaProperty("type")] public ConstraintType Type { get; set; }
        [LuaProperty("compliance")] public double Compliance { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "localPos0"), LuaTableTypeCheck(TableAccessor = "localPos1"), LuaTableTypeCheck(TableAccessor = "maxForce")]
    interface IForce
    {
        [LuaProperty("localPos0")] public Vector3 FirstPosition { get; set; }
        [LuaProperty("localPos1")] public Vector3 SecondPosition { get; set; }
        [LuaProperty("maxForce")] public double MaxForce { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "localPos0"), LuaTableTypeCheck(TableAccessor = "localPos1"), LuaTableTypeCheck(TableAccessor = "maxForce")]
    interface ITorque
    {
        [LuaProperty("localRot0")] public Quaternion FirstPosition { get; set; }
        [LuaProperty("localRot1")] public Quaternion SecondPosition { get; set; }
        [LuaProperty("maxTorque")] public double MaxTorque { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "fixedDistance")]
    class AttachmentConstraint : Constraint, IForce
    {
        [LuaProperty("fixedDistance")] public double FixedDistance { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "minSwingAngle"), LuaTableTypeCheck(TableAccessor = "maxSwingAngle")]
    class HingeSwingLimitsConstraint : Constraint, ITorque
    {
        [LuaProperty("minSwingAngle")] public double MinSwingAngle { get; set; }
        [LuaProperty("maxSwingAngle")] public double MaxSwingAngle { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "targetAngle"), LuaTableTypeCheck(TableAccessor = "nextTickTargetAngle")]
    class HingeTargetAngleConstraint : Constraint, ITorque
    {
        [LuaProperty("targetAngle")] public double TargetAngle { get; set; }
        [LuaProperty("nextTickTargetAngle")] public double NextTickTargetAngle { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "posDamping")]
    class PosDampingConstraint : Constraint, IForce
    {
        [LuaProperty("posDamping")] public double PosDamping { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "ropeLength")]
    class RopeConstraint : Constraint, IForce
    {
        [LuaProperty("ropeLength")] public double RopeLength { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "rotDamping"), LuaTableTypeCheck(TableAccessor = "rotDampingAxes")]
    class RotDampingConstraint : Constraint, IForce
    {
        [LuaEnum(typeof(RotDampingAxes))]
        public enum RotDampingAxes
        {
            [LuaEnumValue("parallel")] Parallel,
            [LuaEnumValue("perpendicular")] Perpendicular,
            [LuaEnumValue("all_axes")] AllAxes
        }

        [LuaProperty("rotDamping")] public double RotDamping { get; set; }
        [LuaProperty("rotDampingAxes")] public RotDampingAxes RotDampingAxis { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "localSlideAxis0"), LuaTableTypeCheck(TableAccessor = "maxDistBetweenPoints")]
    class RotDampingConstraint : Constraint, IForce
    {
        [LuaProperty("localSlideAxis0")] public Vector3 LocalSlideAxis { get; set; }
        [LuaProperty("maxDistBetweenPoints")] public double MaxDistanceBetweenPoints { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "minSwingAngle"), LuaTableTypeCheck(TableAccessor = "maxSwingAngle")]
    class SphericalSwingLimitsConstraint : Constraint, ITorque
    {
        [LuaProperty("minSwingAngle")] public double MinSwingAngle { get; set; }
        [LuaProperty("maxSwingAngle")] public double MaxSwingAngle { get; set; }
    }

    [LuaTableTypeCheck(TableAccessor = "minTwistAngle"), LuaTableTypeCheck(TableAccessor = "maxTwistAngle")]
    class SphericalTwistLimitsConstraint : Constraint, ITorque
    {
        [LuaProperty("minTwistAngle")] public double MinTwistAngle { get; set; }
        [LuaProperty("maxTwistAngle")] public double MaxTwistAngle { get; set; }
    }
}