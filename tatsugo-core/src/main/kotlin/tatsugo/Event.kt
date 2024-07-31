package tatsugo

interface Event

/**
 * Particle events are messages that are sent to a specific particle.
 */
data class FleetMessage<M>(
	val fleet: FleetRef,
	val address: ParticleAddress,
	val message: M
) : Event

/**
 * Utility class to create an array of events.
 */
interface Events {
	companion object {
		/**
		 * No events to emit.
		 */
		val none: Array<Event> = arrayOf()
		/**
		 * Utility function to create an array of events.
		 */
		fun of(vararg events: Event) = arrayOf(*events)
	}
}