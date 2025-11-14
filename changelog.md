# Changes
### Ship API
- Made the Ship API available on every computer but all methods error if not on a Ship
- Made `getOmega`, `getScale`, `getShipyardPosition`, `getVelocity`, `getWorldspacePosition`, and `transformPositionToWorld` output proper `vector`s with correct metatable
- Made `getQuaternion` output a proper `quaternion` with correct metatable
- Re-added `getEulerAnglesZYX"`, `getEulerAnglesZXY`, `getEulerAnglesYXZ`, `getEulerAnglesXYZ`, `getRoll`, `getYaw`, `getPitch`, and `getRotationMatrix` ***only*** so they throw a more useful error redirecting to the new methods.
- Updated help page
### Quaternion API
- Fixed mistake in `tostring` to account for negative values