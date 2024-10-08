package tatsugo

@JvmInline
value class FleetRef(val id: String)

interface Fleet {
	/**
	 * Returns Fleet reference.
	 */
	fun ref(): FleetRef

	/**
	 * Binds particle to this fleet.
	 */
	fun bind(particleLifecycle: ParticleLifecycle)

	/**
	 * Creates a [Queue] out of the Fleet.
	 */
	fun asQueue(): Queue
}
