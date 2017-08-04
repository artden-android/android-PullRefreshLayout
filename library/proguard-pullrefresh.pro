#copy this to your app module and apply

#通过反射调用了RefreshDrawable实现者的构造方法，故keep所有实现者类名及它们的构造方法
-keep class * extends com.baoyz.widget.RefreshDrawable {
    <init>(android.content.Context, com.baoyz.widget.PullRefreshLayout);
}