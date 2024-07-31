package tatsugo

/**
 * Fleet reference.
 */
interface FleetRef {

	val name: String

	/**
	 * Sends a message to [Particle].
	 */
	suspend fun <M> send(particleAddress: ParticleAddress, msg: M)

}