package com.ubertob.okotta.helpdesk.web

import com.ubertob.kondor.json.jsonnode.JsonNode
import com.ubertob.kondor.json.jsonnode.parseJsonNode
import com.ubertob.okotta.helpdesk.domain.CommandAddToBacklog
import com.ubertob.okotta.helpdesk.domain.CommandStartWork
import com.ubertob.okotta.helpdesk.domain.UserId
import com.ubertob.okotta.helpdesk.json.JAddTicketResponse
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotBlank
import strikt.assertions.isNotNull


internal class HelpDeskTest {

    private val handler = helpDeskBuilder()

    @Test
    fun `create a new ticket`() {
        with(
            handler(
                Request(POST, "/ticket").body(
                    """{
                            "title": "Houston",
                            "description": "We have a problem..."
                        }"""
                )
            )
        ) {
            expectThat(status).isEqualTo(OK)
            val result = JAddTicketResponse.fromJson(bodyString()).orNull()
            expectThat(result)
                .isNotNull()
                .get { id }.isNotBlank()
        }
    }

    @Test
    fun `retrieve a ticket`() {
        // setup: create a ticket
        val id = handler.commandHandler(CommandAddToBacklog("title1", "description1")).first().entityKey

        with(handler(Request(GET, "/ticket/$id"))) {
            expectThat(status).isEqualTo(OK)
            expectThat(parseJsonNode(bodyString()).orThrow()).isEquivalentTo(
                """{
                    "id": "$id",
                    "title": "title1",
                    "description": "description1",
                    "kanban_column" : "Backlog"
                }"""
            )
        }

        with(handler(Request(GET, "/tickets"))) {
            expectThat(status).isEqualTo(OK)
            expectThat(parseJsonNode(bodyString()).orThrow()).isEquivalentTo(
                """[{
                    "id": "$id",
                    "title": "title1",
                    "description": "description1",
                    "kanban_column" : "Backlog"
                }]"""
            )
        }
    }

    @Test
    fun `retrieve a ticket error handling`() {
        with(handler(Request(GET, "/ticket/id_not_exists"))) {
            expectThat(status).isEqualTo(NOT_FOUND)
        }
    }

    @Test
    fun `put ticket onhold from backlog`() {
        // setup: create a ticket and start work on it
        val id = handler.commandHandler(CommandAddToBacklog("title1", "description1")).first().entityKey

        with(
            handler(
                Request(POST, "/ticket/$id/onhold")
            )
        ) {
            expectThat(status).isEqualTo(NO_CONTENT)
        }


        with(handler(Request(GET, "/ticket/$id"))) {
            expectThat(status).isEqualTo(OK)
            expectThat(parseJsonNode(bodyString()).orThrow()).isEquivalentTo(
                """{
                    "id": "$id",
                    "title" : "title1",
                    "description": "description1",
                    "kanban_column" : "Blocked"
                }"""
            )
        }
    }

    @Test
    fun `start progress on ticket`() {
        // setup: create a ticket
        val id = handler.commandHandler(CommandAddToBacklog("title1", "description1")).first().entityKey

        with(
            handler(
                Request(POST, "/ticket/$id/start").body(
                    """{
                        "assignee" : "user96"
                    }"""
                )
            )
        ) {
            expectThat(status).isEqualTo(NO_CONTENT)
        }

        with(handler(Request(GET, "/ticket/$id"))) {
            expectThat(status).isEqualTo(OK)
            expectThat(parseJsonNode(bodyString()).orThrow()).isEquivalentTo(
                """{
                    "id": "$id",
                    "title" : "title1",
                    "description": "description1",
                    "kanban_column" : "InDevelopment",
                    "assignee": "user96"
                }"""
            )
        }
    }

    @Test
    fun `assign a different user to a ticket`() {
        // setup: create a ticket and start work on it
        val id = handler.commandHandler(CommandAddToBacklog("title1", "description1")).first().entityKey
        handler.commandHandler(CommandStartWork(id, UserId("user1")))

        with(
            handler(
                Request(POST, "/ticket/$id/assign").body(
                    """{
                        "assignee" : "user2"
                    }"""
                )
            )
        ) {
            expectThat(status).isEqualTo(NO_CONTENT)
        }

        with(handler(Request(GET, "/ticket/$id"))) {
            expectThat(status).isEqualTo(OK)
            expectThat(parseJsonNode(bodyString()).orThrow()).isEquivalentTo(
                """{
                    "id": "$id",
                    "title" : "title1",
                    "description": "description1",
                    "kanban_column" : "InDevelopment",
                    "assignee": "user2"
                }"""
            )
        }
    }


    @Test
    fun `put ticket onhold from inprogress`() {
        // setup: create a ticket and start work on it
        val id = handler.commandHandler(CommandAddToBacklog("title1", "description1")).first().entityKey
        handler.commandHandler(CommandStartWork(id, UserId("user1")))

        with(
            handler(
                Request(POST, "/ticket/$id/onhold")
            )
        ) {
            expectThat(status).isEqualTo(NO_CONTENT)
        }


        with(handler(Request(GET, "/ticket/$id"))) {
            expectThat(status).isEqualTo(OK)
            expectThat(parseJsonNode(bodyString()).orThrow()).isEquivalentTo(
                """{
                    "id": "$id",
                    "title" : "title1",
                    "description": "description1",
                    "kanban_column" : "Blocked",
                    "assignee" : "user1"
                }"""
            )
        }
    }

    @Test
    fun `complete a ticket`() {
        // setup: create a ticket and start work on it
        val id = handler.commandHandler(CommandAddToBacklog("title1", "description1")).first().entityKey
        handler.commandHandler(CommandStartWork(id, UserId("user1")))

        with(
            handler(
                Request(POST, "/ticket/$id/complete")
            )
        ) {
            expectThat(status).isEqualTo(NO_CONTENT)
        }


        with(handler(Request(GET, "/ticket/$id"))) {
            expectThat(status).isEqualTo(OK)
            expectThat(parseJsonNode(bodyString()).orThrow()).isEquivalentTo(
                """{
                    "id": "$id",
                    "title" : "title1",
                    "description": "description1",
                    "kanban_column" : "Done",
                    "assignee" : "user1"
                }"""
            )
        }
    }
}

private fun Assertion.Builder<JsonNode>.isEquivalentTo(jsonString: String): Assertion.Builder<JsonNode> =
    this.isEqualTo(parseJsonNode(jsonString).orThrow())