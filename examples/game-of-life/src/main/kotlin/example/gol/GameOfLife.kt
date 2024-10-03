package example.gol

import kotlinx.coroutines.coroutineScope
import tatsugo.*
import tatsugo.fleet.spawnSimpleFleet
import tatsugo.flow.startChannelBus

//private const val SIZE = 21
//private const val MAX_GENERATIONS = 6
private const val SIZE = 100
private const val MAX_GENERATIONS = 100

suspend fun main(): Unit = coroutineScope {

	// BUS
	// First we need to create a bus to handle the events.

//	val bus = startFlowBus()        // slower
	val bus = startChannelBus()     // faster

	// Event handler: STATS

	bus.subscribe(StatsHandler(SIZE, MAX_GENERATIONS))

	// Queue: GRID

	val grid = Grid(GridConfig(SIZE, MAX_GENERATIONS))

	bus.bind(object : Queue {
		override fun isApplicable(event: Event): Boolean = event is Grid.Tick
		override fun process(event: Event): Array<Event> = grid.on(event as Grid.Tick)
	})

	// Fleet: CELLS

	val fleet = spawnSimpleFleet("cells")
	bus.bind(fleet.asQueue())

	fleet.bind(CellLifecycle(SIZE))

	// START THE GAME

	grid.resetTime()
	initializeGrid(bus, fleet.ref(), SIZE)

	println("Initial state sent; live and let die!")
}

private fun initializeGrid(bus: Bus, fleetRef: FleetRef, size: Int) {
	for (i in 0..<size) {
		for (j in 0..<size) {
			val addr = (i to j).address()
			val msg = if ((i + j) % 2 == 0) {
				Cell.InitialState(CellStatus.Alive, size)
			} else {
				Cell.InitialState(CellStatus.Dead, size)
			}
			bus.emit(FleetMessage(fleetRef, addr, msg))
		}
	}
}
