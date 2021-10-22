package com.covidcertificatechecker.greenpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText email, pass;
    TextView forgotPass;
    Button login, register;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        forgotPass = findViewById(R.id.forgotPasswordText);
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerBtn);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim().isEmpty()){
                    email.setError("Required");
                    email.requestFocus();
                    return;
                }
                if (pass.getText().toString().trim().isEmpty()){
                    pass.setError("Required");
                    pass.requestFocus();
                    return;
                }
                Common.pd = new ProgressDialog(LoginActivity.this);
                Common.pd.setMessage("Signing in...");
                Common.pd.show();
                mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (user != null){
                                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");

                                        dbRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                Common.saveValues(LoginActivity.this, "username", snapshot.child("name").getValue(String.class));
                                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                startActivity(mainIntent);
                                                finish();
                                                Common.pd.cancel();
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Common.pd.cancel();
                                            }
                                        });
                                    }


                                } else {
                                    Common.pd.cancel();
                                    Toast.makeText(LoginActivity.this, "Authentication failed. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(it);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(it);
            }
        });
    }
}