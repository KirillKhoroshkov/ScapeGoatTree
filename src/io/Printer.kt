package io

import main.ScapeGoatEntry
import main.ScapeGoatTree

fun <K: Comparable<K>, V> treeToString(map: ScapeGoatTree<K, V>): String{
    return subTreeToString(map.root)
}

fun <K: Comparable<K>, V> subTreeToString(root: ScapeGoatEntry<K, V>?): String {
    var nodes = mutableListOf<ScapeGoatEntry<K, V>?>()
    var sb = StringBuilder()
    val totals = StringBuilder()
    if (root != null) {
        totals.append("($root)\n")
        nodes.add(root)
        var currentList = mutableListOf<ScapeGoatEntry<K, V>?>()
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
                        sb.append("((N=N), ")
                        currentList.add(null)
                    }
                    if (current.right != null) {
                        sb.append(current.right.toString() + ")")
                        hasNotNull = true
                        currentList.add(current.right)
                    } else {
                        sb.append("(N=N))")
                        currentList.add(null)
                    }
                } else {
                    currentList.add(null)
                    currentList.add(null)
                    sb.append("((N=N), (N=N))")
                }
            }
            if (hasNotNull) {
                totals.append(sb.toString() + "\n")
                sb = StringBuilder()
                nodes = currentList
                currentList = mutableListOf()
            }
        }
        return totals.toString()
    } else {
        return "Map is empty"
    }
}