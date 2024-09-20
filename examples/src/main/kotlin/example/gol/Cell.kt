package example.gol

import example.gol.Cell.InitialState
import tatsugo.*
import tatsugo.NextParticle.Companion.advance

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
	val behavior: (Cell, Message) -> NextParticle
) : Particle {

	override fun behavior(msg: Message): NextParticle = behavior(this, msg)

	/**
	 * Common utility to change the state and behavior of the cell.
	 */
	fun to(newState: CellState, newBehavior: (Cell, Message) -> NextParticle): Cell =
		Cell(address, fleetRef, newState, newBehavior)

	// messages

	sealed interface Msg : Message
	data class InitialState(
		val cellStatus: CellStatus,
		val gridSize: Int,
	) : Msg
	data class AcceptNeighbourStatus(
		val generation: Int,
		val status: CellStatus
	): Msg
}

/**
 * Initial behavior of the cell.
 */
private fun initialBehaviour(cell: Cell, msg: Message): NextParticle {
	val state = cell.state
	val pos = addressToPosition(cell.address)
	return when (msg) {
		is InitialState -> {
			// generation 0, announce status to neighbours
			val tickEvent = Grid.Tick(cell.fleetRef, pos.first, pos.second, 0, msg.cellStatus)

			// update the status of the cell
			val newState = state.update(0) { it.copy(status = msg.cellStatus) }

			advance(cell.to(newState, ::livingBehaviour)).emit(tickEvent)
		}
		else -> advance(cell)
	}
}

/**
 * The living behaviour of the cell.
 * Example when behaviour is implemented outside the cell.
 */
private fun livingBehaviour(cell: Cell, msg: Message): NextParticle {
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

				advance(cell.to(newState, ::livingBehaviour)).emit(tickEvent)
			} else {
				val newState = cell.state.update(msg.generation) { updatedCell }

				advance(cell.to(newState, ::livingBehaviour))
			}
		}
		else -> advance(cell)
	}
}
