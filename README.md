# Kefei
## Is it just me or are databases a bit of a ballache ?!?!
### Kefei is a simple and easy alternative...

```kotlin
class MyClass: Persistable() {

    @Persist
    lateinit var myImportantList: MutableList<String>

    @Persist
    lateinit var myImportantStr: String
    
    /* Whatever else you want... */
}
```

## Stack:
- Kotlin
- Gradle
- Dependencies
  - Kotlin test 
  - Kotlin test junit
  - Logback
  - Moshi (for serialisation of objects and properties)
  - Kotlin reflect


## How it works:
In essence, for any class that extends persist.Persistable that has properties
annotated with @persist.Persist, kefei will register a jvm shutdown hook to save the value
of the property to a local file under the path ./${options.dir}/${class.name}/${field.class.name}/${field.name}

Then, when the application restarts, the same properties from the same class will load its initial value from the file with this path.

## Features
- Supports all primitives and data types (AFAIK)
- JSON serialisation using Moshi
- Optional configuration file
- Minimal, simple code and easy to integrate into projects
- Occasionally dips into the JVM for reflection, but as much as possible is done at the kotlin layer
- Logging of all major lifecycle events use kotlin-logging and logback

### Dedicated to my princess ❤️
