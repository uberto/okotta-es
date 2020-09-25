package com.ubertob.okotta.helpdesk.domain

import com.example.events.CommandAddToBacklog
import com.example.events.Started
import com.example.events.TicketCommand
import com.example.events.TicketEvent
import java.util.*


class TicketCommandHandler(
    val eventStore: TicketEventStore
) : (TicketCommand) -> List<TicketEvent> {

    override fun invoke(command: TicketCommand): List<TicketEvent> {
        val events: List<TicketEvent> = evaluateCommand(command)

        eventStore.store(events)
        return events
    }

    private fun evaluateCommand(command: TicketCommand): List<TicketEvent> =
        when (command) {
            is CommandAddToBacklog -> Started(
                entityKey = UUID.randomUUID().toString(),
                title = command.title,
                description = command.description
            ).toSingleList()  //todo add check if ticket title already exist?
        }

}

private fun TicketEvent.toSingleList(): List<TicketEvent> = listOf(this)
