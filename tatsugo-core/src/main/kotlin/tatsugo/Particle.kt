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
	fun behavior(msg: Message): NextParticle
}

class NextParticle(
	val particle: Particle,
	val events: Array<Event> = Bus.none
) {
	/**
	 * Add events to the Next particle progression.
	 */
	fun emit(vararg events: Event): NextParticle =
		NextParticle(particle, this.events + arrayOf(*events))

	companion object {
		/**
		 * Advance to next particle.
		 */
		fun advance(particle: Particle): NextParticle {
			return NextParticle(particle)
		}
	}
}
