package com.example.newsreading;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class chooseInterest extends AppCompatActivity {
    Button c,f;
    CardView c1;
    EditText uname;
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    Map<String, List> user=new HashMap<>();
    Set<String> favs=new HashSet<>();

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_choose_interest);
        mAuth = FirebaseAuth.getInstance();
        c1 = findViewById(R.id.card1);
        RequestQueue r = Volley.newRequestQueue(this);
        f = findViewById(R.id.finish);
        uname = findViewById(R.id.uname);
        DocumentReference data = db.collection("user").document(mAuth.getUid());

            f.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uname.getText().toString() == "") {
                        Toast.makeText(chooseInterest.this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        ArrayList<String> username = new ArrayList<String>();
                        ArrayList<String> userfavs = new ArrayList<String>();
                        userfavs.addAll(favs);
                        username.add("freemium");
                        if (favs.size() < 2) {
                            Toast.makeText(chooseInterest.this, "Choose atleast 2 favourites", Toast.LENGTH_SHORT).show();
                        } else {
                            username.add(uname.getText().toString());
                            user.put("favs", userfavs);
                            user.put("userdata",username);
                            DocumentReference dr=db.collection("user").document(mAuth.getUid());
                            dr.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                                    redirect();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(chooseInterest.this, "failed,Retry", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            });
        }

    public void redirect() {
                Intent i=new Intent(getApplicationContext(),intro.class);
                startActivity(i);
                finish();
    }

    public void handleFavs(View v){
        String categories[]={"sports","politics","weather","technology","crime","educational"};
        int id[]={R.id.card1,R.id.card2,R.id.card3,R.id.card4,R.id.card5,R.id.card6};
        int color[]={R.id.text1,R.id.text2,R.id.text3,R.id.text4,R.id.text5,R.id.text6};
        TextView tv;
        for(int i=0;i<6;i++){
                    if(v.getId()==id[i]){
                        if(favs.contains(categories[i])){
                            favs.remove(categories[i]);
                            tv=findViewById(color[i]);
                            tv.setBackgroundColor(getResources().getColor(R.color.red));
                            tv.setTextColor(getResources().getColor(R.color.white));
                        }else{
                            favs.add(categories[i]);
                            tv=findViewById(color[i]);
                            tv.setBackgroundColor(getResources().getColor(R.color.green));
                            tv.setTextColor(getResources().getColor(R.color.black));
                        }
                    }
            }
        }
    public ColorFilter createGrayscaleColorFilter() {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0); // Set saturation to 0 for grayscale
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        return colorFilter;
    }
}