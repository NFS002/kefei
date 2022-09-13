package persist

inline fun Boolean?.then(block: Boolean.() -> Unit): Boolean? {
    if (this == true) {
        block()
    }
    return this
}

fun Any?.logf(): Any? {
    if (this != null) {
        return this.toString().subSequence(0, 10).padEnd(3, '.').toString()
    }
    return null
}