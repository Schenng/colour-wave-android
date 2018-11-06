package com.color_wave;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PreviewImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);

        Intent previewImageIntent = getIntent();
        final Bitmap imagePreviewBitmap = previewImageIntent.getParcelableExtra("BitmapImage");

        ImageView imagePreview =  findViewById(R.id.imagePreview);
        imagePreview.setImageBitmap(imagePreviewBitmap);

        Button yesButton = (Button) findViewById(R.id.yesButton);
        yesButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imagePreviewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                RequestParams imageParam = new RequestParams();
                imageParam.add("image", encodedImage);

                getAsyncCall(encodedImage);

            }
        });

        Button noButton = (Button) findViewById(R.id.noButton);
        noButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void getAsyncCall(String encodedImage){
        OkHttpClient httpClient = new OkHttpClient();
        String url = "http://35.184.125.33/image";

        RequestBody formBody = new FormBody.Builder()
                .add("image", encodedImage)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                Log.e("LOG", "error in getting response using async okhttp call");
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                if (!response.isSuccessful()) {
                    throw new IOException("Error response " + response);
                }

                System.out.println(responseBody.contentType());

                Bitmap processedImage = BitmapFactory.decodeStream(response.body().byteStream());

                Intent DisplayImageIntent = new Intent(PreviewImageActivity.this, DisplayImageActivity.class);
                DisplayImageIntent.putExtra("processedImage", processedImage);

                startActivity(DisplayImageIntent);

            }
        });
    }
}
