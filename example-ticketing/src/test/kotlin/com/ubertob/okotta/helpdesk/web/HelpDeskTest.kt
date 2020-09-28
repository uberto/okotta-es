package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.CommandAddToBacklog
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull


internal class HelpDeskTest {

    @Test
    fun `create a new ticket`() {
        val handler = helpDeskBuilder()

        val response = handler(
            Request(POST, "/ticket").body(
                """{
                    "title": "Houston",
                    "description": "We have a problem..."
                }"""
            )
        )

        expectThat(response.status).isEqualTo(OK)
        val jsonContent = response.bodyAsJson()
        expectThat(jsonContent["id"]).isNotNull()
    }

    @Test
    fun `retrieve a ticket`() {
        val handler = helpDeskBuilder()

        // setup: create a ticket
        val id = handler.commandHandler(CommandAddToBacklog("title1", "description1")).first().entityKey

        val response = handler(Request(GET, "/ticket/$id"))

        expectThat(response.status).isEqualTo(OK)
        val jsonContent = response.bodyAsJson()
        expectThat(jsonContent["title"].asText()).isEqualTo("title1")
        expectThat(jsonContent["description"].asText()).isEqualTo("description1")
        expectThat(jsonContent["kanban_column"].asText()).isEqualTo("Backlog")
    }

    @Test
    fun `retrieve a ticket error handling`() {
        val handler = helpDeskBuilder()

        handler(Request(GET, "/ticket/id_not_exists")).run {
            expectThat(status).isEqualTo(NOT_FOUND)
        }
    }
}


