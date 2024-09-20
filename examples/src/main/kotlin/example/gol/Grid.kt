package example.gol

import example.gol.Grid.Tick
import tatsugo.Bus
import tatsugo.Event
import tatsugo.FleetMessage
import tatsugo.FleetRef

class Grid(config: GridConfig) {

	data class Tick(
		val fleetRef: FleetRef,
		val fromX: Int,
		val fromY: Int,
		val generation: Int,
		val newStatus: CellStatus,
	) : Event

	val state = GridState(config = config)

	fun on(tick: Tick): Array<Event> = tick(this, tick)
}

private fun tick(grid: Grid, event: Tick): Array<Event> {
	val config = grid.state.config

	if (grid.state.gameFinished(event.generation)) {
		return Bus.none
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
