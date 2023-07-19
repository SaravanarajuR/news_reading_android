package com.example.newsreading;

import android.app.Application;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CreateElement extends Application {
    ImageView t;
    TextView tv;
    public LinearLayout parent;
    news c;

    CreateElement(news c) {
        this.c=c;
    }

    public LinearLayout create(String search,String url){
        FrameLayout.LayoutParams rl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        t = new ImageView(c);
        rl.width = 160;
        rl.height = 160;
        t.setScaleType(ImageView.ScaleType.FIT_XY);
        Picasso.with(c).load(Uri.parse(url)).into(t);

        CardView back=new CardView(c);
        back.setRadius(160);
        back.addView(t);

        LinearLayout.LayoutParams textview = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv = new TextView(c);
        tv.setText(search);
        tv.setTextSize(14);
        tv.setId(View.generateViewId());
        textview.topMargin = 10;
        tv.setTextColor(Color.WHITE);
        Typeface tf = ResourcesCompat.getFont(c.getApplicationContext(), R.font.gabriela);
        tv.setTypeface(tf);
        textview.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        textview.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        tv.setLayoutParams(textview);

        LinearLayout.LayoutParams parentParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        parent = new LinearLayout(c);
        parent.setGravity(Gravity.CENTER);
        parent.addView(back);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.addView(tv);
        parentParams.leftMargin = 10;
        parentParams.rightMargin = 10;
        parent.setGravity(Gravity.CENTER);
        parentParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        parentParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        parent.setLayoutParams(parentParams);
        t.setLayoutParams(rl);
        return parent;
    }

}
