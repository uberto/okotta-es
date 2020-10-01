package com.ubertob.okotta.helpdesk.web

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser

val klaxon = Klaxon()

interface JsonSerialisable

fun JsonSerialisable.serialise(): String =
    klaxon.toJsonString(this)

inline fun <reified T : JsonSerialisable> String.deserialise(): T? =
    klaxon.parse<T>(this)

val defaultParser = Parser.default()

// used by tests
fun String.asJsonObject(): JsonObject =
    defaultParser.parse(StringBuilder(this)) as JsonObject

fun String.asJsonArray(): JsonArray<*> =
    defaultParser.parse(StringBuilder(this)) as JsonArray<*>