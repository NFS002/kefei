package persist

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import mu.KLogger
import mu.KotlinLogging
import java.io.FileInputStream
import java.util.*


import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField
import kotlin.text.RegexOption.DOT_MATCHES_ALL


abstract class Persistable {

    private val log = KotlinLogging.logger {}

    companion object {

        var options: Properties = loadOptions()

        init {
            options = loadOptions()
        }

        private fun loadOptions(): Properties {
            val log: KLogger = KotlinLogging.logger { }
            val ops = Properties()
            val defaultConfigPath = "~/.kefei/system-options.properties"
            val configPath = System.getenv("KEFEI__SYSTEM_PATH") ?: defaultConfigPath
            log.info { "Loading system configuration from $configPath" }
            val res = FileInputStream(configPath).runCatching {
                this.use {
                    ops.load(it)
                }
            }
            res.onFailure {
                log.error { "Failed to load system configuration: ${it}" }
            }
            res.onSuccess {
                log.info { "Loaded system configuration" }
            }

            return ops
        }

    }

    private val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val fields: MutableList<PersistentField> = mutableListOf()

    init {
        log.debug { "Instantiating persistable class of ${this::class.qualifiedName} with options ${options}" }
        val setValues: Boolean = options.getProperty("init.load", "true").toBoolean()

        this.load(setValues = setValues)
        if (options.getProperty("shutdown.save", "true").toBoolean()) {
            this.saveOnExit()
        }
    }

    /* Load from file */
    fun load(name: String = ".*", setValues: Boolean = true) {
        val classFields = this::class.declaredMemberProperties
        log.info { "Loading persistent fields matching ${name} from ${classFields.size}" }
        for (field in classFields) {
            if (field.name.matches(name.toRegex(DOT_MATCHES_ALL))) {
                for (a in field.annotations) {
                    if (a.annotationClass == Persist::class) {
                        log.debug { "Loading value for: $field" }
                        field.isAccessible = true
                        val javaField = field.javaField
                        if (javaField != null) {
                            val pfield = PersistentField(this::class, javaField)
                            if (setValues) {
                                val json = readTextFromFile(pfield)
                                val adapter = moshi.adapter(javaField.type)
                                when (json.isBlank() || json.isEmpty()) {
                                    true -> {
                                        log.warn { "File ${pfield.file.path} was empty" }
                                    }
                                    false -> {
                                        val v = adapter.fromJson(json)
                                        javaField.trySetAccessible()
                                        javaField.set(this, v)
                                        log.info { "Set value '${v.logf()}' for: $field" }
                                    }
                                }
                            }
                            this.fields.add(pfield)
                            log.debug { "Added persistent field '${field.logf()}' to state" }
                        }
                    }
                }
            }
        }
    }

    /* Write to file */
    fun save() {
        for (field in this.fields) {
            writeBack(field)
        }
    }

    fun writeBack(pfield: PersistentField) {
        val adapter = moshi.adapter<Any>(pfield.field.type)
        val v = adapter.toJson(pfield.field.get(this))
        log.info { "Saving value '${v.logf()}' $pfield " }
        pfield.file.writeText(v)
    }


    private fun readTextFromFile(field: PersistentField): String {
        val str = field.file.readLines().joinToString(separator = "")
        log.debug { "Read '${str.logf()}' from file" }
        return str
    }

    fun saveOnExit() {
        log.debug { "Registering shutdown hook" }
        Runtime.getRuntime().addShutdownHook(Thread { this.save() })
    }
}