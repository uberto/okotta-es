package com.ubertob.okotta.helpdesk.lib

import java.util.concurrent.atomic.AtomicReference

class EventStoreInMemory<E : EntityEvent> : EventStore<E> {

    val events = AtomicReference<List<StoredEvent<E>>>(listOf())

    override fun store(newEvents: Iterable<E>): List<E> =
        newEvents.toList().also { ne ->
            events.updateAndGet {
                it + ne.toSavedEvents(it.size)
            }
        }

    override fun fetchByEntity(entityId: String): List<E> =
        events.get()
            .map(StoredEvent<E>::event)
            .filter { it.entityKey == entityId }

    override fun fetchAfter(eventSeq: EventSeq): Sequence<StoredEvent<E>> =
        events.get()
            .asSequence()
            .dropWhile { it.eventSeq <= eventSeq }

    private fun Iterable<E>.toSavedEvents(last: Int): List<StoredEvent<E>> =
        this.mapIndexed { index, event ->
            StoredEvent(EventSeq(last + index), event)
        }
}