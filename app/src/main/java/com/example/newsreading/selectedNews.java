package com.example.newsreading;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URLDecoder;
import java.text.Normalizer;

public class selectedNews extends AppCompatActivity {
    ImageView imageArea;
    TextView newscontent,newsdesc,newstitle;
    String title,content,image,description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_selected_news);
        Intent i =getIntent();
        image=i.getStringExtra("image");
        title=i.getStringExtra("title");
        description=i.getStringExtra("description");
        content=i.getStringExtra("content");
        try {
            content = URLDecoder.decode(content, "UTF-8");
        }catch(Exception e){
            Log.d("error",e.toString());
        }
        if(image==""|| image=="null" || image.isEmpty()){
            imageArea.setImageResource(getResources().getIdentifier("noimg","drawable",this.getPackageName()));
        }else {
            imageArea = findViewById(R.id.newsImage);
            newstitle=findViewById(R.id.title);
            newsdesc=findViewById(R.id.desc);
            newscontent=findViewById(R.id.content);
            Picasso.with(this).load(image).into(imageArea);
            newstitle.setText(title);
            newsdesc.setText(description);
            newscontent.setText(content);
            newscontent.setAutoLinkMask(Linkify.WEB_URLS);
            newscontent.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}