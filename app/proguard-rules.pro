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

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference {
    *;
}

-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v7.preference.Preference {
    *;
}
-keep class android.support.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

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

# OrmLite uses reflection
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }

#We are not done yet, add the following as well.
-keepclassmembers class * {
    public <init>(android.content.Context);
 }

-keepattributes *Annotation*

#Only if the above haven't fixed then, go with keeping your db class files and its members.
#In your case, that would be
-keep class mishindmitriy.timetable.model.data.entity.**
-keepclassmembers class mishindmitriy.timetable.model.data.entity.** { *; }
