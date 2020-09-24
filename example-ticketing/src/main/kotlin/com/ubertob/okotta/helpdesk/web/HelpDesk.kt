package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.TicketCommandHandler
import com.ubertob.okotta.helpdesk.domain.TicketsProjection
import org.http4k.core.*
import org.http4k.routing.bind
import org.http4k.routing.routes


class HelpDesk(val ticketsProjection: TicketsProjection, val commandHandler: TicketCommandHandler): HttpHandler {

    val httpHandler = routes(
        "/ping" bind Method.GET to { Response(Status.OK).body("pong") },
        "/ticket/{ticketId}" bind Method.GET to ::getTicket,
        "/ticket" bind Method.POST to ::addTicket
    )

    private fun addTicket(request: Request): Response {
        return TODO("not implemented")
    }

    private fun getTicket(request: Request): Response {
        return TODO("not implemented")
    }

    override fun invoke(req: Request): Response  =
        httpHandler(req)

}
