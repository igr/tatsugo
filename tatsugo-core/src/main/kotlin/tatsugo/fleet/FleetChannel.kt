package tatsugo.fleet

import kotlinx.coroutines.channels.Channel
import tatsugo.*
import java.util.concurrent.ConcurrentHashMap

class FleetChannel(
	private val name: String,
	private val newParticle: ParticleSupplier,
) : Fleet {

	private val receiveChannel = Channel<FleetMessage>(capacity = Channel.UNLIMITED)
	private val fleet = ConcurrentHashMap<ParticleAddress, Particle<*, *>>()

	private val ref = object : FleetRef {
		override val name: String = this@FleetChannel.name
		override suspend fun <M> send(particleAddress: ParticleAddress, msg: M) {
			receiveChannel.send(ParticleMessage(particleAddress, msg))
		}
	}

	/**
	 * Runs the whole Fleet.
	 * This is the main message processing dispatcher.
	 */
	override suspend fun run() {
		while (true) {
			val msg = receiveChannel.receive()
			when (msg) {
				is ParticleMessage<*> -> runParticle(msg.message, msg.address)
			}
		}
	}

	/**
	 * Runs a Particle.
	 * Locates the particle by its address.
	 * If the particle does not exist, it creates a new one.
	 * Then it runs the particle with the message.
	 */
	private suspend fun <M> runParticle(msg: M, addr: ParticleAddress) {
		val particle = fleet.computeIfAbsent(addr) { newParticle(ref, addr) } as Particle<*, M>

		val newParticle = particle.behavior(msg)

		if (newParticle != particle) {
			// update the particle if changed
			fleet[addr] = newParticle
		}
	}

	/**
	 * Returns the Fleet reference.
	 */
	override fun ref(): FleetRef = ref
}
