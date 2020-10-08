package com.ubertob.okotta.helpdesk.domain

import com.ubertob.okotta.helpdesk.lib.EntityEvent
import com.ubertob.okotta.helpdesk.lib.EntityState
import java.time.Instant

// =========================================================================================
// TicketEvents are recorded in the event store and tell us what happened to an entity and
// so are expressed in the past tense
// =========================================================================================

sealed class TicketEvent : EntityEvent

data class Created(
  override val entityKey: String,
  val title: String,
  val description: String
) : TicketEvent()

data class Started(
  override val entityKey: String,
  val userId: UserId,
  val time: Instant
) : TicketEvent()

data class Assigned(
  override val entityKey: String,
  val newUserId: UserId,
) : TicketEvent()

data class Blocked(
  override val entityKey: String
) : TicketEvent()

data class Completed(
  override val entityKey: String,
  val time: Instant
) : TicketEvent()

data class Updated(
  override val entityKey: String
) : TicketEvent()

// =========================================================================================
// TicketState reconstructs the current view of an entity from a stream of recorded events
// and so are expressed in the present tense.
//
// We start with an initial state and fold the stream of events as follows:
// (current state + recorded event) -> new state
// =========================================================================================

fun Iterable<TicketEvent>.fold(): TicketState =
  this.fold(InitialState as TicketState) { acc, e -> acc.combine(e) }

sealed class TicketState : EntityState<TicketEvent> {
  abstract override fun combine(event: TicketEvent): TicketState
}

data class InvalidState(val id: String, val prevState: TicketState, val event: TicketEvent) : TicketState() {
  override fun combine(event: TicketEvent): TicketState = this //ignore other events
}

object InitialState : TicketState() {
  override fun combine(event: TicketEvent): TicketState = when (event) {
    is Created -> InBacklog(event.entityKey)
    else -> InvalidState(event.entityKey, this, event)
  }
}

data class InBacklog(
  val entityKey: String
) : TicketState() {
  override fun combine(event: TicketEvent): TicketState = when(event) {
    is Started -> InProgress(event.entityKey, event.userId)
    is Blocked -> OnHold(event.entityKey)
    else -> InvalidState(event.entityKey, this, event)
  }
}

data class InProgress(
  val entityKey: String,
  val assignee: UserId
) : TicketState() {
  override fun combine(event: TicketEvent): TicketState = when(event) {
    is Assigned -> InProgress(event.entityKey, event.newUserId)
    is Blocked -> OnHold(event.entityKey)
    else -> InvalidState(event.entityKey, this, event)
  }
}

data class Done(
  val entityKey: String
) : TicketState() {
  override fun combine(event: TicketEvent): TicketState =
    InvalidState(event.entityKey, this, event) //there are no events allowed in Done state
}

data class OnHold(
  val entityKey: String
) : TicketState() {
  override fun combine(event: TicketEvent): TicketState =
    InvalidState(event.entityKey, this, event) //there are no events allowed in OnHold state
}

// =========================================================================================
// Commands model business domain actions on an entity and are expressed in present verb
// tense.
//
// Commands will be sent to a command handler to validate the current entity state, perform
// any business logic associated with the command action and then record the events that
// happened.
// =========================================================================================

sealed class TicketCommand

data class CommandAddToBacklog(val title: String, val description: String) : TicketCommand()

data class CommandStartWork(val id: String, val assignee: UserId) : TicketCommand()

data class CommandEndWork(val id: String) : TicketCommand()

data class CommandAssignToUser(val id: String, val assignee: UserId) : TicketCommand()
//
//data class CommandUpdateMetadata(val id: String, val title: String?, val description: String?) : TicketCommand()
//
data class CommandPutOnHold(val id: String) : TicketCommand()
//
//data class CommandReactivate(val id: String) : TicketCommand()
