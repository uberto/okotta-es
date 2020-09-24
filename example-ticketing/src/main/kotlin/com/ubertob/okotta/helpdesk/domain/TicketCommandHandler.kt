package com.ubertob.okotta.helpdesk.domain

import com.example.events.TicketCommand
import com.example.events.TicketEvent
import com.example.events.TicketState


class TicketCommandHandler(
    val eventStore: TicketEventStore
) : (TicketCommand) -> List<TicketEvent> {

    override fun invoke(command: TicketCommand): List<TicketEvent> {
        return TODO("not implemented")
    }

}
