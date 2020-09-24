package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.TicketCommandHandler
import com.ubertob.okotta.helpdesk.domain.TicketEventStore
import com.ubertob.okotta.helpdesk.domain.TicketsProjection
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    val helpDesk = helpDeskBuilder()

    helpDesk.asServer(Jetty(8080)).start()
}

fun helpDeskBuilder(): HelpDesk {
    val eventStore = TicketEventStore()

    val commandHandler = TicketCommandHandler(eventStore)
    val ticketProjection = TicketsProjection(eventStore::fetchAfter)

    val helpDesk = HelpDesk(ticketProjection, commandHandler)
    return helpDesk
}