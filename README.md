# 🥔 Tatsugō

**Tatsugō** is a lightweight Event driven engine written in Kotlin for running **particles** and event handlers.

**Tatsugō** is made of:

+ **Bus**: event bus abstraction; essentially a hot flow that broadcasts events to all subscribers;
+ **Event**: a event that is sent via the bus to all subscribers;
+ **Particle**: a small piece of code defined by address and behaviour, that runs in response to messages, in queue;
+ **Fleet**: a collection of particles, usually that belong to the same domain. You can think of Fleet as a table in database, or a collection in datastore.
+ **Message**: a message to a particle.

## Events and event handlers

Event handler is usually a single instance that process all events of a certain type.


## Particle

Particle is a unit of work defined by:

+ _address_,
+ optional _data state_,
+ _behavior_ i.e. workflow state machine.

Particles are never run directly. Instead, they are invoked by Fleet when messages are sent to them.

The user does not instantiate particles.
They are created by the Fleet when needed.
There is no guarantee that once created, a particle will stay in memory.
It is up to the Fleet to decide when to create and destroy particles.

## Fleet

Fleet is a message processor that manages particles.
All messages are coming in the same way: through the _queue_.

SOON: Fleet may be also distributed among different machines.

## Messages

Message is a simple data sent to particles.
