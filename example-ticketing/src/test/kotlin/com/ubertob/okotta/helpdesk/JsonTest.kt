package com.ubertob.okotta.helpdesk

import com.ubertob.kondortools.generateConverterFileFor
import com.ubertob.okotta.helpdesk.domain.*
import org.junit.jupiter.api.Test

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

        println(fileText)
    }

}