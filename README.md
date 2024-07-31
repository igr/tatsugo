# 🥔 Tatsugō

**Tatsugō** is a lightweight Event-driven engine written in Kotlin.

It is uniquely designed: allows state isolation and writing of safe unsynchronized code.

## Overview

**Tatsugō** offers a couple of abstractions.

🚌 **Bus** is a simple event bus that broadcasts events to all subscribers.

🎭 **Event handler** is a simple, traditional event processor.
Handler process an event and return a list of new events that should be emitted.
This is one distinct feature of Tatsugō:
events are never sent directly, from the function's code, but provided withing the returned value.
There is no guarantee when and how the events will be processed.

🧵 **Queue** is a somewhat advanced event handler.
It filters the events and sends them to internal queue.
The queued events are processed in serial order.
Usually, one queue instance processes all its events.

⚛️ **Particle** is an isolated unit of work, with address, behaviour and, usually, internal state.
They remind of actors.
Particles communicate via _messages_, that are events enhanced with the target address.
Particles are never created and run directly.

🚢 **Fleet** manages particles: it creates, destroys and runs them.
Fleet is a queue, too; hence all messages are processed in serial order.

## Usage

With **Tatsugō**, you essentially write an event-driven code.
However, the idea is to write a code that is safe and easy to reason about,
without using _any_ synchronization primitives!
The state becomes isolated and there is no need to use locks.

Essentially, you have three options where to put your code:

1. **Event handler** is a simple event processor.
It should be used only for "global" tasks, like logging, metrics, etc.
There is no guarantee when the event will be processed.
2. **Queue** is usually a singleton that takes care of some global states. It processes events in serial order. The queue code is lock-free!
3. **Particle** represents the behavior around a single component of a state. Each particle has a unique address (database row id, for example.) There could (and should) be a lot of particles running at once! Since they operate only on their own state, they are isolated and do not need locks.


## Example: Game of Life

We have the following components:

+ ⚛️ `Cell` is a _particle_ that represents a single cell in the game.
It has a state: alive or dead (and the address, of course.)
Each particle only knows about its own state.
As particles cannot exist on their own, they are created and managed by the Fleet.
+ 🧵 `Grid` is a _queue_ that manages the cells, i.e., the board.
It is a singleton unit that receives information of the cells' state changes.
On each cell change event, `Grid` sends a notification message to all neighbors of the changed cell to its fleet.
+ 🎭 `StatsCounter` - simple event handler that counts the events and prints the statistics at the end of the game, after the last generation.

The example:

+ grid size is `100x100`
+ number of generations is `100`
+ total number of events: `8 900 400` (!)
+ total execution time is `2.1s` (average) on my machine (M2).
+ the code is lock-free! No synchronization primitives are used.

![](gol.png)
