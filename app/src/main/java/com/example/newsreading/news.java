package com.example.newsreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListUtil;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.util.Pair;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import java.net.URI;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import io.grpc.internal.JsonParser;

public class news extends AppCompatActivity {
    JSONArray results;
    int newsno=0;
    int prevId=0;
    String nextPage;
    LinearLayout parent;
    String url;
    HashMap<String,JSONObject> newsmap=new HashMap<>();
    int page=1;
    ArrayList user, favs = new ArrayList();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocumentReference dr = db.collection("user").document(mAuth.getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().hide();
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                TextView tv;
                DocumentSnapshot ss = task.getResult();
                int head[] = {R.id.head1, R.id.head2, R.id.head3};
                Object userData = ss.get("uname");
                Object fav = ss.get("favs");
                user = (ArrayList) userData;
                favs = (ArrayList) fav;
                for (int i = 0; i < favs.size(); i++) {
                    tv = findViewById(head[i]);
                    tv.setText(favs.get(i).toString());
                }
            }
        });
    }

    public void handleNews(View v) {
        int head[] = {R.id.head1, R.id.head2, R.id.head3};
        parent=findViewById(R.id.newsArea);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            childView.setId(View.NO_ID);
        }
        newsmap.clear();
        parent.removeAllViews();
        newsno=0;
        for (int i = 0; i < favs.size(); i++) {
            if (v.getId() == head[i]) {
                if(nextPage!=null) {
                    url = "https://newsdata.io/api/1/news?apikey=pub_259641873c377e643bcad2a3c456e3eda9486&q=" + favs.get(i).toString() + "&language=en&page="+nextPage;
                }else{
                    url = "https://newsdata.io/api/1/news?apikey=pub_259641873c377e643bcad2a3c456e3eda9486&q=" + favs.get(i).toString() + "&language=en";
                }
                StringRequest req = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            results = res.getJSONArray("results");
                            nextPage=res.getString("nextPage");
                            for (int i = 0; i < results.length(); i++) {
                                try {
                                    JSONObject js=results.getJSONObject(i);
                                    handleRender(js.getString("title"),js.getString("image_url"),js);
                                   } catch (Exception e) {
                                    Log.d("error", e.toString());
                                }
                            }
                        } catch (Exception e) {
                            Log.d("error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(news.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(req);
            }
        }
    }

    public void handleRender(String title,String image,JSONObject js){
            RelativeLayout rl=new RelativeLayout(this);
            RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            int rid=newsno;
            rl.setId(rid);
            newsmap.put(String.valueOf(rid),js);
            rl.setHorizontalGravity(Gravity.CENTER);
            param.bottomMargin=20;
            rl.setBackgroundColor(Color.argb(100,90,90,90));
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject allnews = newsmap.get(String.valueOf(v.getId()));
                        Intent i=new Intent(news.this,selectedNews.class);
                        i.putExtra("title",allnews.getString("title"));
                        i.putExtra("content",allnews.getString("content"));
                        i.putExtra("image",allnews.getString("image_url"));
                        i.putExtra("description",allnews.getString("description"));
                        startActivity(i);
                    }catch(Exception e){
                        Log.d("error getting news",e.getMessage());
                    }
                }
            });
            parent.setBackgroundColor(Color.argb(100,0,0,0));
            rl.setPadding(20,30,20,30);


            ImageView img = new ImageView(this);
            RelativeLayout.LayoutParams imageParam=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            imageParam.width=250;
            imageParam.height=250;
            imageParam.rightMargin=30;
            imageParam.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
            img.setId(View.generateViewId());
            img.setLayoutParams(imageParam);

            TextView t=new TextView(this);
            t.setText("");
            t.setText(title);
            RelativeLayout.LayoutParams textviewParam=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            textviewParam.width=ViewGroup.LayoutParams.MATCH_PARENT;
            textviewParam.height=ViewGroup.LayoutParams.MATCH_PARENT;
            textviewParam.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);
            textviewParam.addRule(RelativeLayout.RIGHT_OF,img.getId());
            t.setLayoutParams(textviewParam);

            if(image!="null" || image.isEmpty()) {
                Picasso.with(this).load(image).into(img);
            }else{
                img.setImageResource(getResources().getIdentifier("noimg","drawable",this.getPackageName()));
            }

            rl.addView(t);
            rl.addView(img);
            rl.setLayoutParams(param);
            parent.addView(rl);
            newsno+=1;
    }

    public void handleSelected(int newsIndex){
    }
}