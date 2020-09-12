package com.ubertob.okotta.core

import java.util.*

data class EntityId(val raw: UUID) {
    companion object {
        fun mint() = EntityId(UUID.randomUUID())
    }
}


interface DomainEvent {
    val id: EntityId
}

interface EntityState<in E : DomainEvent> {
    fun combine(event: E): EntityState<E>
}

interface EntityRetriever<out S : EntityState<E>, in E : DomainEvent> {
    fun retrieveById(id: EntityId): S?
}

typealias EventStreamer<E> = (EntityId) -> Iterable<E>?
typealias EventPersister<E> = (Iterable<E>) -> Unit