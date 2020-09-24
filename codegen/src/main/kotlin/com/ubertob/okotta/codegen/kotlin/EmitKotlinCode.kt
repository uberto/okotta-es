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


    graph.nodes.values
        .map { it.id }
        .toSet()
        .forEach { nodeId ->
            file.addType(
                TypeSpec.classBuilder(nodeId.toClassName())
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("id", String::class)
                            .build()
                    )
                    .addModifiers(KModifier.DATA)
                    .addProperty(
                        PropertySpec.builder("id", String::class)
                            .initializer("id")
                            .build()
                    )
                    .superclass(stateBase)
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

    val edges = graph.edges.values
        .map { it.getAttribute("label").toString() }
        .toSet()

    edges
        .forEach { name ->
            file.addType(
                TypeSpec.classBuilder(name.toClassName())
                    .primaryConstructor(
                        FunSpec.constructorBuilder()
                            .addParameter("id", String::class).build()
                    )
                    .addProperty(
                        PropertySpec.builder("id", String::class)
                            .initializer("id")
                            .build()
                    )
                    .addModifiers(KModifier.DATA)
                    .superclass(baseEvent)
                    .build()
            )
                .build()
        }

    edges.forEach { edge ->
        file.addFunction(
            FunSpec.builder(edge + "Command")
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
