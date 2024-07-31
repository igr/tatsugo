package tatsugo

import kotlinx.coroutines.channels.Channel
import java.util.concurrent.ConcurrentHashMap

/**
 * Fleet reference implementation.
 */
internal class FleetRefImpl(
	override val name: String,
	private val mailbox: Channel<FleetMessage>
) : FleetRef {

	/**
	 * Sends a message to [Particle].
	 */
	override suspend fun <M> send(particleAddress: ParticleAddress, msg: M) {
		mailbox.send(ParticleMessage(particleAddress, msg))
	}

}

class FleetImpl(
	private val name: String,
	private val newParticle: ParticleSupplier,
) : Fleet {

	private val receiveChannel = Channel<FleetMessage>(capacity = Channel.UNLIMITED)
	private val ref = FleetRefImpl(name, receiveChannel)
	private val fleet = ConcurrentHashMap<ParticleAddress, Particle<*, *>>()

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

		fleet[addr] = newParticle
	}

	/**
	 * Returns the Fleet reference.
	 */
	override fun ref(): FleetRef = ref
}
