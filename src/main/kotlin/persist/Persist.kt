package persist


@Target(AnnotationTarget.PROPERTY)
@MustBeDocumented
annotation class Persist(val key: String = "default")

