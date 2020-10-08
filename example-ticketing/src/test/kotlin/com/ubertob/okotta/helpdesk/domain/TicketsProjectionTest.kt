package com.ubertob.okotta.helpdesk.domain

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TicketsProjectionTest {

    val es = TicketEventStore()
    val ch = TicketCommandHandler(es)
    val p = TicketsProjection(es::fetchAfter)

    @Test
    fun `return counts of tickets in each state`() {
        val id1 = ch(CommandAddToBacklog("my title1", "doing some stuff")).single().entityKey
        ch(CommandAddToBacklog("my title2", "doing some stuff"))
        ch(CommandAddToBacklog("my title3", "doing some stuff"))

        expectThat(p.getCounts()).isEqualTo(
            mapOf(TicketStatus.Backlog to 3)
        )

        ch(CommandStartWork(id1, UserId("bob")))

        expectThat(p.getCounts()).isEqualTo(
            mapOf(
                TicketStatus.Backlog to 2,
                TicketStatus.InDevelopment to 1
            )
        )
    }

}