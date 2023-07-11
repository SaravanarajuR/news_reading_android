package com.example.newsreading;

import android.app.Application;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;
import io.grpc.internal.SharedResourceHolder;

public class CreateElement extends Application {
    ImageView t;
    TextView tv;
    String text;
    RelativeLayout parent;
    String imageURI;
    news c;

    CreateElement(String text) {
        this.text = text;
    }

    public RelativeLayout create(news c) {
        this.c = c;
        Uri uri = getImage();
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        t = new ImageView(c);
        rl.width = 400;
        rl.height = 400;
        rl.bottomMargin = 40;
        Picasso.with(c).load(uri).into(t);
        t.setPadding(20, 20, 20, 20);
        rl.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);

        RelativeLayout.LayoutParams textview = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv = new TextView(c);
        tv.setText(text);
        textview.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        tv.setTextSize(18);
        tv.setId(View.generateViewId());
        textview.bottomMargin = 10;
        textview.topMargin = 10;
        tv.setTextColor(Color.WHITE);
        Typeface tf = ResourcesCompat.getFont(c.getApplicationContext(), R.font.gabriela);
        tv.setTypeface(tf);
        textview.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        textview.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        textview.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        tv.setLayoutParams(textview);

        RelativeLayout.LayoutParams parentParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        parent = new RelativeLayout(c);
        parent.setGravity(Gravity.CENTER);
        parent.addView(t);
        parent.addView(tv);
        parentParams.leftMargin = 10;
        parentParams.rightMargin = 10;
        parent.setGravity(Gravity.CENTER);
        parentParams.width = 360;
        parentParams.height = 340;
        parent.setLayoutParams(parentParams);
        parent.setBackgroundColor(Color.argb(50, 255, 255, 255));
        t.setLayoutParams(rl);
        return parent;
    }

    public Uri getImage() {
        JsonObjectRequest req = new JsonObjectRequest("https://api.pexels.com/v1/search?query=" + text + "&per_page=1&size=small&orientation=square", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray photos = new JSONArray(response.getString("photos"));
                    JSONObject url = new JSONObject(photos.getString(0));
                    JSONObject src = new JSONObject(url.getString("src"));
                    imageURI = src.getString("medium");
                } catch (Exception e) {
                    Log.d("exception", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "Pexels error");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authorization", "mfFcHNlUJjFDr3m2EtHO4LWZI0Dc2SYrsNa1H0AVEVIBM9E0s8vvxzAU");
                return params;
            }
        };
        RequestQueue r = Volley.newRequestQueue(c.getApplicationContext());
        r.add(req);
        return Uri.parse(imageURI);
    }
}
