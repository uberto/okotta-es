package com.ubertob.okotta.helpdesk.domain

import com.ubertob.okotta.helpdesk.lib.EntityEvent
import com.ubertob.okotta.helpdesk.lib.EntityState
import java.time.Instant

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
  override fun combine(event: TicketEvent): TicketState {
    return TODO("not implemented")
  }
}

data class InProgress(
  val entityKey: String
) : TicketState() {
  override fun combine(event: TicketEvent): TicketState {
    return TODO("not implemented")
  }
}

data class Done(
  val entityKey: String
) : TicketState() {
  override fun combine(event: TicketEvent): TicketState {
    return TODO("not implemented")
  }
}


data class OnHold(
  val entityKey: String
) : TicketState() {
  override fun combine(event: TicketEvent): TicketState {
    return TODO("not implemented")
  }
}


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

data class Blocked(
  override val entityKey: String
) : TicketEvent()

data class Completed(
  override val entityKey: String
) : TicketEvent()

data class Updated(
  override val entityKey: String
) : TicketEvent()

fun Iterable<TicketEvent>.fold(): TicketState =
  this.fold(InitialState as TicketState) { acc, e -> acc.combine(e) }


sealed class TicketCommand

data class CommandAddToBacklog(val title: String, val description: String) : TicketCommand()

data class CommandStartWork(val id: String, val assignee: UserId) : TicketCommand()

data class CommandEndWork(val id: String) : TicketCommand()

//fun StartedCommand(state: TicketState): TicketCommand() {
//  TODO()
//}
//
//fun BlockedCommand(state: TicketState): TicketCommand() {
//  TODO()
//}
//
//fun CompletedCommand(state: TicketState): TicketCommand() {
//  TODO()
//}
//
//fun UpdatedCommand(state: TicketState): TicketCommand() {
//  TODO()
//}
//
//fun BlockedCommand(state: TicketState): TicketCommand() {
//  TODO()
//}
//
//fun CreatedCommand(state: TicketState): TicketCommand() {
//  TODO()
//}
