package com.example.ecotrack;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GuidelineDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_guideline_detail);

        // Handle the back button press
        findViewById(R.id.toolBar).setOnClickListener(v -> finish());

        // Retrieve data from intent
        Intent intent = getIntent();
        String itemName = intent.getStringExtra("itemName");
        String itemIntro = intent.getStringExtra("itemIntro");
        String itemTips = intent.getStringExtra("itemTips");
        String imageRes = intent.getStringExtra("imageRes");
        String videoUrl = intent.getStringExtra("videoUrl");

        // Find views
        TextView detailName = findViewById(R.id.detailName);
        TextView detailIntro = findViewById(R.id.detailIntro);
        TextView detailTips = findViewById(R.id.detailTips);
        ImageView detailImage = findViewById(R.id.detailImage);
        WebView webView = findViewById(R.id.detailVideo);

        // Set the fetched data
        if (itemName != null) detailName.setText(itemName);
        if (itemIntro != null) detailIntro.setText(itemIntro);
        if (itemTips != null) detailTips.setText(itemTips);

        // Load the image using the resource name
        if (imageRes != null) {
            int resId = getResources().getIdentifier(imageRes, "drawable", getPackageName());
            if (resId != 0) {
                detailImage.setImageResource(resId);
            } else {
                Toast.makeText(this, "Image resource not found", Toast.LENGTH_SHORT).show();
            }
        }

        // Set up WebView for YouTube iframe
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);

        // Extract and embed the YouTube video
        String videoId = extractYouTubeVideoId(videoUrl);
        if (videoId != null) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            webView.loadUrl(embedUrl);
        } else {
            Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    private String extractYouTubeVideoId(@Nullable String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return null;
        }

        String videoId = null;
        if (videoUrl.contains("youtu.be/")) {
            videoId = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);
            int questionMarkIndex = videoId.indexOf('?');
            if (questionMarkIndex != -1) {
                videoId = videoId.substring(0, questionMarkIndex);
            }
        } else if (videoUrl.contains("v=")) {
            videoId = videoUrl.substring(videoUrl.indexOf("v=") + 2);
            int ampersandIndex = videoId.indexOf('&');
            if (ampersandIndex != -1) {
                videoId = videoId.substring(0, ampersandIndex);
            }
        }
        return videoId;
    }
}