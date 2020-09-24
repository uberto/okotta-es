package com.ubertob.okotta.helpdesk.domain

import com.example.events.TicketCommand
import com.example.events.TicketEvent


class TicketCommandHandler(
    val eventStore: TicketEventStore
) : (TicketCommand) -> List<TicketEvent> {

    override fun invoke(command: TicketCommand): List<TicketEvent> {
        val events: List<TicketEvent> = TODO() //when(command){}

        eventStore.store(events)
        return events
    }

}
