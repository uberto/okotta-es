package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.CommandAddToBacklog
import com.ubertob.okotta.helpdesk.domain.TicketCommandHandler
import com.ubertob.okotta.helpdesk.domain.TicketsProjection
import org.http4k.core.*
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes


class HelpDesk(val ticketsProjection: TicketsProjection, val commandHandler: TicketCommandHandler): HttpHandler {

    val httpHandler = routes(
        "/ping" bind Method.GET to { Response(OK).body("pong") },
        "/ticket/{ticketId}" bind Method.GET to ::getTicket,
        "/ticket" bind Method.POST to ::addTicket
    )

    private fun addTicket(request: Request): Response {
        val add: AddTicketRequest = request.bodyString().deserialise()
        return commandHandler(CommandAddToBacklog(add.title, add.description))
            .first().entityKey
            .let { Response(OK).body(AddTicketResponse(it).serialise()) }
    }

    private fun getTicket(request: Request): Response {
        val ticketId = request.path("ticketId") ?: return Response(Status.BAD_REQUEST)
        val found = ticketsProjection.getTicket(ticketId) ?: return Response(Status.NOT_FOUND)
        return Response(OK).body(GetTicketResponse(found.title, found.description, found.kanbanColumn.name).serialise())
    }

    override fun invoke(req: Request): Response  =
        httpHandler(req)

}

// these types define the property names of the serialised json request/response objects
data class AddTicketRequest(val title: String, val description: String): JsonSerialisable
data class AddTicketResponse(val id: String): JsonSerialisable
data class GetTicketResponse(val title: String, val description: String, val kanban_column: String): JsonSerialisable