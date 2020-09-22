package com.ubertob.okotta.core

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SafeEventStateTest {

    @Test
    fun `open close and lock`(){

        val safe = SafeId.mint()
        val events = listOf(
                Opened(safe),
                Closed(safe),
                Opened(safe),
                Closed(safe),
                Locked(safe, "abc")
                )

        val state = events.fold()

        expectThat(state).isEqualTo(AlarmActive(safe, "abc"))

    }

    @Test
    fun `lock and unlock`(){

        val safe = SafeId.mint()
        val events = listOf(
                Opened(safe),
                Closed(safe),
                Locked(safe, "abc"),
                Unlocked(safe),
                Opened(safe)
        )

        val state = events.fold()

        expectThat(state).isEqualTo(Open(safe))

    }


}