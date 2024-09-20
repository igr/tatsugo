package tatsugo

interface Event

interface Message

data class FleetMessage(
	val fleetName: FleetRef,
	val address: ParticleAddress,
	val message: Message
) : Event