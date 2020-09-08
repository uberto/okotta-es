package com.ubertob.okotta.codegen.graph

import com.paypal.digraph.parser.GraphEdge
import com.paypal.digraph.parser.GraphNode
import com.paypal.digraph.parser.GraphParser
import java.io.FileInputStream

data class Graph(val name: String,
                 val nodes: Map<String, GraphNode>,
                 val edges: Map<String, GraphEdge>)



fun parseDotFile(fileName: String): Graph {
    val parser = GraphParser(FileInputStream(fileName))

    return Graph(parser.graphId, parser.nodes, parser.edges)
}