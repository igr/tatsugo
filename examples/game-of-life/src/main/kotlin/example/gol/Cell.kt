package example.gol

import example.gol.Cell.InitialState
import tatsugo.*

/**
 * The lifecycle of a cell.
 * The lifecycle is responsible for creating the cell with the correct state and behavior.
 */
class CellLifecycle(private val size: Int) : ParticleLifecycle {
	override fun onCreate(ref: FleetRef): (ParticleAddress) -> Particle? {
		return {
			Cell(
				it,
				ref,
				CellState(max = calcMaxForAddress(it, size)),
				::initialBehaviour
			)
		}
	}
}

class Cell(
	val address: ParticleAddress,
	val fleetRef: FleetRef,
	val state: CellState,
	val behavior: (Cell, Msg) -> NextParticle
) : Particle {

	override fun <M> behavior(msg: M): NextParticle = behavior(this, msg as Msg)

	/**
	 * Utility to progress the cell: change the behavior.
	 */
	fun to(newState: CellState, newBehavior: (Cell, Msg) -> NextParticle): Cell =
		Cell(address, fleetRef, newState, newBehavior)

	// messages

	sealed interface Msg
	data class InitialState(
		val cellStatus: CellStatus,
		val gridSize: Int,
	) : Msg
	data class AcceptNeighbourStatus(
		val generation: Int,
		val status: CellStatus
	) : Msg

}

/**
 * Initial behavior of the cell.
 */
private fun initialBehaviour(cell: Cell, msg: Cell.Msg): NextParticle {
	val state = cell.state
	val pos = addressToPosition(cell.address)
	return when (msg) {
		is InitialState -> {
			// generation 0, announce status to neighbours
			val tickEvent = Grid.Tick(cell.fleetRef, pos.first, pos.second, 0, msg.cellStatus)

			// update the status of the cell
			val newState = state.update(0) { it.copy(status = msg.cellStatus) }

			Particle.shift(cell.to(newState, ::livingBehaviour)).emit(tickEvent)
		}
		else -> Particle.shift(cell)
	}
}

/**
 * The living behaviour of the cell.
 * Example when behaviour is implemented outside the cell.
 */
private fun livingBehaviour(cell: Cell, msg: Cell.Msg): NextParticle {
	return when (msg) {
		is Cell.AcceptNeighbourStatus -> {
			val currentCell = cell.state[msg.generation]
			val updatedCell = currentCell.addNeighbourStatus(msg.status)

			if (updatedCell.readyForProgress()) {
				// all neighbours have reported their status, ready to progress the cell
				val newCell = updatedCell.progress()
				val nextGeneration = msg.generation + 1
				val position = addressToPosition(cell.address)

				val tickEvent = Grid.Tick(cell.fleetRef, position.first, position.second, nextGeneration, newCell.status)

				val newState = cell.state.update(nextGeneration) { newCell }

				Particle.shift(cell.to(newState, ::livingBehaviour)).emit(tickEvent)
			} else {
				val newState = cell.state.update(msg.generation) { updatedCell }

				Particle.shift(cell.to(newState, ::livingBehaviour))
			}
		}
		else -> Particle.shift(cell)
	}
}
