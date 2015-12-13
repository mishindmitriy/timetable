# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\Program Files\android sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# androidannotations
-dontwarn org.springframework.**

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keep public class * extends android.support.v7.widget.RecyclerView$LayoutManager {
    public <init>(...);
}

# joup
-keep public class org.jsoup.** {
public *;
}

# fabric
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
# my classes for ormlite
-keep class mishindmitriy.timetable.model.db.** { *; }
-keepclasseswithmembers class mishindmitriy.timetable.model.db.** { *; }
-keep class mishindmitriy.timetable.model.data.entity.** { *; }
-keepclasseswithmembers class mishindmitriy.timetable.model.data.entity.** { *; }
