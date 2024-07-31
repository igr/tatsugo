package tatsugo

/**
 * Particle address.
 */
@JvmInline
value class ParticleAddress(val value: String) {
	override fun toString(): String = "@$value"
}

/**
 * Particle is defined by its address, optional state, and behavior.
 * @param P Particle type, this instance.
 * @param M Particle Message type.
 */
interface Particle<P: Particle<P, M>, M> {
	val address: ParticleAddress
	suspend fun behavior(msg: M): P
}