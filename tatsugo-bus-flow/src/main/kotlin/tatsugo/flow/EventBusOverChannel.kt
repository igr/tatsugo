package tatsugo.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import tatsugo.Bus
import tatsugo.Event
import tatsugo.Queue

class EventBusOverChannel(private val scope: CoroutineScope) : Bus {

	private val events = Channel<Event>(Channel.UNLIMITED)

	override fun subscribe(handler: (Event) -> Array<Event>) {
		eventHandlers.add(handler)
	}

	private val eventHandlers = mutableListOf<(Event) -> Array<Event>>()

	fun run() {
		scope.launch(start = CoroutineStart.UNDISPATCHED) {
			while (true) {
				val event = events.receive()
				offerEventToHandlers(event)
			}
		}
	}

	private fun offerEventToHandlers(event: Event) {
		eventHandlers
			.forEach { handler ->
				val events = handler(event)
				events.forEach(::emit)
			}
	}

	override fun emit(event: Event) = events.trySend(event).getOrThrow()

	// queue

	override fun bind(queueHandler: Queue) = bindQueueWithChannel(this, queueHandler)

}