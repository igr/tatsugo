package example

import example.Counter.Message
import traktor.Koogii
import traktor.Mutable
import traktor.TraktorAddress

// respawn a new counter
fun newCounter(address: TraktorAddress): Counter {
	val value = database[address] ?: 0
	return Counter(address, value)
}

// update the counter value
fun newCounter(address: TraktorAddress, value: Int): Counter {
	database[address] = value
	return Counter(address, value)
}

class Counter(
	override val address: TraktorAddress,
	override val state: Int,
	override val behavior: suspend (Counter, Message) -> Counter = Counter::counting,
) : Koogii<Counter, Message, Int> {

	// messages
	sealed interface Message
	data class Inc(val by: Int) : Message, Mutable
	data object Reset : Message, Mutable

	// state machine

	private suspend fun counting(msg: Message): Counter {
		return when (msg) {
			is Inc -> {
				println("Counter $address: $state + ${msg.by}")
				newCounter(address, state + msg.by)
			}
			is Reset -> {
				println("Counter $address: reset")
				newCounter(address, 0)
			}
		}
	}

}
