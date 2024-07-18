package com.spgon.a3flowers.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.spgon.a3flowers.R;
import com.spgon.a3flowers.activity.Quiz;
import com.spgon.a3flowers.model.VideoItem;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoListAdapter extends ArrayAdapter<VideoItem> {

    private final LayoutInflater inflater;

    public VideoListAdapter(Context context, ArrayList<VideoItem> videoItems) {
        super(context, 0, videoItems);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.video_list_item, parent, false);
        }

        TextView quizButton = convertView.findViewById(R.id.take_quiz);
        VideoItem videoItem = getItem(position);
        convertView.setTag(videoItem.getId());
        quizButton.setTag(videoItem.getId());
        ImageView thumbnailImageView = convertView.findViewById(R.id.thumbnailImageView);
        TextView videoTitle = convertView.findViewById(R.id.videoTitle);

        // Load and display the thumbnail using Glide (or your preferred image loading library)
        String thumbnailUrl = getThumbnailUrl(videoItem.getVideoLink());
        Glide.with(getContext()).load(thumbnailUrl).into(thumbnailImageView);

        // Set an OnClickListener on the thumbnail to open YouTube when clicked
        thumbnailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYouTubeVideo(videoItem.getVideoLink());
                if(videoItem.getVideoLink().contains("/shorts/")){
                    openYouTubeShortVideo(videoItem.getVideoLink());
                }
                else{
                    openYouTubeVideo(videoItem.getVideoLink());
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoId = (String) v.getTag();

                // Create an Intent for opening the Quiz activity
                Bundle bundle = new Bundle();
                bundle.putString("video_id", videoId);

                Intent quizIntent = new Intent(getContext(), Quiz.class);
                quizIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                quizIntent.putExtras(bundle);
                getContext().startActivity(quizIntent);
            }
        });

        // Set video title
        videoTitle.setText(videoItem.getTitle());
        videoTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYouTubeVideo(videoItem.getVideoLink());
            }
        });

        return convertView;
    }

    // Method to get the thumbnail URL
    private String getThumbnailUrl(String videoLink) {
        // Extract video ID from the video link
        String videoId = extractVideoId(videoLink);

        // Generate thumbnail URL using the extracted video ID
        return "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";


    }

    // Method to extract video ID from YouTube video link
    private String extractVideoId(String videoLink) {
        String videoId = null;
        if (videoLink != null && videoLink.trim().length() > 0) {
            String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed\\?video_id=|%2Fv%2F)[^#\\&\\?\\n]*";
            Pattern compiledPattern = Pattern.compile(pattern);
            Matcher matcher = compiledPattern.matcher(videoLink);
            if (matcher.find()) {
                videoId = matcher.group();
            }
        }
        return videoId;
    }

    // Method to open YouTube when the thumbnail is clicked
    private void openYouTubeVideo(String videoId) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoId));

        // Set the package name to ensure the YouTube app is used
        intent.setPackage("com.google.android.youtube");

        // Check if the YouTube app is installed, if not, open in a web browser
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Add FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } else {
            // If the YouTube app is not installed, open in a web browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoId));
            // Add FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }


    private void openYouTubeShortVideo(String videoId) {
        // Construct the URL for YouTube shorts video
        String shortVideoUrl = videoId;

        // Create an intent to view the YouTube short video
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shortVideoUrl));

        // Set the package name to ensure the YouTube app is used
        intent.setPackage("com.google.android.youtube");

        // Check if the YouTube app is installed, if not, open in a web browser
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Add FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } else {
            // If the YouTube app is not installed, open in a web browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(shortVideoUrl));
            // Add FLAG_ACTIVITY_NEW_TASK flag
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }
}
