package test

import main.ScapeGoatTree
import org.testng.annotations.Test
import io.printSubTree
import io.printTree
import java.util.*

class Tests {

    @Test
    fun findPath(){
        println("FIND_PATH_TEST")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        map.put(1, 1)
        println("Path to absent node:")
        println(map.findPath(2))
    }

    @Test
    fun put(){
        println("PUT_TEST")
        val map = ScapeGoatTree<Int, Int>(0.5)
        map.put(1, 1)
        printTree(map)
        println("----------------------------------------------------------------")
        map.put(1, 1)
        printTree(map)
        println("----------------------------------------------------------------")
        map.put(2, 2)
        printTree(map)
        println("----------------------------------------------------------------")
        map.put(5, 5)
        printTree(map)
        println("----------------------------------------------------------------")
        map.put(3, 3)
        printTree(map)
        println("----------------------------------------------------------------")
        map.put(4, 4)
        printTree(map)
        println("----------------------------------------------------------------")
        map.put(6, 6)
        printTree(map)
        println("----------------------------------------------------------------")
    }

    @Test
    fun rebuild(){
        println("REBUILD_TEST")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        var node = ScapeGoatTree.Node(1, 1)
        node.right = ScapeGoatTree.Node(2, 2)
        node.right!!.right = ScapeGoatTree.Node(3, 3)
        node.right!!.right!!.right = ScapeGoatTree.Node(4, 4)
        node.right!!.right!!.right!!.right = ScapeGoatTree.Node(5, 5)
        println("Before rebuild:")
        printSubTree(node)
        node = map.rebuild(node)
        println("After rebuild:")
        printSubTree(node)
    }

    @Test
    fun bisect(){
        println("BISECT_TEST")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        val sortedNodesList = mutableListOf(
                ScapeGoatTree.Node(1, 1),
                ScapeGoatTree.Node(2, 2),
                ScapeGoatTree.Node(3, 3),
                ScapeGoatTree.Node(4, 4),
                ScapeGoatTree.Node(5, 5)
        )
        println("sortedNodesList: " + sortedNodesList)
        val node = map.bisect(sortedNodesList)
        println("After bisect:")
        printSubTree(node)
    }

    @Test
    fun sizeOf(){
        println("SIZE_OF_TEST")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        val node = ScapeGoatTree.Node(1, 1)
        node.right = ScapeGoatTree.Node(2, 2)
        node.right!!.right = ScapeGoatTree.Node(3, 3)
        node.right!!.right!!.right = ScapeGoatTree.Node(4, 4)
        node.right!!.right!!.right!!.right = ScapeGoatTree.Node(5, 5)
        println("Tree for counting:")
        printSubTree(node)
        println("Count:")
        println(map.sizeOf(node))
    }

    @Test
    fun findScapeGoat(){
        println("FIND_SCAPE_GOAT_TEST")
        val node = ScapeGoatTree.Node(1, 1)
        node.right = ScapeGoatTree.Node(2, 2)
        node.right!!.right = ScapeGoatTree.Node(3, 3)
        node.right!!.right!!.right = ScapeGoatTree.Node(4, 4)
        node.right!!.right!!.right!!.right = ScapeGoatTree.Node(5, 5)
        val path1 = ArrayDeque<ScapeGoatTree.Node<Int, Int>>()
        path1.addLast(node)
        path1.addLast(node.right)
        path1.addLast(node.right!!.right)
        path1.addLast(node.right!!.right!!.right)
        path1.addLast(node.right!!.right!!.right!!.right)
        val path2 = path1.clone()
        val path3 = path1.clone()
        println("0.99999:")
        val map1 = ScapeGoatTree<Int, Int>(0.99999)
        println(map1.findScapegoat(path1))
        println("0.5:")
        val map2 = ScapeGoatTree<Int, Int>(0.5)
        println(map2.findScapegoat(path2))
        println("0.7:")
        val map3 = ScapeGoatTree<Int, Int>(0.6)
        println(map3.findScapegoat(path3))
    }

    @Test
    fun string(){
        println("TO_STRING_TEST")
        val map = ScapeGoatTree<Int, Int>(0.6)
        map.put(1, 1)
        map.put(1, 1)
        map.put(2, 2)
        map.put(5, 5)
        map.put(3, 3)
        map.put(4, 4)
        map.put(6, 6)
        printTree(map)
        println("______________________________________")
        println(map)
    }
}