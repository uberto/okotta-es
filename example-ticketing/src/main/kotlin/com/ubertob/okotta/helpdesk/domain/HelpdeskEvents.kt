package com.example.events

import kotlin.String

sealed class HelpdeskState

data class Done(
  val id: String
) : HelpdeskState()

data class InBacklog(
  val id: String
) : HelpdeskState()

data class InProgress(
  val id: String
) : HelpdeskState()

data class OnHold(
  val id: String
) : HelpdeskState()

data class Start(
  val id: String
) : HelpdeskState()

sealed class HelpdeskEvent

data class Started(
  val id: String
) : HelpdeskEvent()

data class Blocked(
  val id: String
) : HelpdeskEvent()

data class Completed(
  val id: String
) : HelpdeskEvent()

data class Updated(
  val id: String
) : HelpdeskEvent()

data class Created(
  val id: String
) : HelpdeskEvent()

fun StartedCommand(state: HelpdeskState): HelpdeskEvent {
  TODO()
}

fun BlockedCommand(state: HelpdeskState): HelpdeskEvent {
  TODO()
}

fun CompletedCommand(state: HelpdeskState): HelpdeskEvent {
  TODO()
}

fun UpdatedCommand(state: HelpdeskState): HelpdeskEvent {
  TODO()
}

fun BlockedCommand(state: HelpdeskState): HelpdeskEvent {
  TODO()
}

fun CreatedCommand(state: HelpdeskState): HelpdeskEvent {
  TODO()
}
