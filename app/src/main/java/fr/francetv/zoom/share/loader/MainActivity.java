package fr.francetv.zoom.share.loader;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int[] mColors = new int[] {
            android.R.color.holo_blue_light,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light,
            android.R.color.holo_purple
    };

    private final Random mRandom = new Random();

    private final View.OnClickListener mButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLoader.setThemeColor(ContextCompat.getColor(getApplicationContext(), mColors[mRandom.nextInt(mColors.length)]));
        }
    };

    private ZoomLoaderView mLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoader = (ZoomLoaderView)findViewById(R.id.loader);
        findViewById(R.id.change_color_button).setOnClickListener(mButtonOnClickListener);
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
}
