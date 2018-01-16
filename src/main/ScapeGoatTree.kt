package main

import java.io.Serializable
import java.util.*
import kotlin.NoSuchElementException

class ScapeGoatTree<K, V>(balanceFactor: Double) :
        SortedMap<K, V>, Iterable<Map.Entry<K, V>>, Cloneable, Serializable {

    internal var root: ScapeGoatEntry<K, V>? = null

    var balanceFactor: Double = if (balanceFactor >= 1 || balanceFactor < 0.5) {
        throw IllegalArgumentException("0.5 <= balanceFactor < 1")
    } else {
        val factor = balanceFactor.toString()
        if (factor.length < 7) {
            factor.toDouble()
        } else {
            factor.substring(0, 6).toDouble()
        }
    }
        set(value) {
            val oldBalanceFactor = field
            if (value >= 1 || value < 0.5) {
                throw IllegalArgumentException("0.5 <= balanceFactor < 1")
            } else {
                val factor = value.toString()
                field = if (factor.length < 7) {
                    factor.toDouble()
                } else {
                    factor.substring(0, 6).toDouble()
                }
                if (oldBalanceFactor > field && root != null) {
                    root = rebuild(root!!)
                }
            }
        }

    private var _size: Int = 0

    private var lastModifiedSize = 0

    private var comparator: Comparator<K>? = null

    constructor(balanceFactor: Double, comparator: Comparator<K>) : this(balanceFactor) {
        this.comparator = comparator
    }

    /**
     * Returns the number of key/value pairs in the map.
     */
    override val size: Int
        get() = _size

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
        get() {
            val set = mutableSetOf<MutableMap.MutableEntry<K, V>>()
            for (element in this) {
                set.add(element as MutableMap.MutableEntry<K, V>)
            }
            return set
        }

    override val keys: MutableSet<K>
        get() {
            val set = mutableSetOf<K>()
            for (element in this) {
                set.add(element.key)
            }
            return set
        }

    override val values: MutableList<V>
        get() {
            val set = mutableListOf<V>()
            for (element in this) {
                set.add(element.value)
            }
            return set
        }

    private fun find(key: K, from: ScapeGoatEntry<K, V>?): ScapeGoatEntry<K, V>? {
        if (from == null || from.key == key) {
            return from
        } else if (compare(from.key, key) > 0) {
            return find(key, from.left)
        } else {
            return find(key, from.right)
        }
    }

    internal fun findPath(key: K): Deque<ScapeGoatEntry<K, V>> {
        val deque = ArrayDeque<ScapeGoatEntry<K, V>>()
        var current = root
        while (current != null) {
            deque.addLast(current)
            if (current.key == key) {
                current = null
            } else {
                if (compare(current.key, key) > 0) {
                    current = current.left
                } else {
                    current = current.right
                }
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

    inner class ScapeGoatTreeIterator : Iterator<Map.Entry<K, V>> {
        private val innerRoots = ArrayDeque<ScapeGoatEntry<K, V>>()
        private var next = root
        private var counter = size
        private val firstSize = size
        private var returnedBack = false

        private fun findNext(): ScapeGoatEntry<K, V>? {
            val current = next
            if (next != null) {
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
            }
            return current
        }

        /**
         * Returns `true` if the iteration has more elements.
         */
        override fun hasNext(): Boolean {
            return counter > 0
        }

        /**
         * Returns the next element in the iteration.
         */
        override fun next(): Map.Entry<K, V> {
            if (size != firstSize) {
                throw ConcurrentModificationException()
            } else {
                return findNext() ?: throw NoSuchElementException()
            }
        }

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
            root = ScapeGoatEntry(key, value)
            oldValue = null
            _size++
        } else {
            val last = path.last
            if (last.key == key) {
                oldValue = last.value
                last.value = value
            } else {
                if (compare(last.key, key) > 0) {
                    last.left = ScapeGoatEntry(key, value)
                    path.addLast(last.left)
                } else {
                    last.right = ScapeGoatEntry(key, value)
                    path.addLast(last.right)
                }
                oldValue = null
                balanceIfNeeded(path)
                _size++
            }
        }
        return oldValue
    }

    private fun balanceIfNeeded(path: Deque<ScapeGoatEntry<K, V>>) {
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

    internal fun findScapegoat(deque: Deque<ScapeGoatEntry<K, V>>): Deque<ScapeGoatEntry<K, V>> {
        var currentSize = 1
        var depth = 0
        while (!deque.isEmpty()) {
            val current = deque.removeLast()
            if (!deque.isEmpty()) {
                depth += 1
                val parent = deque.last
                val siblingSize = if (parent.left == current) sizeOf(parent.right) else sizeOf(parent.left)
                val totalSize = 1 + currentSize + siblingSize
                var coefficient = Math.abs(Math.log(totalSize.toDouble()) / Math.log(1 / balanceFactor))
                        .toString()
                if (coefficient.length > 6) {
                    coefficient = coefficient.substring(0, 6)
                }
                if (depth > coefficient.toDouble()) {
                    return deque
                }
                currentSize = totalSize
            }
        }
        return deque
    }

    internal fun sizeOf(root: ScapeGoatEntry<K, V>?): Int {
        if (root == null) {
            return 0
        } else {
            val deque = ArrayDeque<ScapeGoatEntry<K, V>>()
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
            return count
        }
    }

    internal fun rebuild(goat: ScapeGoatEntry<K, V>): ScapeGoatEntry<K, V> {
        val sortedEntryList = mutableListOf<ScapeGoatEntry<K, V>>()
        var top: ScapeGoatEntry<K, V>? = goat
        val stack = ArrayDeque<ScapeGoatEntry<K, V>>()
        while (top != null || !stack.isEmpty()) {
            if (!stack.isEmpty()) {
                top = stack.removeFirst()
                sortedEntryList.add(top)
                if (top.right != null) {
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
        lastModifiedSize = _size
        return toTree(sortedEntryList)
    }

    /*
     *Рекурсивно ломает лист пополам
     */
    internal fun toTree(entryList: List<ScapeGoatEntry<K, V>>): ScapeGoatEntry<K, V> {
        val indexOfMiddle = entryList.size / 2
        val currentRoot = entryList[indexOfMiddle]
        if (entryList.size > 2) {
            currentRoot.left = toTree(entryList.subList(0, indexOfMiddle))
            currentRoot.right = toTree(entryList.subList(indexOfMiddle + 1, entryList.size))
        } else if (entryList.size == 2) {
            currentRoot.left = entryList[0]
        }
        return currentRoot
    }

    /**
     * Removes the specified key and its corresponding value from this map.
     *
     * @return the previous value associated with the key, or `null` if the key was not present in the map.
     */
    override fun remove(key: K): V? {
        val path = findPath(key)
        if (!path.isEmpty() && path.last.key == key) {
            val node = path.removeLast()
            val parentOfNode = if (path.isEmpty()) null else path.removeLast()
            val newNode: ScapeGoatEntry<K, V>?
            if (node.left != null && node.right != null) {
                var parentOfLeftmostOfRight = node
                var leftmostOfRight = node.right
                while (leftmostOfRight!!.left != null) {
                    parentOfLeftmostOfRight = leftmostOfRight
                    leftmostOfRight = leftmostOfRight.left
                }
                if (node.right != leftmostOfRight) {
                    parentOfLeftmostOfRight.left = leftmostOfRight.right
                    leftmostOfRight.right = node.right
                }
                leftmostOfRight.left = node.left
                newNode = leftmostOfRight
            } else if (node.left != null) {
                newNode = node.left
            } else if (node.right != null) {
                newNode = node.right
            } else {
                newNode = null
            }
            if (parentOfNode != null) {
                if (parentOfNode.left == node) {
                    parentOfNode.left = newNode
                } else {
                    parentOfNode.right = newNode
                }
            }
            if (node == root) {
                root = newNode
            }
            _size--
            if (_size < balanceFactor * lastModifiedSize && root != null) {
                root = rebuild(root!!)
            }
            return node.value
        } else {
            return null
        }
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

    override fun subMap(fromKey: K, toKey: K): SortedMap<K, V> {
        return SubMap(fromKey, toKey)
    }

    override fun headMap(toKey: K): SortedMap<K, V> {
        return SubMap(null, toKey)
    }

    override fun tailMap(fromKey: K): SortedMap<K, V> {
        return SubMap(fromKey, null)
    }

    inner class SubMap(val fromKey: K?, val toKey: K?) : SortedMap<K, V>, Iterable<Map.Entry<K, V>> {

        init {
            if (fromKey != null && toKey != null) {
                if (compare(fromKey, toKey) > 0) throw IllegalArgumentException()
            } else if (fromKey == null && toKey == null) throw IllegalArgumentException()
        }

        private fun isKeyInRange(key: K) =
                if (fromKey != null && toKey != null) {
                    compare(key, fromKey) >= 0 && compare(key, toKey) < 0
                } else if (fromKey != null) {
                    compare(key, fromKey) >= 0
                } else if (toKey != null) {
                    compare(key, toKey) < 0
                } else throw IllegalArgumentException()

        override fun get(key: K): V? {
            return if (isKeyInRange(key)) this@ScapeGoatTree[key] else throw IllegalArgumentException()
        }

        override fun put(key: K, value: V): V? {
            return if (isKeyInRange(key)) this@ScapeGoatTree.put(key, value) else throw IllegalArgumentException()
        }

        override fun remove(key: K): V? {
            return if (isKeyInRange(key)) this@ScapeGoatTree.remove(key) else throw IllegalArgumentException()
        }

        override fun subMap(fromKey: K, toKey: K): SortedMap<K, V> {
            throw UnsupportedOperationException()
        }

        override fun headMap(toKey: K): SortedMap<K, V> {
            throw UnsupportedOperationException()
        }

        override fun tailMap(fromKey: K): SortedMap<K, V> {
            throw UnsupportedOperationException()
        }

        override fun containsKey(key: K): Boolean {
            for (element in this) {
                if (element.key == key) {
                    return true
                }
            }
            return false
        }

        override fun containsValue(value: V): Boolean {
            for (element in this) {
                if (element.value == value) {
                    return true
                }
            }
            return false
        }

        override fun firstKey(): K {
            val iterator = SubMapIterator()
            if (iterator.hasNext()) {
                return iterator.next().key
            } else {
                throw NoSuchElementException()
            }
        }

        override fun lastKey(): K {
            var current: Map.Entry<K, V>? = null
            for (element in this) {
                current = element
            }
            if (current != null) {
                return current.key
            } else {
                throw NoSuchElementException()
            }
        }

        override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
            get() {
                val set = mutableSetOf<MutableMap.MutableEntry<K, V>>()
                for (element in this) {
                    set.add(element as MutableMap.MutableEntry<K, V>)
                }
                return set
            }

        override val keys: MutableSet<K>
            get() {
                val set = mutableSetOf<K>()
                for (element in this) {
                    set.add(element.key)
                }
                return set
            }

        override val values: MutableList<V>
            get() {
                val set = mutableListOf<V>()
                for (element in this) {
                    set.add(element.value)
                }
                return set
            }

        override val size: Int
            get() {
                var counter = 0
                for (element in this) {
                    counter++
                }
                return counter
            }

        override fun putAll(from: Map<out K, V>) {
            for (element in from) {
                put(element.key, element.value)
            }
        }

        override fun isEmpty(): Boolean {
            return this.size == 0
        }

        override fun comparator(): Comparator<K>? {
            return comparator
        }

        override fun clear() {
            for (element in keys) {
                this@ScapeGoatTree.remove(element)
            }
        }

        override fun iterator(): Iterator<Map.Entry<K, V>> {
            return SubMapIterator()
        }

        inner class SubMapIterator : Iterator<Map.Entry<K, V>> {
            private val iterator = this@ScapeGoatTree.iterator()
            private var next: Map.Entry<K, V>? = null
            private val firstSize = _size

            init {
                if (fromKey != null) {
                    while (iterator.hasNext()) {
                        val currentNext = iterator.next()
                        if (compare(currentNext.key, fromKey) >= 0) {
                            next = if (isKeyInRange(currentNext.key)) currentNext else null
                            break
                        }
                    }
                } else {
                    val currentNext = if (iterator.hasNext()) iterator.next() else null
                    if (currentNext != null && isKeyInRange(currentNext.key)) {
                        next = currentNext
                    } else {
                        next = null
                    }
                }
            }

            override fun hasNext(): Boolean {
                return next != null
            }

            override fun next(): Map.Entry<K, V> {
                if (firstSize != _size) {
                    throw ConcurrentModificationException()
                } else if (next == null) {
                    throw NoSuchElementException()
                } else {
                    val returned = next!!
                    if (iterator.hasNext()) {
                        val currentNext = iterator.next()
                        if (isKeyInRange(currentNext.key)) {
                            next = currentNext
                        } else {
                            next = null
                        }
                    } else {
                        next = null
                    }
                    return returned
                }
            }
        }

        override fun toString(): String {
            val sb = StringBuilder()
            sb.append("{")
            for (element in this) {
                sb.append("$element, ")
            }
            if (sb.length > 2) {
                sb.delete(sb.length - 2, sb.length)
            }
            sb.append("}")
            return sb.toString()
        }
    }

    override fun comparator(): Comparator<in K>? {
        return comparator
    }

    /**
     * Returns `true` if the map maps one or more keys to the specified [value].
     */
    override fun containsValue(value: V): Boolean {
        for (element in this) {
            if (element.value == value) {
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
        _size = 0
        lastModifiedSize = 0
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
        var top: ScapeGoatEntry<K, V>? = root
        val stack = ArrayDeque<ScapeGoatEntry<K, V>>()
        while (top != null || !stack.isEmpty()) {
            if (!stack.isEmpty()) {
                top = stack.removeFirst()
                sb.append("$top, ")
                if (top.right != null) {
                    val topRight = top.right
                    top = topRight
                } else {
                    top = null
                }
            }
            while (top != null) {
                stack.addFirst(top)
                val topLeft = top.left
                top = topLeft
            }
        }
        if (sb.length > 2) {
            sb.delete(sb.length - 2, sb.length)
        }
        sb.append("}")
        return sb.toString()
    }

    private fun compare(first: K, second: K): Int {
        return when {
            comparator != null -> comparator!!.compare(first, second)
            first is Comparable<*> -> (first as Comparable<Any>).compareTo(second as Comparable<*>)
            else -> throw ClassCastException("Comparator is absent but K is not comparable")
        }
    }
}
