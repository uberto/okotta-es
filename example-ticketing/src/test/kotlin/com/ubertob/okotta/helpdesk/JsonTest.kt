package com.ubertob.okotta.helpdesk

import com.ubertob.kondortools.generateConverterFileFor
import com.ubertob.okotta.helpdesk.domain.*
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotBlank

class JsonTest {


    @Test
    fun generateJsonConverters() {
        val fileText = generateConverterFileFor(
            Created::class,
            Started::class,
            Assigned::class,
            Blocked::class,
            Completed::class,
            Updated::class,
        )

        expectThat(fileText).isNotBlank()

        println(fileText)
    }

}