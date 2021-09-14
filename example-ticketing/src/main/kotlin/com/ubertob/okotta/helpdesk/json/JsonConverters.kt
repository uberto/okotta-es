package com.ubertob.okotta.helpdesk.json

import com.ubertob.kondor.json.*
import com.ubertob.kondor.json.datetime.JInstant
import com.ubertob.kondor.json.datetime.str
import com.ubertob.kondor.json.jsonnode.JsonNodeObject
import com.ubertob.okotta.helpdesk.domain.*
import com.ubertob.okotta.helpdesk.web.*


object JAssignTicketRequest : JAny<AssignTicketRequest>() {
    private val assignee by str(AssignTicketRequest::assignee)

    override fun JsonNodeObject.deserializeOrThrow(): AssignTicketRequest =
        AssignTicketRequest(
            assignee = +assignee
        )
}

object JAddTicketRequest : JAny<AddTicketRequest>() {
    private val description by str(AddTicketRequest::description)

    private val title by str(AddTicketRequest::title)

    override fun JsonNodeObject.deserializeOrThrow(): AddTicketRequest =
        AddTicketRequest(
            description = +description,
            title = +title
        )
}

object JAddTicketResponse : JAny<AddTicketResponse>() {
    private val id by str(AddTicketResponse::id)

    override fun JsonNodeObject.deserializeOrThrow(): AddTicketResponse =
        AddTicketResponse(
            id = +id
        )
}

object JStartTicketRequest : JAny<StartTicketRequest>() {
    private val assignee by str(StartTicketRequest::assignee)

    override fun JsonNodeObject.deserializeOrThrow(): StartTicketRequest =
        StartTicketRequest(
            assignee = +assignee
        )
}

object JGetTicketResponse : JAny<GetTicketResponse>() {
    private val assignee by str(GetTicketResponse::assignee)

    private val description by str(GetTicketResponse::description)

    private val id by str(GetTicketResponse::id)

    private val kanban_column by str(GetTicketResponse::kanban_column)

    private val title by str(GetTicketResponse::title)

    override fun JsonNodeObject.deserializeOrThrow(): GetTicketResponse =
        GetTicketResponse(
            assignee = +assignee,
            description = +description,
            id = +id,
            kanban_column = +kanban_column,
            title = +title
        )
}

val JAllTicketsResponse = JList(JGetTicketResponse)

val JDebugEventsResponse = JList(JDebugEvent)

object JDebugEvent : JAny<DebugEvent>() {
    private val event by obj(JTicketEvent, DebugEvent::event)

    private val eventId by num(DebugEvent::eventId)

    private val eventName by str(DebugEvent::eventName)

    override fun JsonNodeObject.deserializeOrThrow(): DebugEvent =
        DebugEvent(
            event = +event,
            eventId = +eventId,
            eventName = +eventName
        )
}

// in case of persistence

object JTicketEvent : JSealed<TicketEvent>() {
    override val discriminatorFieldName = "type"

    override val subConverters = mapOf(
        "Assigned" to JAssigned,
        "Blocked" to JBlocked,
        "Completed" to JCompleted,
        "Created" to JCreated,
        "Started" to JStarted,
        "Updated" to JUpdated
    )

    override fun extractTypeName(event: TicketEvent): String =
        when (event) {
            is Assigned -> "Assigned"
            is Blocked -> "Blocked"
            is Completed -> "Completed"
            is Created -> "Created"
            is Started -> "Started"
            is Updated -> "Updated"
        }

}


object JCreated : JAny<Created>() {
    private val description by str(Created::description)

    private val entityKey by str(Created::entityKey)

    private val title by str(Created::title)

    override fun JsonNodeObject.deserializeOrThrow(): Created =
        Created(
            description = +description,
            entityKey = +entityKey,
            title = +title
        )
}

object JUserId : JStringRepresentable<UserId>() {
    override val cons = ::UserId
    override val render = UserId::name
}

object JStarted : JAny<Started>() {
    private val entityKey by str(Started::entityKey)

    private val time by str(Started::time)

    private val userId by obj(JUserId, Started::userId)

    override fun JsonNodeObject.deserializeOrThrow(): Started =
        Started(
            entityKey = +entityKey,
            time = +time,
            userId = +userId
        )
}

object JAssigned : JAny<Assigned>() {
    private val entityKey by str(Assigned::entityKey)

    private val newUserId by obj(JUserId, Assigned::newUserId)

    override fun JsonNodeObject.deserializeOrThrow(): Assigned =
        Assigned(
            entityKey = +entityKey,
            newUserId = +newUserId
        )
}

object JBlocked : JAny<Blocked>() {
    private val entityKey by str(Blocked::entityKey)

    override fun JsonNodeObject.deserializeOrThrow(): Blocked =
        Blocked(
            entityKey = +entityKey
        )
}

object JCompleted : JAny<Completed>() {
    private val entityKey by str(Completed::entityKey)

    private val time by obj(JInstant, Completed::time)

    override fun JsonNodeObject.deserializeOrThrow(): Completed =
        Completed(
            entityKey = +entityKey,
            time = +time
        )
}

object JUpdated : JAny<Updated>() {
    private val entityKey by str(Updated::entityKey)

    override fun JsonNodeObject.deserializeOrThrow(): Updated =
        Updated(
            entityKey = +entityKey
        )
}



