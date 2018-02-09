package com.turkeytech.egranja.activity;

        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.AppCompatImageView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.View;
        import android.widget.MediaController;
        import android.widget.VideoView;

        import com.turkeytech.egranja.R;


public class VideoViewActivity extends AppCompatActivity {
    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;
    Toolbar mToolbar;
    AppCompatImageView mBackImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from video_main.xml
        setContentView(R.layout.activity_video_view);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Set back icon
        mBackImage = findViewById(R.id.videoToolbar_back);
        mBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Find your VideoView in your video_main.xml layout
        videoview = findViewById(R.id.VideoView);

        // Execute StreamVideo AsyncTask

        // Create a progressbar
        pDialog = new ProgressDialog(VideoViewActivity.this);

        // Set progressbar title
        pDialog.setTitle("Video Description");

        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

        // Show progressbar
        pDialog.show();

        try {

            Intent intent = getIntent();
            String VideoURL = intent.getStringExtra("vUrl");

            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    VideoViewActivity.this);
            mediacontroller.setAnchorView(videoview);

            // Get the URL from String VideoURL
            Uri video = Uri.parse(VideoURL);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {

            Log.e("Error", e.getMessage());
            e.printStackTrace();

        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

    }
}
