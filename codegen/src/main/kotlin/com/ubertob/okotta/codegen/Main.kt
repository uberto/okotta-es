package com.ubertob.okotta.codegen

import com.paypal.digraph.parser.GraphEdge
import com.paypal.digraph.parser.GraphNode
import com.paypal.digraph.parser.GraphParser
import com.squareup.kotlinpoet.*
import java.io.File
import java.io.FileInputStream
import java.nio.file.Path


fun main(){

    //parse dot file


    val parser = GraphParser(FileInputStream("examples/door.dot"))
    val nodes: Map<String, GraphNode> = parser.getNodes()
    val edges: Map<String, GraphEdge> = parser.getEdges()

    log("--- nodes:")
    for (node in nodes.values) {
        log(node.getId().toString() + " " + node.getAttributes())
    }

    log("--- edges:")
    for (edge in edges.values) {
        log(edge.getNode1().getId().toString() + "->" + edge.getNode2().getId() + " " + edge.getAttributes())
    }



    //emit kotlin code somewhere


    val greeterClass = ClassName("", "Greeter")
    val file = FileSpec.builder("", "Door")
        .addType(
            TypeSpec.classBuilder("Greeter")
            .primaryConstructor(
                FunSpec.constructorBuilder()
                .addParameter("name", String::class)
                .build())
            .addProperty(
                PropertySpec.builder("name", String::class)
                .initializer("name")
                .build())
            .addFunction(FunSpec.builder("greet")
                .addStatement("println(%P)", "Hello, \$name")
                .build())
            .build())
        .addFunction(FunSpec.builder("main")
            .addParameter("args", String::class, KModifier.VARARG)
            .addStatement("%T(args[0]).greet()", greeterClass)
            .build())
        .build()

    file.writeTo(File("examples/door.kt"))

}

fun log(s: String) {
    println(s)
}
