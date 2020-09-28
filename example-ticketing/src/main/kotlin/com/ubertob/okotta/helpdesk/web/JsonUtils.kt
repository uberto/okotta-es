package com.ubertob.okotta.helpdesk.web

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.core.Response

val mapper = jacksonObjectMapper()

interface JsonSerialisable

fun JsonSerialisable.serialise() : String =
    mapper.writeValueAsString(this)

inline fun <reified T: JsonSerialisable> String.deserialise(): T =
    mapper.readValue(this)

// used by tests
fun Response.bodyAsJson(): JsonNode =
    mapper.readTree(bodyString())