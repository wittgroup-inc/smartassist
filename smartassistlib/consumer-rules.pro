# Added for gson deserialization
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation @interface com.google.gson.annotations.SerializedName

# Add this global rule
-keepattributes Signature

-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
# Added for firebase realtime deserialization
# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models. Modify to fit the structure
# of your app.
-keepclassmembers class com.gowittgroup.smartassistlib.models.** {
*;
}

-keep class com.google.ai.client.generativeai.** { *; }