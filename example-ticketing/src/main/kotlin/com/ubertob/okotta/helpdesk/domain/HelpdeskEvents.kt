package com.example.events

import kotlin.String

sealed class TicketState

data class Done(
  val id: String
) : TicketState()

data class InBacklog(
  val id: String
) : TicketState()

data class InProgress(
  val id: String
) : TicketState()

data class OnHold(
  val id: String
) : TicketState()

data class Start(
  val id: String
) : TicketState()

sealed class TicketEvent

data class Started(
  val id: String
) : TicketEvent()

data class Blocked(
  val id: String
) : TicketEvent()

data class Completed(
  val id: String
) : TicketEvent()

data class Updated(
  val id: String
) : TicketEvent()

data class Created(
  val id: String
) : TicketEvent()



sealed class TicketCommand
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
