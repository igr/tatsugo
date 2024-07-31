package tatsugo.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import tatsugo.Bus
import tatsugo.Event
import tatsugo.Queue

class EventBusOverFlow(private val scope: CoroutineScope) : Bus {

	// MutableSharedFlow is a hot flow that allows emitting elements from a single producer
	// and broadcasting them to multiple consumers.
	private val events = MutableSharedFlow<Event>(extraBufferCapacity = Int.MAX_VALUE)

	/**
	 * Subscribe creates a new coroutine that listens for events and processes them.
	 */
	override fun subscribe(handler: (Event) -> Array<Event>) {
		scope.launch(start = CoroutineStart.UNDISPATCHED) {
			events.collect {
				val events = handler(it)
				events.forEach(::emit)
			}
		}
	}

	override fun emit(event: Event) {
		val emitResult = events.tryEmit(event)
		if (!emitResult) {
			throw IllegalStateException("Failed to emit event: $event")
		}
	}

	// queue

	override fun bind(queueHandler: Queue) = bindQueueWithChannel(this, queueHandler)

}