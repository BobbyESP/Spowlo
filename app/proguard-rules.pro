# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Add kotlinx.serialization to the list of libraries to be processed by Proguard
# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
   static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
   static **$* *;
}
-keepclassmembers class <2>$<3> {
   kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
   public static ** INSTANCE;
}
-keepclassmembers class <1> {
   public static <1> INSTANCE;
   kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault
-keepattributes *Annotation*

-renamesourcefileattribute
-repackageclasses
-allowaccessmodification

# from: https://www.guardsquare.com/blog/eliminating-data-leaks-caused-by-kotlin-assertions
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
	public static void checkNotNull(...);
	public static void checkExpressionValueIsNotNull(...);
	public static void checkNotNullExpressionValue(...);
	public static void checkParameterIsNotNull(...);
	public static void checkNotNullParameter(...);
	public static void checkReturnedValueIsNotNull(...);
	public static void checkFieldIsNotNull(...);
	public static void throwUninitializedPropertyAccessException(...);
	public static void throwNpe(...);
	public static void throwJavaNpe(...);
	public static void throwAssert(...);
	public static void throwIllegalArgument(...);
	public static void throwIllegalState(...);
}

-dontwarn kotlin.**

# moshix issues with serialized sealed classes
-keep class com.bobbyesp.spowlo.appModules.core.objs.** { *; }

-assumenosideeffects class android.util.Log {
    public static void d(...);
    public static void v(...);
    public static void i(...);
    public static void w(...);
    public static void e(...);
}

# genericjson gson fixes
-keep,allowobfuscation class * extends xyz.gianlu.librespot.json.JsonWrapper { *; }

# protobuf
-keep class com.spotify.** {*;}
-keep class spotify.** {*;}
-keep class com.bobbyesp.spowlo.** {*;}
-keep class com.bobbyesp.spowlo.proto.** {*;}
-keep class com.google.protobuf.Any {*;}
-keep class * extends com.google.protobuf.AbstractMessage {*;}
-keepnames @dev.zacsweers.moshix.sealed.annotations.NestedSealed class **

# librespot
-keep class xyz.gianlu.librespot.** { *; }
-keep class xyz.gianlu.librespot.audio.decoders.** {*;}

# gson rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep specific classes and their dependencies
-keep class com.google.auto.service.AutoService
-keep class com.google.auto.value.AutoValue
-keep class javax.lang.model.** { *; }
-keep class javax.sound.sampled.** { *; }
-keep class org.apache.logging.log4j.** { *; }
-keep class org.slf4j.** { *; }