# Keep Room entities and generated DAOs
-keep class com.herotraining.data.db.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
