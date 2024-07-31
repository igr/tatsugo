package tatsugo

/**
 * Base class for all exceptions in Tatsugo.
 */
open class TatsugoException(message: String) : RuntimeException(message)

/**
 * Thrown when a particle with a given address is not found in a Fleet.
 */
class UnknownParticleException(fleetRef: FleetRef, address: ParticleAddress) :
	TatsugoException("Unknown particle $address in fleet $fleetRef")