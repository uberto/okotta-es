package com.ubertob.okotta.helpdesk.lib


interface EntityEvent {
    val entityKey: String
}

interface EntityState<E : EntityEvent> {
    fun combine(event: E): EntityState<E>
}

interface EventStore<E : EntityEvent> {
    fun store(newEvents: Iterable<E>): List<E>
    fun fetchAfter(eventSeq: EventSeq): Sequence<StoredEvent<E>>
    fun fetchByEntity(entityId: String): List<E>
}


data class EventSeq(val progressive: Int) {
    operator fun compareTo(other: EventSeq): Int = progressive.compareTo(other.progressive)
}

data class StoredEvent<E : EntityEvent>(val eventSeq: EventSeq, val event: E)



