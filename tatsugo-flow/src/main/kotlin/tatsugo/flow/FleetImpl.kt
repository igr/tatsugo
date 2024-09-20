package tatsugo.flow

import tatsugo.*
import java.util.concurrent.ConcurrentHashMap

class FleetImpl(
	private val fleetRef: FleetRef,
	private val eventBus: Bus,
) : Fleet {

	override fun ref(): FleetRef = fleetRef

	private val fleet = ConcurrentHashMap<ParticleAddress, Particle>()

	/**
	 * Runs the whole Fleet.
	 * This is the main message processing dispatcher.
	 */
	override fun run() {
		eventBus.subscribe { event ->
			when (event) {
				is FleetMessage -> if (event.fleetName == fleetRef) {
					runParticle(event.message, event.address)
				}
			}
			Bus.none
		}
	}

	/**
	 * Runs a Particle.
	 * Locates the particle by its address.
	 * If the particle does not exist, it creates a new one.
	 * Then it runs the particle with the message.
	 */
	private suspend fun runParticle(msg: Message, addr: ParticleAddress) {
		val particle = fleet.computeIfAbsent(addr) { newParticle(addr) }

		val next = particle.behavior(msg)

		val newParticle = next.particle

		if (newParticle != particle) {
			// update the particle if changed
			fleet[addr] = newParticle
		}

		// emit events
		if (next.events.isNotEmpty()) {
			next.events.forEach { eventBus.emit(it) }
		}
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

}
