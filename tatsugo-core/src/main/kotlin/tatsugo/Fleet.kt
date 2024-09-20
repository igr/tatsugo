package tatsugo

@JvmInline
value class FleetRef(val id: String)

interface Fleet {
	/**
	 * Runs the Fleet.
	 */
	fun run()
	/**
	 * Returns Fleet reference.
	 */
	fun ref(): FleetRef

	fun bind(particleLifecycle: ParticleLifecycle)
}
