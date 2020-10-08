package com.ubertob.okotta.core

typealias SafeId = EntityId

sealed class SafeEvent : DomainEvent
data class Opened(override val id: SafeId) : SafeEvent()
data class Closed(override val id: SafeId) : SafeEvent()
data class Locked(override val id: SafeId, val code: String) : SafeEvent()
data class Unlocked(override val id: SafeId) : SafeEvent()

fun Iterable<SafeEvent>.fold(): SafeState =
        this.fold(InitialState as SafeState) { acc, e -> acc.combine(e) }


sealed class SafeState : EntityState<SafeEvent> {
    abstract override fun combine(event: SafeEvent): SafeState
}

object InitialState : SafeState() {
    override fun combine(event: SafeEvent): SafeState =
            when (event) {
                is Opened -> Open(event.id)
                else -> InvalidState(event.id,this, event)
            }

}

data class Open internal constructor(
        val id: SafeId
) : SafeState() {
    override fun combine(event: SafeEvent): SafeState =
        when (event) {
            is Closed -> AlarmInactive(event.id)
            is Opened,
            is Locked,
            is Unlocked -> InvalidState(event.id, this, event)
        }
}

data class AlarmInactive internal constructor(
        val id: SafeId
) : SafeState() {
    override fun combine(event: SafeEvent): SafeState =
        when (event) {
            is Opened -> Open(event.id)
            is Locked -> AlarmActive(event.id, event.code)
            is Closed,
            is Unlocked -> InvalidState(event.id, this, event)
        }
}

data class AlarmActive internal constructor(
        val id: SafeId,
        val code: String
) : SafeState() {
    override fun combine(event: SafeEvent): SafeState =
        when (event) {
            is Unlocked -> AlarmInactive(event.id)
            is Opened,
            is Closed,
            is Locked -> InvalidState(event.id, this, event)
        }
}

data class InvalidState(val id: SafeId, val prevState: SafeState, val event: SafeEvent): SafeState() {
    override fun combine(event: SafeEvent): SafeState = this //ignore other events
}