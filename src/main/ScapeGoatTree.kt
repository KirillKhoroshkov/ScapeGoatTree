package main

import java.io.Serializable
import java.util.*
import kotlin.NoSuchElementException

class ScapeGoatTree<K: Comparable<K>, V>(balanceFactor: Double): SortedMap<K, V>, Iterable<Map.Entry<K, V>>, Cloneable, Serializable {
    internal var root: Node<K, V>? = null

    private var A: Double
    init {
        if (balanceFactor >= 1 || balanceFactor < 0.5) {
            throw IllegalArgumentException()
        } else {
            A = balanceFactor
        }
    }

    /**
     * Returns the number of key/value pairs in the map.
     */
    override var size: Int = 0
        get() = size

    private var maxSize = 0

    /**
     * Returns a [Set] view of the mappings contained in this map.
     * The set's iterator returns the entries in ascending key order.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own `remove` operation, or through the
     * `setValue` operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the `Iterator.remove`,
     * `Set.remove`, `removeAll`, `retainAll` and
     * `clear` operations.  It does not support the
     * `add` or `addAll` operations.
     *
     * @return a set view of the mappings contained in this map,
     * sorted in ascending key order
     */
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override val keys = mutableSetOf<K>()

    override val values = mutableListOf<V>()

    inner class ScapeGoatTreeIterator : Iterator<Map.Entry<K, V>> {
        private val innerRoots = ArrayDeque<Node<K, V>>()
        private var next = root
        private var counter = size
        private var returnedBack = false

        private fun findNext(): Node<K, V>? {
            val current = next
            if (!returnedBack && next!!.left != null) {
                innerRoots.add(next)
                next = next!!.left
                return findNext()
            } else if (next!!.right != null) {
                next = next!!.right
                returnedBack = false
            } else {
                next = innerRoots.pollLast()
                returnedBack = true
            }
            counter--
            return current
        }

        /**
         * Returns `true` if the iteration has more elements.
         */
        override fun hasNext(): Boolean {
            return counter != 0
        }

        /**
         * Returns the next element in the iteration.
         */
        override fun next(): Map.Entry<K, V> {
            val it = findNext() ?: throw NoSuchElementException()
            return ScapeGoatEntry(it.key, it.value)
        }

    }

    inner class ScapeGoatEntry<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {
        /**
         * Changes the value associated with the key of this entry.
         *
         * @return the previous value corresponding to the key.
         */
        override fun setValue(newValue: V): V {
            val oldValue = value
            value = newValue
            return oldValue
        }

        override fun toString(): String {
            return "($key=$value)"
        }

    }
    internal class Node<T, U>(val key: T, var value: U) {
        var left: Node<T, U>? = null

        var right: Node<T, U>? = null
        override fun toString(): String {
            return "($key=$value)"
        }

    }

    fun getBalanceFactor(): Double{
        return A
    }

    fun setBalanceFactor(newBalanceFactor: Double): Double{
        val oldBalanceFactor = A
        if (newBalanceFactor >= 1 || newBalanceFactor < 0.5) {
            throw IllegalArgumentException()
        } else {
            A = newBalanceFactor
            if (oldBalanceFactor < A && root != null){
                rebuild(root!!)
            }
            return oldBalanceFactor
        }
    }

    private fun find(key: K, from: Node<K, V>?): Node<K, V>? {
        if (from == null || from.key == key) {
            return from
        } else if (from.key > key) {
            return find(key, from.left)
        } else {
            return find(key, from.right)
        }
    }

    internal fun findPath(key: K): Deque<Node<K, V>> {
        val deque = ArrayDeque<Node<K, V>>()
        var current = root
        while (current != null && current.key != key) {
            deque.addLast(current)
            if (current.key > key) {
                current = current.left
            } else {
                current = current.right
            }
        }
        return deque
    }

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<Map.Entry<K, V>> {
        return ScapeGoatTreeIterator()
    }

    /**
     * Returns the value corresponding to the given [key], or `null` if such balanceFactor key is not present in the map.
     */
    override fun get(key: K): V? {
        return find(key, root)?.value
    }

    /**
     * Associates the specified [value] with the specified [key] in the map.
     *
     * @return the previous value associated with the key, or `null` if the key was not present in the map.
     */
    override fun put(key: K, value: V): V? {
        val oldValue: V?
        val path = findPath(key)
        if (path.isEmpty()) {
            root = Node(key, value)
            oldValue = null
        } else {
            val last = path.last
            if (last.key == key) {
                oldValue = last.value
                entries.remove(ScapeGoatEntry(key, oldValue))
                values.remove(last.value)
                last.value = value
            } else {
                if (last.key > key) {
                    last.left = Node(key, value)
                    path.addLast(last.left)
                } else {
                    last.right = Node(key, value)
                    path.addLast(last.right)
                }
                oldValue = null
                balanceIfNeeded(path)
                keys.add(key)
                size++
            }
            values.add(value)
        }
        return oldValue
    }

    private fun balanceIfNeeded(path: Deque<Node<K, V>>){
        println("BALANCE_IF_NEEDED")
        val pathToGoat = findScapegoat(path)
        if (!pathToGoat.isEmpty()) {
            val goat = pathToGoat.removeLast()
            if (!pathToGoat.isEmpty()) {
                if (pathToGoat.last.left == goat) {
                    pathToGoat.last.left = rebuild(goat)
                } else {
                    pathToGoat.last.right = rebuild(goat)
                }
            } else {
                root = rebuild(goat)
            }
        }
    }

    internal fun findScapegoat(deque: Deque<Node<K, V>>): Deque<Node<K, V>> {
        println("FIND_SCAPEGOAT")
        var currentSize = 1
        var depth = 0
        while (!deque.isEmpty()) {
            val current = deque.removeLast()
            if (!deque.isEmpty()){
                depth += 1
                val parent = deque.last
                val siblingSize = if (parent.left == current) sizeOf(parent.right) else sizeOf(parent.left)
                val totalSize = 1 + currentSize + siblingSize
                println("TOTAL_SIZE: $totalSize")
                var coefficient = Math.abs(Math.log(totalSize.toDouble()) / Math.log(1 / A))
                        .toString()
                if (coefficient.length < 7){
                    coefficient = coefficient.substring(0, coefficient.lastIndex)
                } else {
                    coefficient = coefficient.substring(0, 6)
                }
                println("COEFF: " + coefficient)
                println("HEIGHT: " + depth)
                if (depth > coefficient.toDouble()) {
                    println("height > coefficient")
                    return deque
                }
                currentSize = totalSize
            }
        }
        return deque
    }

    internal fun sizeOf(root: Node<K, V>?): Int {
        println("SIZE_OF" + root)
        if (root == null) {
            return 0
        } else {
            val deque = ArrayDeque<Node<K, V>>()
            var count = 0
            deque.addLast(root)
            while (!deque.isEmpty()) {
                val current = deque.removeFirst()
                count++
                if (current.left != null) {
                    deque.addLast(current.left)
                }
                if (current.right != null) {
                    deque.addLast(current.right)
                }
            }
            println("return $count")
            return count
        }
    }

    internal fun rebuild(goat: Node<K, V>): Node<K, V> {
        println("REBUILD" + goat)
        val sortedNodeList = mutableListOf<Node<K, V>>()
        var top: Node<K, V>? = goat
        val stack = ArrayDeque<Node<K, V>>()
        while (top != null || !stack.isEmpty()) {
            if (!stack.isEmpty()) {
                top = stack.removeFirst()
                sortedNodeList.add(top)
                if (top.right != null){
                    val topRight = top.right
                    top.right = null
                    top = topRight
                } else {
                    top = null
                }
            }
            while (top != null) {
                stack.addFirst(top)
                val topLeft = top.left
                top.left = null
                top = topLeft
            }
        }
        return bisect(sortedNodeList)
    }

    internal fun bisect(nodeList: List<Node<K, V>>): Node<K, V> {
        println("BISECT" + nodeList.last())
        val indexOfMiddle = nodeList.size / 2
        val currentRoot = nodeList[indexOfMiddle]
        if (nodeList.size > 2) {
            currentRoot.left = bisect(nodeList.subList(0, indexOfMiddle))
            currentRoot.right = bisect(nodeList.subList(indexOfMiddle + 1, nodeList.size))
        } else if (nodeList.size == 2){
            currentRoot.left = nodeList[0]
        }
        return currentRoot
    }

    /**
     * Removes the specified key and its corresponding value from this map.
     *
     * @return the previous value associated with the key, or `null` if the key was not present in the map.
     */
    override fun remove(key: K): V? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun lastKey(): K {
        if (root == null) {
            throw NoSuchElementException()
        } else {
            var current = root
            while (current!!.right != null) {
                current = current.right
            }
            return current.key
        }
    }

    override fun firstKey(): K {
        if (root == null) {
            throw NoSuchElementException()
        } else {
            var current = root
            while (current!!.left != null) {
                current = current.left
            }
            return current.key
        }
    }

    override fun headMap(p0: K): SortedMap<K, V> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun tailMap(p0: K): SortedMap<K, V> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun subMap(p0: K, p1: K): SortedMap<K, V> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun comparator(): Comparator<in K> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    override fun containsValue(value: V): Boolean {
        val iterator = values.iterator()
        while (iterator().hasNext()) {
            if (iterator.next() == value) {
                return true
            }
        }
        return false
    }

    /**
     * Removes all elements from this map.
     */
    override fun clear() {
        root = null
        size = 0
        keys.clear()
        values.clear()
        entries.clear()
    }

    /**
     * Updates this map with key/value pairs from the specified map [from].
     */
    override fun putAll(from: Map<out K, V>) {
        for (node in from) {
            put(node.key, node.value)
        }
    }

    /**
     * Returns `true` if the map contains the specified [key].
     */
    override fun containsKey(key: K): Boolean {
        return find(key, root) != null
    }

    /**
     * Returns `true` if the map is empty (contains no elements), `false` otherwise.
     */
    override fun isEmpty(): Boolean {
        return root == null
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("{")
        var top: Node<K, V>? = root
        val stack = ArrayDeque<Node<K, V>>()
        while (top != null || !stack.isEmpty()) {
            if (!stack.isEmpty()) {
                top = stack.removeFirst()
                sb.append(top)
                sb.append(", ")
                if (top.right != null){
                    val topRight = top.right
                    top.right = null
                    top = topRight
                } else {
                    top = null
                }
            }
            while (top != null) {
                stack.addFirst(top)
                val topLeft = top.left
                top.left = null
                top = topLeft
            }
        }
        sb.delete(sb.length - 2, sb.length)
        sb.append("}")
        return sb.toString()
    }
}