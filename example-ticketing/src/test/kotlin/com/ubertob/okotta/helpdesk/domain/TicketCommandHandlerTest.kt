package com.ubertob.okotta.helpdesk.domain

import com.example.events.CommandAddToBacklog
import com.example.events.Started
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo

internal class TicketCommandHandlerTest {

    val es = TicketEventStore()
    val ch = TicketCommandHandler(es)
    val p = TicketsProjection(es::fetchAfter)

    @Test
    fun `add a new ticket to backlog`() {
        val c = CommandAddToBacklog("my title", "doing some stuff")

        val event = ch(c).single()

        expectThat(event).isA<Started>()

        expectThat(p.`get single ticket`(event.entityKey))
            .isEqualTo(
                TicketsProjectionRow(
                    ticketId = event.entityKey,
                    title = "my title",
                    description = "doing some stuff",
                    kanbanColumn = TicketStatus.Backlog
                )
            )

    }
}