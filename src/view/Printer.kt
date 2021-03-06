package view

import main.ScapeGoatEntry
import main.ScapeGoatTree

fun <K: Comparable<K>, V> treeToString(map: ScapeGoatTree<K, V>): String{
    return subTreeToString(map.root)
}

internal fun <K: Comparable<K>, V> subTreeToString(root: ScapeGoatEntry<K, V>?): String {
    var nodes = mutableListOf<ScapeGoatEntry<K, V>?>()
    var sb = StringBuilder()
    val totals = StringBuilder()
    if (root != null) {
        totals.append("(${root.key})\n")
        nodes.add(root)
        var currentList = mutableListOf<ScapeGoatEntry<K, V>?>()
        while (!nodes.isEmpty()) {
            var hasNotNull = false
            while (!nodes.isEmpty()) {
                val current = nodes[0]
                nodes.removeAt(0)
                if (current != null) {
                    if (current.left != null) {
                        sb.append("(" + current.left!!.key + ", ")
                        hasNotNull = true
                        currentList.add(current.left)
                    } else {
                        sb.append("(N, ")
                        currentList.add(null)
                    }
                    if (current.right != null) {
                        sb.append(current.right!!.key.toString() + ")")
                        hasNotNull = true
                        currentList.add(current.right)
                    } else {
                        sb.append("N)")
                        currentList.add(null)
                    }
                } else {
                    currentList.add(null)
                    currentList.add(null)
                    sb.append("((N), (N))")
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

fun <V> treeToPyramid(tree: ScapeGoatTree<Int, V>):MutableList<MutableList<Int?>>{
    var nodes = mutableListOf<ScapeGoatEntry<Int, V>?>()
    var currentRow = mutableListOf<Int?>()
    val totals = mutableListOf<MutableList<Int?>>()
    val root = tree.root
    if (root != null) {
        totals.add(mutableListOf(root.key))
        nodes.add(root)
        var currentList = mutableListOf<ScapeGoatEntry<Int, V>?>()
        while (!nodes.isEmpty()) {
            var hasNotNull = false
            while (!nodes.isEmpty()) {
                val current = nodes[0]
                nodes.removeAt(0)
                if (current != null) {
                    if (current.left != null) {
                        currentRow.add(current.left!!.key)
                        hasNotNull = true
                        currentList.add(current.left)
                    } else {
                        currentRow.add(null)
                        currentList.add(null)
                    }
                    if (current.right != null) {
                        currentRow.add(current.right!!.key)
                        hasNotNull = true
                        currentList.add(current.right)
                    } else {
                        currentRow.add(null)
                        currentList.add(null)
                    }
                } else {
                    currentList.add(null)
                    currentList.add(null)
                    currentRow.add(null)
                    currentRow.add(null)
                }
            }
            if (hasNotNull) {
                totals.add(currentRow)
                currentRow = mutableListOf()
                nodes = currentList
                currentList = mutableListOf()
            }
        }
    }
    return totals
}

fun toFormatString(int: Int): String{
    return String.format("%5d", int)
}