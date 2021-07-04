package com.example.trackme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumber extends AppCompatActivity {
    public static final String VER_ID = "Verification_ID";
    private TextView phoneNumber, code;

    private FirebaseAuth authInstance;
    private  Button genOtp;
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = findViewById(R.id.phno);
        code=findViewById(R.id.code);

        genOtp = findViewById(R.id.but1);
         authInstance = FirebaseAuth.getInstance();
        if(authInstance.getCurrentUser()!=null){
            System.out.println(authInstance.getCurrentUser().toString()+"______________________________________");
        }else{
            System.out.println("No");
        }


        mCallBack = getCallback();

        setOnclick();

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getCallback() {

        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(PhoneNumber.this, "yes", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(PhoneNumber.this, e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(String veriId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(veriId, forceResendingToken);
                Intent intent = new Intent(PhoneNumber.this, Otp_verification.class);
                intent.putExtra(VER_ID, veriId);
                startActivity(intent);


            }
        };
    }

    private void setOnclick() {

        genOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inCode=code.getText().toString().trim();
                String phn = phoneNumber.getText().toString().trim();
              phn="+"+inCode+phn;
              if(phn==null){
                  Toast.makeText(getApplicationContext(),"Please enter details",Toast.LENGTH_LONG).show();
              }
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(authInstance)
                                .setPhoneNumber(phn)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(PhoneNumber.this)
                                .setCallbacks(mCallBack)
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
    }
}