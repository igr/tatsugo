package tatsugo.flow

import kotlinx.coroutines.CoroutineScope
import tatsugo.Bus

fun CoroutineScope.startFlowBus(): Bus {
	return EventBusOverFlow(this)
}

fun CoroutineScope.startChannelBus(): Bus {
	val bus = EventBusOverChannel(this)
	bus.run()
	return bus
}
