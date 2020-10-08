package com.ubertob.okotta.helpdesk.domain

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.time.Instant

class HelpdeskEventsStateTest {

    @Test
    fun `OnHold state from backlog`() {

        val events = listOf(
            Created("id", "title", "description"),
            Blocked("id")
        )

        val state = events.fold()

        expectThat(state).isEqualTo(OnHold("id"))
    }

    @Test
    fun `OnHold state from in progress`() {

        val events = listOf(
            Created("id", "title", "description"),
            Started("id", UserId("frank"), Instant.now()),
            Blocked("id")
        )

        val state = events.fold()

        expectThat(state).isEqualTo(OnHold("id"))
    }

}