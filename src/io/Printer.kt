package io

import main.ScapeGoatTree

fun <K: Comparable<K>, V> printTree(map: ScapeGoatTree<K, V>){
    printSubTree(map.root)
}

internal fun <K: Comparable<K>, V> printSubTree(root: ScapeGoatTree.Node<K, V>?) {
    var nodes = mutableListOf<ScapeGoatTree.Node<K, V>?>()
    var sb = StringBuilder()
    if (root != null) {
        println("($root)")
        nodes.add(root)
        var currentList = mutableListOf<ScapeGoatTree.Node<K, V>?>()
        while (!nodes.isEmpty()) {
            var hasNotNull = false
            while (!nodes.isEmpty()) {
                val current = nodes[0]
                nodes.removeAt(0)
                if (current != null) {
                    if (current.left != null) {
                        sb.append("(" + current.left + ", ")
                        hasNotNull = true
                        currentList.add(current.left)
                    } else {
                        sb.append("(N=N, ")
                        currentList.add(null)
                    }
                    if (current.right != null) {
                        sb.append(current.right.toString() + ")")
                        hasNotNull = true
                        currentList.add(current.right)
                    } else {
                        sb.append("N=N)")
                        currentList.add(null)
                    }
                } else {
                    currentList.add(null)
                    currentList.add(null)
                    sb.append("(N=N, N=N)")
                }
            }
            if (hasNotNull) {
                println(sb)
                sb = StringBuilder()
                nodes = currentList
                currentList = mutableListOf()
            }
        }
    } else {
        println("It is empty")
    }
}