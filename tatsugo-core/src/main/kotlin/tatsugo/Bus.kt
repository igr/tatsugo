package tatsugo

interface Bus {

	/**
	 * Subscribe to events.
	 * Event handlers may be executed in any order, depending
	 * on the Bus implementation.
	 */
	fun subscribe(handler: (Event) -> Array<Event>)

	/**
	 * Emit an event to a bus.
	 */
	fun emit(event: Event)

	/**
	 * Binds a queue to a bus.
	 * Events that enter the queue are processed in serial order.
	 */
	fun bind(queueHandler: Queue)
}