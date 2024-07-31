package tatsugo

/**
 * Queue process events in a serial manner.
 */
interface Queue {
	/**
	 * Filters the events that can be processed by this queue.
	 */
	fun isApplicable(event: Event): Boolean

	/**
	 * Processes the event and returns the events that should be emitted.
	 */
	fun process(event: Event): Array<Event>
}