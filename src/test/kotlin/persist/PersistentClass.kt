package persist

import persist.Persist
import persist.Persistable

enum class ShoeCondition() {
    BNIB,
    DECENT,
    BATTERED
}

data class Person(var name:String, var swagLevel: Long = 0L)

data class Shoe(var size:Int, var material: String,
                var condition: ShoeCondition = ShoeCondition.BATTERED,
                var previousOwners: List<Person> = listOf())

class PersistentClass: Persistable() {

    @Persist
    lateinit var mutListStr: MutableList<String>

    @Persist
    lateinit var listStr: List<String>

    @Persist
    lateinit var str1: String

    @Persist
    lateinit var shoe: Shoe

    @Persist
    var nullableInt:Int? = null

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersistentClass

        return (
            this.str1.equals(other.str1) &&
            this.nullableInt == other.nullableInt &&
            this.listStr.equals(other.listStr) &&
            this.mutListStr.equals(other.mutListStr) &&
            this.shoe.equals(other.shoe)
        )
    }
}