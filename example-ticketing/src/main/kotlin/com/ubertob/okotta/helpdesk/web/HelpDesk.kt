package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.*
import com.ubertob.okotta.helpdesk.lib.EventSeq
import org.http4k.core.*
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.routing.static


class HelpDesk(val ticketsProjection: TicketsProjection, val commandHandler: TicketCommandHandler): HttpHandler {

    val httpHandler = routes(
        "/ping" bind Method.GET to { Response(OK).body("pong") },
        "/ui" bind static(Classpath("/com/ubertob/okotta/helpdesk/web")),
        "/debug-events" bind Method.GET to ::debugEvents,
        "/ticket" bind Method.POST to ::addTicket,
        "/tickets" bind Method.GET to ::allTickets,
        "/ticket/{ticketId}" bind Method.GET to ::getTicket,
        "/ticket/{ticketId}/assign" bind Method.POST to ::assignTicket,
        "/ticket/{ticketId}/start" bind Method.POST to ::startTicket,
        "/ticket/{ticketId}/onhold" bind Method.POST to ::putTicketOnHold,
        "/ticket/{ticketId}/complete" bind Method.POST to ::completeTicket,
    )

    private fun debugEvents(@Suppress("UNUSED_PARAMETER") request: Request): Response {
        val storedEvents = commandHandler.eventStore.fetchAfter(EventSeq(-1))
            .toList()
            .map { storedEvent ->
                DebugEvent(
                    eventId = storedEvent.eventSeq.progressive,
                    event = storedEvent.event,
                    eventName = storedEvent.event.javaClass.simpleName
                )
            }
        return Response(OK).contentTypeJson().body(DebugEventsResponse(storedEvents).serialise())
    }

    private fun addTicket(request: Request): Response {
        val add: AddTicketRequest = request.bodyString().deserialise() ?: return Response(Status.BAD_REQUEST)
        return commandHandler(CommandAddToBacklog(add.title, add.description))
            .first().entityKey
            .let { Response(OK).contentTypeJson().body(AddTicketResponse(it).serialise()) }
    }

    private fun getTicket(request: Request): Response {
        val ticketId = request.path("ticketId") ?: return Response(Status.BAD_REQUEST)
        val found = ticketsProjection.getTicket(ticketId) ?: return Response(Status.NOT_FOUND)
        return Response(OK).contentTypeJson().body(
            GetTicketResponse(
                id = ticketId,
                title = found.title,
                description = found.description,
                kanban_column = found.kanbanColumn.name,
                assignee = found.assignee?.name
            ).serialise()
        )
    }

    private fun assignTicket(request: Request): Response {
        val ticketId = request.path("ticketId") ?: return Response(Status.BAD_REQUEST)
        val assign: AssignTicketRequest = request.bodyString().deserialise() ?: return Response(Status.BAD_REQUEST)

        commandHandler(CommandAssignToUser(ticketId, assign.assignee.asUserId()))
        return Response(NO_CONTENT)
    }

    private fun putTicketOnHold(request: Request): Response {
        val ticketId = request.path("ticketId") ?: return Response(Status.BAD_REQUEST)

        commandHandler(CommandPutOnHold(ticketId))
        return Response(NO_CONTENT)
    }

    private fun allTickets(@Suppress("UNUSED_PARAMETER") request: Request): Response {
        val all = ticketsProjection.getTickets().map {
            GetTicketResponse(
                id = it.key.id,
                title = it.value.title,
                description = it.value.description,
                kanban_column = it.value.kanbanColumn.name,
                assignee = it.value.assignee?.name
            )
        }
        return Response(OK).contentTypeJson().body(AllTicketsResponse(all).serialise())
    }

    private fun startTicket(request: Request): Response {
        val ticketId = request.path("ticketId") ?: return Response(Status.BAD_REQUEST)
        val startTicket: StartTicketRequest = request.bodyString().deserialise() ?: return Response(Status.BAD_REQUEST)
        commandHandler(CommandStartWork(ticketId, startTicket.assignee.asUserId()))
        return Response(NO_CONTENT)
    }

    private fun completeTicket(request: Request): Response {
        val ticketId = request.path("ticketId") ?: return Response(Status.BAD_REQUEST)
        commandHandler(CommandEndWork(ticketId))
        return Response(NO_CONTENT)
    }

    override fun invoke(req: Request): Response =
        httpHandler(req)

}

private fun String.asUserId(): UserId = UserId(this)

private fun Response.contentTypeJson() = header("Content-type", "application/json")

// these types define the property names of the serialised json request/response objects
data class AssignTicketRequest(val assignee: String) : JsonSerialisable
data class AddTicketRequest(val title: String, val description: String) : JsonSerialisable
data class AddTicketResponse(val id: String) : JsonSerialisable
data class StartTicketRequest(val assignee: String) : JsonSerialisable
data class GetTicketResponse(
    val id: String,
    val title: String,
    val description: String,
    val kanban_column: String,
    val assignee: String?
) : JsonSerialisable

class AllTicketsResponse(array: List<GetTicketResponse>) : ArrayList<GetTicketResponse>(array), JsonSerialisable

class DebugEventsResponse(array: List<DebugEvent>): ArrayList<DebugEvent>(array), JsonSerialisable
data class DebugEvent(val eventId: Int, val eventName: String, val event: TicketEvent)