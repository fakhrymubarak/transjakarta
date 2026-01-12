# General
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.android.AndroidExceptionPreHandler {
    <init>();
}

# Hilt / Dagger
-keep class * extends dagger.hilt.internal.definecomponent.DefineComponentNoParent
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper$LayoutInflaterFactoryWrapper
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper$LayoutInflaterFactoryWrapper$1
-keep class dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper
-keep class dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper$1

# Retrofit
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and EnclosingMethod to switch over to the generic signature.
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keepattributes *Annotation*

# KotlinX Serialization
-keepattributes *Annotation*, InnerClasses
-dontwarn kotlinx.serialization.SerializationException

# Keep DTOs and Data Classes used in Network/JSON
# Adjust package name to match your specific DTO packages
-keep class com.fakhry.transjakarta.**Dto { *; }
-keep class com.fakhry.transjakarta.**.remote.response.** { *; }

# Paging 3
-dontwarn androidx.paging.**

# Maps / Plays Services (if used)
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**