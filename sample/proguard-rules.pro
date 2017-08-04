# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/baoyz/Developer/Android/sdk/tools/proguard/proguard-android.txt
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

#通过反射调用了RefreshDrawable实现者的构造方法，故keep所有实现者类名及它们的构造方法
-keep class * extends com.baoyz.widget.RefreshDrawable {
    <init>(android.content.Context, com.baoyz.widget.PullRefreshLayout);
}
