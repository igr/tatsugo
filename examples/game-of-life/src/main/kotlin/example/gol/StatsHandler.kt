package example.gol

import tatsugo.Event
import tatsugo.Events
import tatsugo.FleetMessage

class StatsHandler(size: Int, private val maxGen: Int): (Event) -> Array<Event> {
	private var totalCount = 0
	private var tickCount = 0
	private var fleetCount = 0

	private var lastGenCount = 0
	private var theEndWasAnnounced = false
	private val maxTicks = size * size

	override fun invoke(event: Event): Array<Event> {
		totalCount++
		when (event) {
			is Grid.EndOfGame -> {
				// The end is emitted and received.
				// However, depending on the Bus implementation,
				// there might still be remaining messages that this
				// event handler needs to process.
				// In other words, the end event may come before some last Tick events.
				// Again, this depends on the Bus implementation (!)
				theEndWasAnnounced = true
				totalCount--    // we don't want this event to be counted
				println("The end was received")
				theEnd(event.generation)
			}
			is Grid.Tick -> {
				tickCount++
				if (event.generation == maxGen) {
					// we are now in the echoes of the LAST generation
					// the real END OF THE GAME
					lastGenCount++
					if (lastGenCount == maxTicks) {
						theEnd(event.generation)
						return Events.none
					}
				}
			}
			is FleetMessage<*> -> {
				fleetCount++
			}
		}
		return Events.none
	}

	private fun theEnd(generation: Int) {
		println("The end was announced: $theEndWasAnnounced")
		println("Game finished at generation $generation after $totalCount events ($tickCount ticks + $fleetCount fleet messages)")
	}
}