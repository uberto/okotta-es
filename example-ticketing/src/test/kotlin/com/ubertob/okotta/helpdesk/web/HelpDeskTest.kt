package com.ubertob.okotta.helpdesk.web

import com.ubertob.okotta.helpdesk.domain.TicketCommandHandler
import com.ubertob.okotta.helpdesk.domain.TicketQueryRunner
import org.junit.jupiter.api.Assertions.*

internal class HelpDeskTest {

    fun `test somethign`(){
        val commandHandler = TicketCommandHandler() //eventStore
        val queryHandler = TicketQueryRunner() //streamer::fetchAfter

        val handler = HelpDesk(queryHandler, commandHandler)
    }
}