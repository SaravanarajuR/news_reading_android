package com.example.newsreading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class intro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        HashMap<String, String> urls = new HashMap<>();
        getSupportActionBar().hide();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String user=mAuth.getUid();
        if (user != null) {
            DocumentReference dr = db.collection("user").document(mAuth.getUid());
            dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    TextView tv;
                    ArrayList favs = new ArrayList();
                    DocumentSnapshot ss = task.getResult();
                    if (ss.get("favs") != null) {
                        Object data = ss.get("userdata");
                        Object fav = ss.get("favs");
                        ArrayList dataArray=(ArrayList)data ;
                        String uname=dataArray.get(0).toString();
                        String plan = dataArray.get(1).toString();
                        favs = (ArrayList) fav;
                        favs.addAll(new Categories().getData());
                        final int size = favs.size() - 1;
                        for (int i = 0; i < favs.size(); i++) {
                            Object toSearch = favs.get(i);
                            String url = "https://api.pexels.com/v1/search?query=" + favs.get(i).toString() + "&page=1&per_page=1&size=small&orientation=potrait";
                            String key = "mfFcHNlUJjFDr3m2EtHO4LWZI0Dc2SYrsNa1H0AVEVIBM9E0s8vvxzAU";
                            boolean last = i == size;
                            JsonObjectRequest js = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONArray photos = new JSONArray(response.getString("photos"));
                                        JSONObject js = new JSONObject(photos.getString(0));
                                        JSONObject src = new JSONObject(js.getString("src"));
                                        String image = src.getString("small");
                                        urls.put(String.valueOf(toSearch), image);
                                        if (last) {
                                            Log.d("favs", urls.toString());
                                            Intent i = new Intent(getApplicationContext(), news.class);
                                            i.putExtra("urls", urls);
                                            i.putExtra("uname", uname);
                                            i.putExtra("plan", plan);
                                            startActivity(i);
                                            finish();
                                        }
                                    } catch (Exception e) {
                                        Log.d("error", e.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("Error", "Error getting fb data");
                                }
                            }) {
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("authorization", key);

                                    return params;
                                }
                            };
                            RequestQueue rs = Volley.newRequestQueue(getApplicationContext());
                            rs.add(js);
                        }
                    } else {
                        Intent i = new Intent(getApplicationContext(), chooseInterest.class);
                        startActivity(i);
                        finish();
                    }
                }
            });
        } else {
            Intent i = new Intent(getApplicationContext(), login.class);
            startActivity(i);
            finish();
        }
    }
}