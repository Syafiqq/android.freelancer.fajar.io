# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
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
# For volley https://github.com/google/volley/blob/master/consumer-proguard-rules.pro
-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.NetworkDispatcher {
    void processRequest();
}
-keepclassmembers,allowshrinking,allowobfuscation class com.android.volley.CacheDispatcher {
    void processRequest();
}

# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-joda-time.pro
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

# html-textview
# iconics https://github.com/mikepenz/Android-Iconics#proguard
-keep class .R
-keep class **.R$* {
    <fields>;
}

# android-pdf-viewer https://github.com/barteksc/AndroidPdfViewer#proguard
-keep class com.shockwave.**
