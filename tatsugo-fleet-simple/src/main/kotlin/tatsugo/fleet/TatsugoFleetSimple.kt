package tatsugo.fleet

import tatsugo.Fleet
import tatsugo.FleetRef

/**
 * Spawns a new Fleet.
 */
fun spawnSimpleFleet(name: String): Fleet {
	return FleetImpl(FleetRef(name))
}
