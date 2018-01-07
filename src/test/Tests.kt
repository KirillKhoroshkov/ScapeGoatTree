package test

import main.ScapeGoatTree
import org.testng.annotations.Test
import io.subTreeToString
import io.treeToString
import main.ScapeGoatEntry
import java.util.*

class Tests {

    @Test
    fun findPath(){
        println("**********FIND_PATH_TEST**********")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        map.put(1, 1)
        println("Path to absent node:")
        println(map.findPath(2))
    }

    @Test
    fun put(){
        println("**********PUT_TEST**********")
        val map = ScapeGoatTree<Int, Int>(0.5)
        map.put(1, 1)
        println(treeToString(map))
        println("----------------------------------------------------------------")
        map.put(1, 1)
        println(treeToString(map))
        println("----------------------------------------------------------------")
        map.put(2, 2)
        println(treeToString(map))
        println("----------------------------------------------------------------")
        map.put(5, 5)
        println(treeToString(map))
        println("----------------------------------------------------------------")
        map.put(3, 3)
        println(treeToString(map))
        println("----------------------------------------------------------------")
        map.put(4, 4)
        println(treeToString(map))
        println("----------------------------------------------------------------")
        map.put(6, 6)
        println(treeToString(map))
        println("----------------------------------------------------------------")
    }

    @Test
    fun rebuild(){
        println("**********REBUILD_TEST**********")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        var node = ScapeGoatEntry(1, 1)
        node.right = ScapeGoatEntry(2, 2)
        node.right!!.right = ScapeGoatEntry(3, 3)
        node.right!!.right!!.right = ScapeGoatEntry(4, 4)
        node.right!!.right!!.right!!.right = ScapeGoatEntry(5, 5)
        println("Before rebuild:")
        println(subTreeToString(node))
        node = map.rebuild(node)
        println("After rebuild:")
        println(subTreeToString(node))
    }

    @Test
    fun bisect(){
        println("**********BISECT_TEST**********")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        val sortedNodesList = mutableListOf(
                ScapeGoatEntry(1, 1),
                ScapeGoatEntry(2, 2),
                ScapeGoatEntry(3, 3),
                ScapeGoatEntry(4, 4),
                ScapeGoatEntry(5, 5)
        )
        println("sortedNodesList: " + sortedNodesList)
        val node = map.bisect(sortedNodesList)
        println("After bisect:")
        println(subTreeToString(node))
    }

    @Test
    fun sizeOf(){
        println("**********SIZE_OF_TEST**********")
        val map = ScapeGoatTree<Int, Int>(0.99999)
        val node = ScapeGoatEntry(1, 1)
        node.right = ScapeGoatEntry(2, 2)
        node.right!!.right = ScapeGoatEntry(3, 3)
        node.right!!.right!!.right = ScapeGoatEntry(4, 4)
        node.right!!.right!!.right!!.right = ScapeGoatEntry(5, 5)
        println("Tree for counting:")
        println(subTreeToString(node))
        println("Count:")
        println(map.sizeOf(node))
    }

    @Test
    fun findScapeGoat(){
        println("**********FIND_SCAPE_GOAT_TEST**********")
        val node = ScapeGoatEntry(1, 1)
        node.right = ScapeGoatEntry(2, 2)
        node.right!!.right = ScapeGoatEntry(3, 3)
        node.right!!.right!!.right = ScapeGoatEntry(4, 4)
        node.right!!.right!!.right!!.right = ScapeGoatEntry(5, 5)
        val path1 = ArrayDeque<ScapeGoatEntry<Int, Int>>()
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
        println("**********TO_STRING_TEST**********")
        val map = ScapeGoatTree<Int, Int>(0.6)
        map.put(1, 1)
        map.put(1, 1)
        map.put(2, 2)
        map.put(5, 5)
        map.put(3, 3)
        map.put(4, 4)
        map.put(6, 6)
        println(treeToString(map))
        println("______________________________________")
        println(map)
    }

    @Test
    fun setBalanceFactor(){
        println("**********SET_BALANCE_FACTOR**********")
        val map = ScapeGoatTree<Int, Int>(0.9)
        map.put(1, 1)
        map.put(1, 1)
        map.put(2, 2)
        map.put(5, 5)
        map.put(3, 3)
        map.put(4, 4)
        map.put(6, 6)
        println("0.9:")
        println(treeToString(map))
        println("0.99:")
        map.balanceFactor = 0.99
        println(treeToString(map))
        println("0.5:")
        map.balanceFactor = 0.5
        println(treeToString(map))
    }

    @Test
    fun getKeysValuesEntries(){
        println("**********GET_KEYS_VALUES_ENTRIES**********")//Нужное подчеркнуть
        val map = ScapeGoatTree<Int, Int>(0.6)
        map.put(1, 1)
        map.put(1, 1)
        map.put(2, 2)
        map.put(5, 5)
        map.put(3, 3)
        map.put(4, 4)
        map.put(6, 6)
        println(treeToString(map))
        println("Keys:")
        println(map.keys)
        println("Values")
        println(map.values)
        println("Entries")
        println(map.entries)
    }

    @Test
    fun remove(){
        println("**********REMOVE**********")
        val map = ScapeGoatTree<Int, Int>(0.6)
        map.put(1, 1)
        map.put(2, 2)
        map.put(5, 5)
        map.put(3, 3)
        map.put(4, 4)
        map.put(7, 7)
        map.put(6, 6)
        println(treeToString(map))
        println("_______________________________________")
        println("Remove(5):")
        map.remove(5)
        println(treeToString(map))
        println("_______________________________________")
        println("Remove(3):")
        map.remove(3)
        println(treeToString(map))
        println("_______________________________________")
        println("Remove(1):")
        map.remove(1)
        println(treeToString(map))
        println("_______________________________________")
        map.remove(7)
        map.remove(6)
        map.remove(2)
        map.remove(4)
        println(treeToString(map))
    }
}