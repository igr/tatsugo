package example.gol

import example.gol.Grid.Message
import example.gol.Grid.Tick
import tatsugo.FleetRef
import tatsugo.Particle
import tatsugo.ParticleAddress

val startTime = System.currentTimeMillis()

data class GridConfig(
	val gridSize: Int,
	val maxGenerations: Int,
) {
	fun createGrid() = Array(gridSize) { Array(gridSize) { CellStatus.Dead } }
}

class Grid(
	override val address: ParticleAddress,
	val config: GridConfig,
	val behavior: suspend (Grid, Message) -> Grid
) : Particle<Grid, Message> {

	override suspend fun behavior(msg: Message): Grid = behavior(this, msg)

	sealed interface Message
	data class Tick(
		val fleetRef: FleetRef,
		val fromX: Int,
		val fromY: Int,
		val generation: Int,
		val newStatus: CellStatus,
		val particleAddress: ParticleAddress = address,
	) : Message

	// internal state

	internal val counts: MutableMap<Int, Int> = mutableMapOf()

	val db = mutableMapOf(0 to config.createGrid())

	companion object {
		fun new(size: Int, maxGeneration: Int): Grid {
			return Grid(address, GridConfig(size, maxGeneration), ::gridBehaviour)
		}

		var address = ParticleAddress("GRID")
	}
}

suspend fun gridBehaviour(grid: Grid, msg: Message): Grid {
	val counts = grid.counts
	val config = grid.config
	val size = config.gridSize

	return when (msg) {
		// cell is updated
		is Tick -> {
			if (msg.generation > config.maxGenerations) {
				//println("max generation reached")
				return grid
			}

			// increment count
			counts[msg.generation] = counts.getOrDefault(msg.generation, 0) + 1

			val g = grid.db.computeIfAbsent(msg.generation) { config.createGrid() }
			g[msg.fromX][msg.fromY] = msg.newStatus

			if (counts[msg.generation]!! >= size * size) {
				printGrid(msg.generation, size, g)
			}
			announceStateToNeighbours(msg, size)
			grid
		}
	}
}

private fun printGrid(
	generation: Int,
	gridSize: Int,
	g: Array<Array<CellStatus>>,
) {
	println("\n---GRID for generation $generation after ${System.currentTimeMillis() - startTime}ms---")

	for (i in 0..<gridSize) {
		for (j in 0..<gridSize) {
			print(if (g[i][j] == CellStatus.Alive) "*" else ".")
		}
		println()
	}
}

private suspend fun announceStateToNeighbours(tick: Tick, gridSize: Int) {
	val x = tick.fromX
	val y = tick.fromY
	listOf(
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
	}.forEach {
		tick.fleetRef.send(it.address(), Cell.AcceptNeighbourStatus(tick.generation, tick.newStatus))
	}
}
