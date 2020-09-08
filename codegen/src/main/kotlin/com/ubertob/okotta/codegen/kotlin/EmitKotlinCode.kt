package com.ubertob.okotta.codegen.kotlin

import com.squareup.kotlinpoet.*
import com.ubertob.okotta.codegen.graph.Graph

fun emitKotlinCode(graph: Graph): FileSpec {

    val packageName = "com.example.events"
    val className = ClassName(packageName, graph.name.toClassName())
    val baseName = className.simpleName
    val stateBaseName = "${baseName}State"
    val file = FileSpec.builder(packageName, baseName)
        .addType(
            TypeSpec.classBuilder(stateBaseName)
                .addModifiers(KModifier.SEALED)
                .build()
        )

    val stateBase = ClassName(packageName, stateBaseName)


    graph.nodes.values.forEach { node ->
        file.addType(
            TypeSpec.classBuilder(node.id.toClassName())
                .addModifiers(KModifier.DATA)
                .addProperty("id", String::class)
                .addSuperinterface(stateBase)
                .build()
        ).build()
    }

    val baseEventName = "${baseName}Event"
    file.addType(
        TypeSpec.classBuilder(baseEventName)
            .addModifiers(KModifier.SEALED)
            .build()
    )

    val baseEvent = ClassName(packageName, baseEventName)

    graph.edges.values.forEach { edge ->
        val name = edge.getAttribute("label").toString()
        file.addType(
            TypeSpec.classBuilder(name.toClassName())
                .addModifiers(KModifier.DATA)
                .addProperty("id", String::class)
                .addSuperinterface(baseEvent)
                .build()
        )
            .build()
    }

    graph.edges.values.forEach { edge ->
        file.addFunction(
            FunSpec.builder(edge.getAttribute("label").toString() + "Command")
                .addParameter("state", stateBase)
                .returns(baseEvent)
                .addStatement("TODO()", className)
                .build()
        )
            .build()
    }

    return file.build()
}

private fun String.toClassName(): String =
    split(' ')
        .filter(String::isNotBlank)
        .map(String::capitalize)
        .joinToString(separator = "")
