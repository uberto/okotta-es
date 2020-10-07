package com.ubertob.okotta.helpdesk.domain

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEmpty
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

        expectThat(p.getTicket(event.entityKey))
            .isEqualTo(
                TicketsProjectionRow(
                    title = "my title",
                    description = "doing some stuff",
                    kanbanColumn = TicketStatus.Backlog
                )
            )

    }

    @Test
    fun `add a new ticket to backlog and started working on`() {
        val developer = UserId("Frank")

        val id = ch(CommandAddToBacklog("my title", "doing some stuff")).single().entityKey
        val event = ch(CommandStartWork(id, developer)).single()

        expectThat(event).isA<Started>()

        expectThat(p.getTicket(id))
            .isEqualTo(
                TicketsProjectionRow(
                    title = "my title",
                    description = "doing some stuff",
                    kanbanColumn = TicketStatus.InDevelopment,
                    assignee = developer
                )
            )
    }

    @Test
    fun `add a new ticket to backlog, work on it, and complete it`() {
        val developer = UserId("Frank")

        val id = ch(CommandAddToBacklog("my title", "doing some stuff")).single().entityKey
        ch(CommandStartWork(id, developer))
        val event = ch(CommandEndWork(id)).single()

        expectThat(event).isA<Completed>()

        expectThat(p.getTicket(id))
            .isEqualTo(
                TicketsProjectionRow(
                    title = "my title",
                    description = "doing some stuff",
                    kanbanColumn = TicketStatus.Done,
                    assignee = developer
                )
            )
    }

    @Test
    fun `assign a ticket to another person multiple times`() {
        val frank = UserId("Frank")
        val alice = UserId("Alice")
        val bob = UserId("Bob")

        val id = ch(CommandAddToBacklog("my title", "doing some stuff")).single().entityKey
        ch(CommandStartWork(id, frank)).single()

        ch(CommandAssignToUser(id, alice)).single().let { event ->
            expectThat(event).isA<Assigned>()

            expectThat(p.getTicket(id))
                .isEqualTo(
                    TicketsProjectionRow(
                        title = "my title",
                        description = "doing some stuff",
                        kanbanColumn = TicketStatus.InDevelopment,
                        assignee = alice
                    )
                )
        }

        ch(CommandAssignToUser(id, bob)).single().let { event ->
            expectThat(event).isA<Assigned>()

            expectThat(p.getTicket(id))
                .isEqualTo(
                    TicketsProjectionRow(
                        title = "my title",
                        description = "doing some stuff",
                        kanbanColumn = TicketStatus.InDevelopment,
                        assignee = bob
                    )
                )
        }
    }


    @Test
    fun `trying to assing a ticket to the same person doesn't generate events`() {
        val frank = UserId("Frank")

        val id = ch(CommandAddToBacklog("my title", "doing some stuff")).single().entityKey
        ch(CommandStartWork(id, frank))
        val events = ch(CommandAssignToUser(id, frank))

        expectThat(events).isEmpty()
    }
}