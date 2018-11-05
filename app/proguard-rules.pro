# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/wyouflf/develop/android-sdk/tools/proguard/proguard-android.txt
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
#指定代码的压缩级别
-optimizationpasses 5
    
#包明不混合大小写
-dontusemixedcaseclassnames
    
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
    
#优化  不优化输入的类文件
-dontoptimize
    
#预校验
-dontpreverify
    
#混淆时是否记录日志
-verbose
    
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
    
#保护注解
-keepattributes *Annotation*
    
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * { 
    native <methods>;
}
 # 保持自定义控件类不被混淆
-keepclasseswithmembers class * {  
    public <init>(android.content.Context, android.util.AttributeSet);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保持自定义控件类不被混淆   
-keepclassmembers class * extends android.app.Activity { 
    public void *(android.view.View);
}
# 保持枚举 enum 类不被混淆    
-keepclassmembers enum * {     
    public static **[] values();    
    public static ** valueOf(java.lang.String);
}
# 保持 Parcelable 不被混淆 
-keep class * implements android.os.Parcelable {  
    public static final android.os.Parcelable$Creator *;
}

################### for xUtils
-keep public class org.xutils.** {
    public protected *;
}
-keep public interface org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.** {
    public protected *;
}
-keepclassmembers class * extends org.xutils.http.RequestParams {*;}
-keepclassmembers class * {
   void *(android.view.View);
   *** *Click(...);
   *** *Event(...);
}

################## common
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Preserve all annotations.

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
    public protected *;
}


# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# Preserve all View implementations, their special context constructors, and
# their setters.

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Preserve all possible onClick handlers.

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

# Preserve the special fields of all Parcelable implementations.

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.

-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve annotated Javascript interface methods.

-keepclassmembers class * {
   @android.webkit.JavascriptInterface <methods>;
}

# Preserve the required interface from the License Verification Library
# (but don't nag the developer if the library is not used at all).

-keep public interface com.android.vending.licensing.ILicensingService

-dontnote com.android.vending.licensing.ILicensingService

#-keepclassmembers class * { 
#   *void *(android.view.View); 
#	* *** *Click(...); 
#	* *** *Event(...);  * }

-keepattributes *JavascriptInterface*

-dontshrink

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep public class * extends android.support.v4.**


#umeng统计
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.qxshikong.myvideoapp.R$*{
public static final int *;
}

# 以下类过滤不混淆
-dontwarn com.umeng.**
-keep public class * extends com.umeng.**
# 以下包不进行过滤
-keep class com.umeng.** { *; }

-dontwarn cn.jpush.**
-keepattributes  EnclosingMethod,Signature
-keep class cn.jpush.** { *; }
-keepclassmembers class ** {
    public void onEvent*(**);
}
#========================gson================================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#========================protobuf================================
-dontwarn com.google.**
-keep class com.google.protobuf.** {*;}


