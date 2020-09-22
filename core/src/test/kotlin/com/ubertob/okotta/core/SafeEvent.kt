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
                else -> this //ignore other events
            }

}

data class Open internal constructor(
        val id: SafeId
) : SafeState() {
    override fun combine(event: SafeEvent): SafeState =
            when (event) {
                is Closed -> AlarmInactive(id)
                else -> this //ignore other events

            }
}

data class AlarmInactive internal constructor(
        val id: SafeId
) : SafeState() {
    override fun combine(event: SafeEvent): SafeState =
            when (event) {
                is Opened -> Open(id)
                is Locked -> AlarmActive(id, event.code)
                else -> this //ignore other events
            }

}

data class AlarmActive internal constructor(
        val id: SafeId,
        val code: String
) : SafeState() {
    override fun combine(event: SafeEvent): SafeState =
            when (event) {
                is Unlocked -> AlarmInactive(id)
                else -> this //ignore other events
            }
}
