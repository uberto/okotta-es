package com.ubertob.okotta.helpdesk.domain

import com.example.events.*
import com.ubertob.okotta.helpdesk.lib.*

data class TicketsProjectionRow(
    val ticketId: String,
    val title: String,
    val description: String,
    val kanbanColumn: TicketStatus
)

enum class TicketStatus {
    Backlog, InDevelopment, Blocked, Done
}

class TicketsProjection(eventFetcher: FetchStoredEvents<TicketEvent>) :
    InMemoryProjection<TicketsProjectionRow, TicketEvent> by ConcurrentInMemoryProjection(
        eventFetcher,
        ::eventProjector
    ) {

    fun `get single ticket`(ticketId: String): TicketsProjectionRow? =
        allRows().firstOrNull { it.ticketId == ticketId }


    companion object {
        fun eventProjector(e: TicketEvent): List<DeltaRow<TicketsProjectionRow>> =
            when (e) {
//            is ListCreated -> CreateRow(e.rowId(), ListProjectionRow(e.owner, true, ToDoList(e.name, emptyList())))
//            is ItemAdded -> UpdateRow(e.rowId()) { r: ListProjectionRow -> r.copy(list = r.list.copy(items = r.list.items + e.item)) }
//            is ItemRemoved -> UpdateRow(e.rowId()) { it.copy(list = it.list.copy(items = it.list.items - e.item)) }
//            is ItemModified -> UpdateRow(e.rowId()) { it.copy(list = it.list.copy(items = it.list.items - e.prevItem + e.item)) }
//            is ListPutOnHold -> UpdateRow(e.rowId()) { it.copy(active = false) }
//            is ListReleased -> UpdateRow(e.rowId()) { it.copy(active = true) }
//            is ListClosed -> DeleteRow(e.rowId())
                is Started -> CreateRow(
                    e.rowId(),
                    TicketsProjectionRow(e.entityKey, e.title, e.description, TicketStatus.Backlog)
                )
                is Blocked -> TODO()
                is Completed -> TODO()
                is Updated -> TODO()
                is Created -> TODO()
            }.toSingle()
    }
}

private fun TicketEvent.rowId(): RowId = RowId(entityKey)

fun <T : Any> DeltaRow<T>.toSingle(): List<DeltaRow<T>> = listOf(this)