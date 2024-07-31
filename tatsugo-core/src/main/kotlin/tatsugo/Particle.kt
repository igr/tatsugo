package tatsugo

/**
 * Particle address.
 */
@JvmInline
value class ParticleAddress(val value: String) {
	override fun toString(): String = "@$value"
}

/**
 * Particle lifecycle.
 */
interface ParticleLifecycle {
	/**
	 * Called when a fleet requires a particle instance
	 * for given address for the first time.
	 */
	fun onCreate(ref: FleetRef): (ParticleAddress) -> Particle?

	fun destroy(ref: FleetRef) {}
}

/**
 * Particle is defined by behavior.
 */
interface Particle {
	fun <M> behavior(msg: M): NextParticle

	companion object {
		/**
		 * Advances to next particle (state and behavior).
		 */
		fun shift(particle: Particle): NextParticle {
			return NextParticle(particle)
		}
	}
}

// todo need a better name
class NextParticle internal constructor(
	val particle: Particle,
	val events: Array<Event> = Events.none
) {
	/**
	 * Add events to the Next particle progression.
	 */
	fun emit(vararg events: Event): NextParticle =
		NextParticle(particle, Events.of(*events))

}
