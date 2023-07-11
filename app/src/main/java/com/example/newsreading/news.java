package com.example.newsreading;

import static com.google.android.material.internal.ContextUtils.getActivity;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.AsyncListUtil;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.gms.location.FusedLocationProviderClient;

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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import io.github.coordinates2country.Coordinates2Country;
import io.grpc.internal.JsonParser;

public class news extends AppCompatActivity {
    JSONArray results;
    int newsno = 0;
    Boolean start=TRUE,locationGranted = FALSE,searchNews=FALSE;
    TextView searchBar;
    String nextPage,url;
    Button lo,search;
    double latitude,longitude;
    String userLocation,key;
    LinearLayout parent;


    HashMap<String, JSONObject> newsmap = new HashMap<>();
    FusedLocationProviderClient fusedlocation;
    ArrayList user, favs = new ArrayList();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DocumentReference dr = db.collection("user").document(mAuth.getUid());
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        getSupportActionBar().hide();
        fusedlocation = LocationServices.getFusedLocationProviderClient(this);
        search=findViewById(R.id.button);
        searchBar=findViewById(R.id.searchbar);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key=searchBar.getText().toString();
                if(key!="") {
                    searchNews = TRUE;
                    handleNews(null);
                }
            }
        });
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                TextView tv;
                DocumentSnapshot ss = task.getResult();
                Object userData = ss.get("uname");
                Object fav = ss.get("favs");
                user = (ArrayList) userData;
                favs = (ArrayList) fav;
                LinearLayout ll;
                ll=findViewById(R.id.ll);
                ll.removeAllViews();
                favs.addAll(new Categories().getData());
                for (int i = 0; i < favs.size(); i++) {
                    CreateElement element=new CreateElement(favs.get(i).toString());
                    RelativeLayout t=element.create(news.this);
                    t.setId(i+2000);
                    t.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleNews(v);
                        }
                    });
                    ll.addView(t);
                }
                locationPermission();
            }
        });

        lo = findViewById(R.id.logout);
        lo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent i = new Intent(getApplicationContext(), login.class);
                startActivity(i);
            }
        });

    }

    public void handleNews(View v) {
        parent = findViewById(R.id.newsArea);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View childView = parent.getChildAt(i);
            childView.setId(View.NO_ID);
        }
        newsmap.clear();
        parent.removeAllViews();
        newsno = 0;
        if(start){
            url = "https://newsdata.io/api/1/news?apikey=pub_259641873c377e643bcad2a3c456e3eda9486&language=en&country="+userLocation;
        }
        if(searchNews){
            url = "https://newsdata.io/api/1/news?apikey=pub_259641873c377e643bcad2a3c456e3eda9486&qInTitle="+key+"&language=en";
        }
                if (v!=null) {
                    RelativeLayout p=findViewById(v.getId());
                    View child=p.getChildAt(1);
                    TextView childText=findViewById(child.getId());
                    String text=childText.getText().toString();
                    if(!start && !searchNews) {
                        if (nextPage != null) {
                            url = "https://newsdata.io/api/1/news?apikey=pub_259641873c377e643bcad2a3c456e3eda9486&qInTitle=" + text+ "&language=en&page=" + nextPage;
                        } else {
                            url = "https://newsdata.io/api/1/news?apikey=pub_259641873c377e643bcad2a3c456e3eda9486&qInTitle=" + text + "&language=en";
                        }
                    }
                }else {
                        searchNews=FALSE;
                        start=FALSE;
                }
                    StringRequest req = new StringRequest(url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject res = new JSONObject(response);
                                results = res.getJSONArray("results");
                                    nextPage = res.getString("nextPage");
                                for (int i = 0; i < results.length(); i++) {
                                    try {
                                        JSONObject js = results.getJSONObject(i);
                                        handleRender(js.getString("title"), js.getString("image_url"), js);
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
                            Toast.makeText(news.this, "request error", Toast.LENGTH_SHORT).show();
                        }
                    });
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(req);
            }

    public void handleRender(String title, String image, JSONObject js) {
        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int rid = newsno;
        rl.setId(rid);
        newsmap.put(String.valueOf(rid), js);
        rl.setHorizontalGravity(Gravity.CENTER);
        param.bottomMargin = 20;
        rl.setBackgroundColor(Color.argb(100, 90, 90, 90));
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject allnews = newsmap.get(String.valueOf(v.getId()));
                    Intent i = new Intent(news.this, selectedNews.class);
                    i.putExtra("title", allnews.getString("title"));
                    i.putExtra("content", allnews.getString("content"));
                    i.putExtra("image", allnews.getString("image_url"));
                    i.putExtra("description", allnews.getString("description"));
                    startActivity(i);
                } catch (Exception e) {
                    Log.d("error getting news", e.getMessage());
                }
            }
        });
        parent.setBackgroundColor(getResources().getColor(R.color.darknavy));
        rl.setPadding(20, 30, 20, 30);


        ImageView img = new ImageView(this);
        RelativeLayout.LayoutParams imageParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageParam.width = 250;
        imageParam.height = 250;
        imageParam.rightMargin = 30;
        imageParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        img.setId(View.generateViewId());
        img.setLayoutParams(imageParam);

        TextView t = new TextView(this);
        t.setText("");
        t.setText(title);
        RelativeLayout.LayoutParams textviewParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textviewParam.width = ViewGroup.LayoutParams.MATCH_PARENT;
        textviewParam.height = ViewGroup.LayoutParams.MATCH_PARENT;
        textviewParam.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        textviewParam.addRule(RelativeLayout.RIGHT_OF, img.getId());
        t.setLayoutParams(textviewParam);

        if (image != "null" || image.isEmpty()) {
            Picasso.with(this).load(image).into(img);
        } else {
            img.setImageResource(getResources().getIdentifier("noimg", "drawable", this.getPackageName()));
        }

        rl.addView(t);
        rl.addView(img);
        rl.setLayoutParams(param);
        parent.addView(rl);
        newsno += 1;
    }

    public void locationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                locationGranted = TRUE;
                fusedlocation.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location==null){
                            Toast.makeText(news.this, "Location disabled", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            latitude = location.getLatitude();
                            longitude=location.getLongitude();
                            GetCountry gc=new GetCountry();
                            userLocation=gc.getCountry(getApplicationContext(),latitude,longitude).toLowerCase();
                            handleNews(null);
                        }}
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("location error",e.toString());
                    }
                });
            }
    } else
    {
        fusedlocation.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location==null){
                    Toast.makeText(news.this, "Location disabled", Toast.LENGTH_SHORT).show();
                }
                else {
                    latitude = location.getLatitude();
                    longitude=location.getLongitude();
                    GetCountry gc=new GetCountry();
                    userLocation=gc.getCountry(getApplicationContext(),latitude,longitude).toLowerCase();
                    handleNews(null);
                }}
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("location error",e.toString());
            }
        });
        locationGranted = TRUE;
    }
}
}