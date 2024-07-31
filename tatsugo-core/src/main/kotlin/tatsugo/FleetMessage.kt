package tatsugo

/**
 * Internal message for Fleet.
 */
internal sealed interface FleetMessage

/**
 * Internal message wrapper for [Particle] messages.
 */
internal data class ParticleMessage<M>(
	val address: ParticleAddress,
	val message: M,
) : FleetMessage
