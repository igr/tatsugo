package tatsugo

open class TatsugoException(message: String) : RuntimeException(message)

class UnknownParticleException(fleetRef: FleetRef, address: ParticleAddress) :
	TatsugoException("Unknown particle $address in fleet $fleetRef")