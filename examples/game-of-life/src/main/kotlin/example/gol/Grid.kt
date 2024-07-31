package example.gol

import example.gol.Grid.Tick
import tatsugo.Event
import tatsugo.Events
import tatsugo.FleetMessage
import tatsugo.FleetRef

class Grid(config: GridConfig) {

	val state = GridState(config = config)

	data class Tick(
		val fleetRef: FleetRef,
		val fromX: Int,
		val fromY: Int,
		val generation: Int,
		val newStatus: CellStatus,
	) : Event

	data class EndOfGame(val generation: Int) : Event

	fun on(tick: Tick): Array<Event> = tick(this, tick)

	fun resetTime() {
		state.startTime = System.currentTimeMillis()
	}
}

private fun tick(grid: Grid, event: Tick): Array<Event> {
	val config = grid.state.config

	if (grid.state.gameFinished(event.generation)) {
		if (!grid.state.endReported) {
			println("THE END")
			grid.state.endReported = true
			return Events.of(Grid.EndOfGame(event.generation))
		}
		return Events.none
	}

	grid.state.updateStatus(
		event.generation,
		event.fromX,
		event.fromY,
		event.newStatus
	)
	if (grid.state.generationFinished(event.generation)) {
		grid.state.printMatrix(event.generation)
	}
	return announceStateToNeighbours(event, config.gridSize)
}

private fun announceStateToNeighbours(tick: Tick, gridSize: Int): Array<Event> {
	val x = tick.fromX
	val y = tick.fromY
	return listOf(
		(x - 1 to y - 1),
		(x - 1 to y),
		(x - 1 to y + 1),
		(x to y - 1),
		(x to y + 1),
		(x + 1 to y - 1),
		(x + 1 to y),
		(x + 1 to y + 1),
	).filter {
		if (it.first < 0 || it.first >= gridSize) false
		else if (it.second < 0 || it.second >= gridSize) false
		else true
	}.map {
		FleetMessage(tick.fleetRef, it.address(), Cell.AcceptNeighbourStatus(tick.generation, tick.newStatus))
	}.toTypedArray()
}
