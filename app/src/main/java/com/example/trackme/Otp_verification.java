package com.example.trackme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.trackme.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Otp_verification extends AppCompatActivity {
    public static final String SIGNAL ="hi" ;
    private FirebaseAuth AutObj;
    private String verification_id;
    private EditText otp_tv;
    private Button Verify_but;
    private PhoneAuthCredential credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        otp_tv=findViewById(R.id.otp_code);
        Verify_but=findViewById(R.id.verify_but);
        AutObj=FirebaseAuth.getInstance();

        Onstart();
Toast.makeText(getApplicationContext(),verification_id,Toast.LENGTH_LONG).show();
setOnClick();


    }
//click event
    private void setOnClick() {
        Verify_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp=otp_tv.getText().toString();
                credentials= PhoneAuthProvider.getCredential(verification_id,otp);
                SignInWithCredential(credentials);
            }
        });
    }

    private void SignInWithCredential(PhoneAuthCredential credentials) {
AutObj.signInWithCredential(credentials)
      .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                StoreOnFirebase();
            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
          tost(e.getMessage().toString());
            }
        });

    }

    private void StoreOnFirebase() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("/user/"+AutObj.getUid());
        User user=new User(AutObj.getUid(),AutObj.getCurrentUser().getPhoneNumber());
        ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
                Intent intent=new Intent(getBaseContext(),MapsActivity.class);
                finish();
                startActivity(intent);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( Exception e) {
                        Toast.makeText(getBaseContext(),e.getMessage().toString()+"kkk",Toast.LENGTH_SHORT);
                    }
                });



    }


    private void tost(String error) {
        Toast.makeText(getApplicationContext(),error,Toast.LENGTH_LONG).show();
    }

    private void Onstart() {

        Intent intent=getIntent();
        verification_id=intent.getStringExtra(PhoneNumber.VER_ID);
    }
}