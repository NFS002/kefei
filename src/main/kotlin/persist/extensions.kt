package persist

inline fun Boolean?.then(block: Boolean.() -> Unit): Boolean? {
    if (this == true) {
        block()
    }
    return this
}