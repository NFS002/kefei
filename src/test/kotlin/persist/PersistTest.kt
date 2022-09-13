package persist

import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals


class PersistTest {

    private val dir = File("kefei")


    @Before
    fun before() {
        dir.exists().then {
            dir.deleteRecursively()
        }
    }

    @After
    fun after() {
        dir.exists().then {
            dir.deleteRecursively()
        }
    }

    @Test
    fun loads() {
        val pObj1 = PersistentClass()
        pObj1.str1 = "Do you know the way?"
        pObj1.listStr = listOf("in", "the", "end", "it", "will", "all", "be", "ok")
        pObj1.mutListStr = mutableListOf("this", "to", "shall", "pass")
        pObj1.nullableInt = 200
        pObj1.shoe = Shoe(size = 11, material = "fresh wood", condition = ShoeCondition.DECENT)
        pObj1.shoe.previousOwners = listOf(Person("maxwell"), Person(name = "stacey", swagLevel = 100))

        pObj1.save()

        val pObj2 = PersistentClass()
        pObj2.load()

        assertEquals(pObj1, pObj2)
    }

}

