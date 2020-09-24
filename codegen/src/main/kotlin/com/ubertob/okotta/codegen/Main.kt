package com.ubertob.okotta.codegen

import com.ubertob.okotta.codegen.graph.parseDotFile
import com.ubertob.okotta.codegen.kotlin.emitKotlinCode
import java.io.File


fun main(){

    val g = parseDotFile("codegen/examples/helpdesk.dot")

    val f = emitKotlinCode(g)

    f.writeTo(File("codegen/generated"))

}

