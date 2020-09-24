package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.TicketCommandHandler
import com.ubertob.okotta.helpdesk.domain.TicketQueryRunner
import org.http4k.client.OkHttp


import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

internal class MainTest {

    @Test
    fun `server can start`(){
        val commandHandler = TicketCommandHandler() //eventStore
        val queryHandler = TicketQueryRunner() //streamer::fetchAfter

        val server = HelpDesk(queryHandler, commandHandler).asServer(Jetty(8081))

        server.start()

        val client = OkHttp()
        val resp = client(Request(Method.GET, "http://localhost:8081/ping"))

        expectThat(resp.status).isEqualTo(Status.OK)
        server.stop()

    }
}