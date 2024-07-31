package tatsugo.fleet

import tatsugo.Bus
import tatsugo.Fleet
import tatsugo.FleetRef

/**
 * Spawns a new Fleet.
 */
fun spawnSimpleFleet(name: String, bus: Bus): Fleet {
	val fleet = FleetImpl(FleetRef(name), bus)
	fleet.runFleet()
	return fleet
}
