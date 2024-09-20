package example.gol

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import tatsugo.Bus
import tatsugo.FleetMessage
import tatsugo.FleetRef
import tatsugo.flow.spawnFlowFleet
import tatsugo.flow.startFlowBus

//private const val SIZE = 21
//private const val MAX_GENERATIONS = 6
private const val SIZE = 100
private const val MAX_GENERATIONS = 100

suspend fun main(): Unit = coroutineScope {

	val bus = startFlowBus()

	val grid = Grid(GridConfig(SIZE, MAX_GENERATIONS))

	bus.subscribe {
		when (it) {
			is Grid.Tick -> grid.on(it)
			else -> Bus.none
		}
	}

	val fleet = spawnFlowFleet("cells", bus)

	fleet.bind(CellLifecycle(SIZE))

	delay(1000) // give it time to start

	initializeGrid(bus, fleet.ref(), SIZE)

	println("Initial state sent")
}

private suspend fun initializeGrid(bus: Bus, fleetRef: FleetRef, size: Int) {
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
