package com.ubertob.okotta.helpdesk.domain

import com.ubertob.okotta.helpdesk.lib.EventStore
import com.ubertob.okotta.helpdesk.lib.EventStoreInMemory

class TicketEventStore : EventStore<TicketEvent> by EventStoreInMemory() {
    //todo add fetchByTicketName
}

