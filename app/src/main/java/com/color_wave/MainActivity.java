package com.color_wave;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.*;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    int showHints = 1;
    Button goBtn;
    Button hintBtn;
    LinearLayout hintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hintLayout = findViewById(R.id.hint_text);

        // Let's go button
        goBtn = findViewById(R.id.lets_go);
        goBtn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            }
        });

        hintBtn = findViewById(R.id.hint_toggle);
        hintBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                toggleHints(hintLayout, hintBtn);
            }
        });

        toggleHints(hintLayout, hintBtn);
    }

    public void toggleHints(final LinearLayout layout, Button button){
        if (showHints == 0){
            layout.setVisibility(View.VISIBLE);
            layout.setAlpha(0.0f);
            layout.animate().translationY(layout.getHeight()/20).alpha(1.0f).setListener(null);
            button.setText(R.string.how_it_works_minus);
        } else {
            layout.animate().translationY(0).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    layout.setVisibility(View.INVISIBLE);
                }
            });
            button.setText(R.string.how_it_works_plus);
        }
        showHints = (showHints + 1) % 2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            Intent previewImageIntent = new Intent(this, PreviewImageActivity.class);
            previewImageIntent.putExtra("BitmapImage", imageBitmap);

            startActivity(previewImageIntent);
        }
    }

    public void getTestText(final TextView textView) {
        ColorWaveRestClient.get("test", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                textView.setText(responseString);
            }
        });
    }
}
