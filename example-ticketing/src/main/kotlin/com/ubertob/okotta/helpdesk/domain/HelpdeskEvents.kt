package com.example.events

import com.ubertob.okotta.helpdesk.lib.EntityEvent
import com.ubertob.okotta.helpdesk.lib.EntityState

sealed class TicketState : EntityState

data class Done(
  override val entityKey: String
) : TicketState()

data class InBacklog(
  override val entityKey: String
) : TicketState()

data class InProgress(
  override val entityKey: String
) : TicketState()

data class OnHold(
  override val entityKey: String
) : TicketState()

data class Start(
  override val entityKey: String
) : TicketState()


sealed class TicketEvent : EntityEvent

data class Started(
  override val entityKey: String,
  val title: String,
  val description: String
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

data class Created(
  override val entityKey: String
) : TicketEvent()


sealed class TicketCommand

data class CommandAddToBacklog(val title: String, val description: String) : TicketCommand()

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
