package main

class ScapeGoatEntry<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {

    internal var left: ScapeGoatEntry<K, V>? = null
    internal var right: ScapeGoatEntry<K, V>? = null

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

    override fun equals(other: Any?): Boolean {
        return (other is ScapeGoatEntry<*, *> && key == other.key)
    }

    override fun hashCode(): Int {
        return key!!.hashCode()
    }

}