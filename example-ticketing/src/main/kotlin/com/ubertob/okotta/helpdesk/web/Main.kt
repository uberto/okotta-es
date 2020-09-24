package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.TicketCommandHandler
import com.ubertob.okotta.helpdesk.domain.TicketQueryRunner
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
//    val streamer = ToDoListEventStreamerInMemory()
//    val eventStore = ToDoListEventStore(streamer)
//
    val commandHandler = TicketCommandHandler() //eventStore
    val queryHandler = TicketQueryRunner() //streamer::fetchAfter

    HelpDesk(queryHandler, commandHandler).asServer(Jetty(8080)).start()
}