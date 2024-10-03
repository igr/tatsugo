package tatsugo.fleet

import tatsugo.*

class FleetImpl(
	private val fleetRef: FleetRef
) : Fleet {

	override fun ref(): FleetRef = fleetRef

	private val fleet = mutableMapOf<ParticleAddress, Particle>()

	/**
	 * Runs a Particle.
	 * Locates the particle by its address.
	 * If the particle does not exist, it creates a new one.
	 * Then it runs the particle with the message.
	 */
	private fun <M> runParticle(msg: M, addr: ParticleAddress): Array<Event> {
		val particle = fleet.computeIfAbsent(addr) { newParticle(addr) }

		val next = particle.behavior(msg)

		val newParticle = next.particle

		if (newParticle != particle) {
			// update the particle if changed
			fleet[addr] = newParticle
		}

		return next.events
	}

	private val particles = mutableListOf<ParticleLifecycle>()

	override fun bind(particleLifecycle: ParticleLifecycle) {
		particles.add(particleLifecycle)
	}

	private fun newParticle(addr: ParticleAddress): Particle {
		particles.forEach { lifecycle ->
			val particle = lifecycle.onCreate(fleetRef)(addr)
			if (particle != null) {
				return particle
			}
		}
		throw UnknownParticleException(fleetRef, addr)
	}

	/**
	 * Creates a very simple queue that receives events
	 * and puts them into the queue.
	 */
	override fun asQueue(): Queue = object : Queue {
		override fun isApplicable(event: Event): Boolean {
			if (event is FleetMessage<*>) {
				if (event.fleet == fleetRef) {
					return true
				}
			}
			return false
		}
		override fun process(event: Event): Array<Event> {
			val message = event as FleetMessage<*>
			return runParticle(message.message, message.address)
		}
	}

}
