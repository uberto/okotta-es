package com.ubertob.okotta.helpdesk.domain

import com.ubertob.okotta.helpdesk.lib.*

data class TicketsProjectionRow(
    val ticketId: String,
    val title: String,
    val description: String,
    val kanbanColumn: TicketStatus,
    val assignee: UserId? = null
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
                is Created -> CreateRow(
                    e.rowId(),
                    TicketsProjectionRow(e.entityKey, e.title, e.description, TicketStatus.Backlog, null)
                )
                is Started -> UpdateRow(e.rowId()) { r: TicketsProjectionRow ->
                    r.copy(kanbanColumn = TicketStatus.InDevelopment, assignee = e.userId)

                }
                is Blocked -> TODO()
                is Completed -> TODO()
                is Updated -> TODO()
            }.toSingle()
    }
}

private fun TicketEvent.rowId(): RowId = RowId(entityKey)

fun <T : Any> DeltaRow<T>.toSingle(): List<DeltaRow<T>> = listOf(this)