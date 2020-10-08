package com.ubertob.okotta.helpdesk.domain

import java.time.Instant
import java.util.*


class TicketCommandHandler(
    val eventStore: TicketEventStore
) : (TicketCommand) -> List<TicketEvent> {

    fun getTicket(ticketId: String): TicketState =
        eventStore.fetchByEntity(ticketId).fold()

    override fun invoke(command: TicketCommand): List<TicketEvent> {
        val events: List<TicketEvent> = evaluateCommand(command)

        eventStore.store(events)
        return events
    }

    private fun evaluateCommand(command: TicketCommand): List<TicketEvent> =
        when (command) {
            is CommandAddToBacklog -> Created(
                entityKey = UUID.randomUUID().toString(),
                title = command.title,
                description = command.description
            ).toSingleList()  //todo add check if ticket title already exist?

            is CommandStartWork -> when (val ticket = getTicket(command.id)) {
                is InBacklog -> Started(ticket.entityKey, command.assignee, Instant.now()).toSingleList()
                InitialState,
                is Done,
                is InProgress,
                is OnHold,
                is InvalidState -> throw RuntimeException("Invalid state! $ticket")
            }

            is CommandEndWork -> when (val ticket = getTicket(command.id)) {
                is InProgress -> Completed(ticket.entityKey, Instant.now()).toSingleList()
                InitialState,
                is InBacklog,
                is Done,
                is OnHold,
                is InvalidState -> throw RuntimeException("Invalid state! $ticket")
            }

            is CommandAssignToUser -> when (val ticket = getTicket(command.id)) {
                is InProgress -> if (ticket.assignee != command.assignee) {
                    Assigned(ticket.entityKey, command.assignee).toSingleList()
                } else {
                    emptyList()
                }
                InitialState,
                is InBacklog,
                is Done,
                is OnHold,
                is InvalidState -> throw RuntimeException("Invalid state! $ticket")
            }
            is CommandPutOnHold -> when (val ticket = getTicket(command.id)) {
                is InBacklog -> Blocked(ticket.entityKey).toSingleList()
                is InProgress -> Blocked(ticket.entityKey).toSingleList()
                InitialState,
                is Done,
                is OnHold,
                is InvalidState -> throw RuntimeException("Invalid state! $ticket")
            }
            is CommandUpdateMetadata -> when (val ticket = getTicket(command.id)) {
                is InProgress -> Updated(ticket.entityKey, command.description).toSingleList()
                InitialState,
                is InBacklog,
                is Done,
                is OnHold,
                is InvalidState -> throw RuntimeException("Invalid state! $ticket")
            }
        }

}

private fun TicketEvent.toSingleList(): List<TicketEvent> = listOf(this)
