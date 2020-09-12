package com.ubertob.okotta.core

import java.util.concurrent.atomic.AtomicReference

interface ToDoListRetriever : EntityRetriever<ToDoListState, ToDoListEvent> {

    fun retrieveByName(user: User, listName: ListName): ToDoListState?

    override fun retrieveById(id: ToDoListId): ToDoListState?

}


class ToDoListEventStore(private val eventStreamer: ToDoListEventStreamer) : ToDoListRetriever,
    EventPersister<ToDoListEvent> {

    override fun retrieveById(id: ToDoListId): ToDoListState? =
        eventStreamer(id)
            ?.fold()

    
    override fun retrieveByName(user: User, listName: ListName): ToDoListState? =
        eventStreamer.retrieveIdFromName(user, listName)
            ?.let(::retrieveById)

    override fun invoke(events: Iterable<ToDoListEvent>) {
        eventStreamer.store(events)
    }

}


interface ToDoListEventStreamer : EventStreamer<ToDoListEvent> {
    fun retrieveIdFromName(user: User, listName: ListName): ToDoListId?
    fun store(newEvents: Iterable<ToDoListEvent>)
}

class ToDoListEventStreamerInMemory : ToDoListEventStreamer {

    val events = AtomicReference<List<ToDoListEvent>>(listOf())

    override fun retrieveIdFromName(user: User, listName: ListName): ToDoListId? =
        events.get()
            .firstOrNull { it == ListCreated(it.id, user, listName) }
            ?.id

    override fun store(newEvents: Iterable<ToDoListEvent>) {
        events.updateAndGet { it + newEvents }
    }

    override fun invoke(id: ToDoListId): Iterable<ToDoListEvent> =
        events.get()
            .filter { it.id == id }

}

