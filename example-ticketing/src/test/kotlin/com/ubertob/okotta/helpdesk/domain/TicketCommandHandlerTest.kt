package com.ubertob.okotta.helpdesk.domain

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

        expectThat(event).isA<Created>()

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

    @Test
    fun `add a new ticket to backlog and started working on`() {
        val c = CommandAddToBacklog("my title", "doing some stuff")
        val id = ch(c).single().entityKey
        val developer = UserId("Frank")

        val start = CommandStartWork(id, developer)
        val event = ch(start).single()

        expectThat(event).isA<Started>()

        expectThat(p.`get single ticket`(id))
            .isEqualTo(
                TicketsProjectionRow(
                    ticketId = id,
                    title = "my title",
                    description = "doing some stuff",
                    kanbanColumn = TicketStatus.InDevelopment,
                    assignee = developer
                )
            )

    }
}