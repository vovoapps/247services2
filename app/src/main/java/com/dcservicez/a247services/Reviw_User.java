package com.dcservicez.a247services;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.dcservicez.a247services.Prefs.Prefs;
import com.dcservicez.a247services.objects.Review;
import com.dcservicez.a247services.objects.Review_item;
import com.google.firebase.database.FirebaseDatabase;

public class Reviw_User extends AppCompatActivity {
    Prefs prefs;
    String id;
    boolean isSP=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviw__user);
        prefs=new Prefs(this);

        id=getIntent().getExtras().getString("user_id");
        isSP=getIntent().getExtras().getBoolean("isSP");
    }



    public void click_done(View view){
        try {
            int rate=(int)((RatingBar)findViewById(R.id.review_ratingbar)).getRating();
            String review=((EditText)findViewById(R.id.review_edtxt)).getText().toString();
            Review review1=new Review(rate,review);

            if(isSP){
                FirebaseDatabase.getInstance().getReference("Users").child(id).child("service").child("reviews").child(prefs.email()).setValue(review1);
            }else{
                FirebaseDatabase.getInstance().getReference("Users").child(id).child("reviews").child(prefs.email()).setValue(review1);

            }

            finish();
        } catch (Exception e) {
            e.printStackTrace();
//
//
//
        }


    }
}
