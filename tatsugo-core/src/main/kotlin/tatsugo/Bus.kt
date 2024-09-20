package tatsugo

interface Bus {

	/**
	 * Subscribe to events.
	 */
	fun subscribe(handler: suspend (Event) -> Array<Event>)

	/**
	 * Emit an event.
	 */
	suspend fun emit(event: Event)

	companion object {
		val none: Array<Event> = arrayOf()
	}
}