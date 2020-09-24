package com.ubertob.okotta.helpdesk.domain

data class TicketsProjectionRow(val ticketId: String, val active: Boolean)

class TicketsProjection
//(eventFetcher: FetchStoredEvents<ToDoListEvent>) :
//    InMemoryProjection<TicketsProjectionRow, ToDoListEvent> by ConcurrentInMemoryProjection(
//        eventFetcher,
//        ::eventProjector
//    )