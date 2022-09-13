package persist

import java.io.File
import java.lang.reflect.Field
import java.nio.file.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.reflect.KClass

data class PersistentField(val clazz: KClass<out Any>, val field: Field) {

    val file: File

    init {
        val dir = Persistable.options.getProperty("dir", "kefei")
        val filePath = Path.of("$dir/${clazz.qualifiedName}/${field.type}/${field.name}".replace(' ', '.'))
        filePath.parent.toFile().mkdirs()
        (!filePath.exists()).then {
            filePath.createFile()
        }
        this.file = filePath.toFile()
    }
}