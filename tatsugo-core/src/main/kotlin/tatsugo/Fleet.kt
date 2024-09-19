package tatsugo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import tatsugo.fleet.FleetChannel

interface Fleet {
	/**
	 * Runs the Fleet.
	 */
	suspend fun run()
	/**
	 * Reference to the Fleet.
	 */
	fun ref(): FleetRef
}

typealias ParticleSupplier = (FleetRef, ParticleAddress) -> Particle<*, *>


/**
 * Spawns a new Fleet.
 */
fun spawnFleet(
	name: String,
	scope: CoroutineScope,
	newParticle: ParticleSupplier
): FleetRef {
	val fleet = FleetChannel(name, newParticle)
	scope.launch { fleet.run() }
	return fleet.ref()
}
