# Changes
### Ship API
- Made `getOmega`, `getScale`, `getShipyardPosition`, `getVelocity`, `getWorldspacePosition`, and `transformPositionToWorld` output proper `vector`s with correct metatable
- Made `getQuaternion` output a proper `quaternion` with correct metatable
- Made `getTransformationMatrix` output a proper `matrix` with correct metatable
- Made `getConstraints` output use proper `vector`s and `quaternion`s where applicable
- Re-added `getEulerAnglesZYX"`, `getEulerAnglesZXY`, `getEulerAnglesYXZ`, `getEulerAnglesXYZ`, `getRoll`, `getYaw`, `getPitch`, and `getRotationMatrix` ***only*** so they throw a more useful error redirecting to the new methods.
- Updated Ship API help page
- Iproved `physics_ticks` event output
- Moved `physics_ticks` event to Ship API
- Added `pullPhysicsTicks` to utilize `physics_ticks` with proper `vector` and `quaternion` support
### Quaternion API
- Moved to [CC: Advanced Math Library](https://github.com/TechTastic/Advanced-Math)
### Extended Ship API
- Moved `physics_ticks` event to Ship API
### New APIs
- [CC: Advanced Math Library](https://github.com/TechTastic/Advanced-Math) created and included to handle pure Lua math including `quaternion` and `matrix` APIs, as well as a new `pid` module