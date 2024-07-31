# 🥔 Tatsugō

**Tatsugō** is a lightweight engine written in Kotlin for running **particles**: small chunks of code.

**Tatsugō** is made of:

+ **Fleet**: a collection of particles. Usually, a **Fleet** is a collection of particles that belong to the same domain. You can think of Fleet as a table in database, or a collection in datastore.
+ **Particle**: a small piece of code that can be run in parallel. It is run and managed by belonging Fleet.
+ **Message**: a message is sent to a particle to run it. A message can contain data that can be used by the particle.

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
