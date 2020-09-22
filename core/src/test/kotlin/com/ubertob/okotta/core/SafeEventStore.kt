package com.ubertob.okotta.core

import java.util.concurrent.atomic.AtomicReference

interface SafeRetriever : EntityRetriever<SafeState, SafeEvent> {

    override fun retrieveById(id: SafeId): SafeState?

}


class SafeEventStore(private val eventStreamer: SafeEventStreamer) : SafeRetriever,
    EventPersister<SafeEvent> {

    override fun retrieveById(id: SafeId): SafeState? =
        eventStreamer(id)
            ?.fold()


    override fun invoke(events: Iterable<SafeEvent>) {
        eventStreamer.store(events)
    }

}


interface SafeEventStreamer : EventStreamer<SafeEvent> {
     fun store(newEvents: Iterable<SafeEvent>)
}

class SafeEventStreamerInMemory : SafeEventStreamer {

    val events = AtomicReference<List<SafeEvent>>(listOf())

    override fun store(newEvents: Iterable<SafeEvent>) {
        events.updateAndGet { it + newEvents }
    }

    override fun invoke(id: SafeId): Iterable<SafeEvent> =
        events.get()
            .filter { it.id == id }

}

