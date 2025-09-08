# Changes
- Removed the `getEulerAngles***` methods in favor of a `quaternion` Lua library in-game
- Added `quaternion` Lua library as an in-game API
- Changed the `physics_tick` event to `physics_ticks` and updated how it works
  - `physics_ticks` now saves up previous ticks of data THEN, on game tick, send out the event with the queued physics data
- Updated config documentation
- Added `ship` help page in-game
- Added new methods to both Ship and Extended Ship API that take vectors