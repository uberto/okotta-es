package com.ubertob.okotta.helpdesk.web

import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull


internal class HelpDeskTest {

    @Test
    fun `create a new ticket and retrieve it`() {
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
}


