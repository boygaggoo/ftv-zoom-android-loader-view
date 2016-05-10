# francetv zoom Loader View #

This project is part of [francetv zoom open source projects](https://github.com/francetv/zoom-public) (iOS, Android and Angular).

Angular module to make popin easily. It provide a service to build popin with a basic style.

![](demo.gif)

## How to ##

#### Copy files in your project ####

From /lib directory, copy ZoomLoaderView.java and img_loader_mask.png in your project java package and resources /drawable-xhdmi directory.

#### Add in your layout ####

```xml
    <fr.francetv.zoom.share.loader.ZoomLoaderView
        android:id="@+id/loader"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
```

#### In your activity ####


```java
    private ZoomLoaderView mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoader = (ZoomLoaderView)findViewById(R.id.loader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoader.start();
    }

    @Override
    protected void onPause() {
        mLoader.stop();
        super.onPause();
    }
```

#### Change color ####

```java
    mLoader.setThemeColor(ContextCompat.getColor(getApplicationContext(), android.R.color.holo_purple));
```
