package main

import java.util.*

class ScapeGoatTreeIterator<out T: ScapeGoatEntry<*, *>>(val tree: ScapeGoatTree<*, *>) : Iterator<T> {

    private val innerRoots = ArrayDeque<T>()
    private var next = tree.root
    private val firstSize = tree.size
    private var counter = tree.size
    private var returnedBack = false

    private fun findNext(): T? {
        val current = next
        if (!returnedBack && next!!.left != null) {
            innerRoots.add(next as T)
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
        return current as T
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
    override fun next(): T {
        if (tree.size != firstSize){
            throw ConcurrentModificationException()
        } else {
            return findNext() ?: throw NoSuchElementException()
        }
    }

}
