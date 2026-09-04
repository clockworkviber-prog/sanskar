# Add project specific ProGuard rules here.

# Room — entities/DAOs are reached via reflection & generated code
-keep class com.sanskar.app.data.db.** { *; }
-dontwarn androidx.room.paging.**

# Kotlin coroutines / metadata
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
-dontwarn kotlinx.coroutines.**

# Coil
-dontwarn coil.**

# Keep data classes used across Room <-> UI mapping and Compose navigation
# argument reflection (kept broadly since these are small, non-perf-critical
# model classes — safer than risking a stripped field breaking persistence).
-keep class com.sanskar.app.data.** { *; }
