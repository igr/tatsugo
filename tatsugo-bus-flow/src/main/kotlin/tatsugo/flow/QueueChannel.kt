package tatsugo.flow

import tatsugo.Bus
import tatsugo.Events
import tatsugo.Queue

internal fun bindQueueWithChannel(bus: Bus, queueHandler: Queue) {
	bus.subscribe { event ->
		if (queueHandler.isApplicable(event)) {
			queueHandler.process(event)
		} else {
			Events.none
		}
	}
}