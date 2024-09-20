package tatsugo.flow

import kotlinx.coroutines.CoroutineScope
import tatsugo.Bus
import tatsugo.Fleet
import tatsugo.FleetRef

fun CoroutineScope.startFlowBus(): Bus {
	val bus = EventBus(this)
	bus.run()
	return bus
}

/**
 * Spawns a new Fleet.
 */
fun spawnFlowFleet(name: String, bus: Bus): Fleet {
	val fleet = FleetImpl(FleetRef(name), bus)
	fleet.run()
	return fleet
}
