package tatsugo.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import tatsugo.Bus
import tatsugo.Event

class EventBus(private val scope: CoroutineScope) : Bus {

	// MutableSharedFlow is a hot flow that allows emitting elements from a single producer
	// and broadcasting them to multiple consumers.
	private val _events = MutableSharedFlow<Event>(extraBufferCapacity = Int.MAX_VALUE)

	// read-only shared flow
	private val events = _events.asSharedFlow()

	private val jobs = mutableListOf<Job>()

	override fun subscribe(handler: suspend (Event) -> Array<Event>) {
		eventHandlers.add(handler)
	}

	private val eventHandlers = mutableListOf<suspend (Event) -> Array<Event>>()

	// there could be a better and faster way to process events,
	// i.e. using a lookup.
	private suspend fun handleEvent(event: Event) {
		eventHandlers.forEach {
			val events = it(event)
			events.forEach { emit(it) }
		}
	}

	// underlying subscription may be expensive!
	// instead, we will collect only once but add handlers to the flow processor above.
	fun run() {
		scope.launch {
			events.collect {
				handleEvent(it)
			}
		}.also {
			jobs.add(it)
		}
	}

	override suspend fun emit(event: Event) {
		_events.emit(event) // suspends until all subscribers receive it
	}
}