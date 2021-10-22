package com.covidcertificatechecker.greenpass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.covidcertificatechecker.greenpass.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    EditText username, email, password;
    Button signUp;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();

        signUp = findViewById(R.id.createAccountBtn);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText().toString().trim().isEmpty()){
                    username.setError("Required");
                    username.requestFocus();
                    return;
                }
                if (email.getText().toString().trim().isEmpty()){
                    email.setError("Required");
                    email.requestFocus();
                    return;
                }
                if (password.getText().toString().trim().isEmpty()){
                    password.setError("Required");
                    password.requestFocus();
                    return;
                }
                Common.pd = new ProgressDialog(SignUpActivity.this);
                Common.pd.setMessage("Signing Up...");
                Common.pd.show();

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    User mUser = new User();
                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users");
                                    mUser.setId(user.getUid());
                                    mUser.setName(username.getText().toString());
                                    mUser.setEmail(email.getText().toString());

                                    dbRef.child(mUser.getId()).setValue(mUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SignUpActivity.this, "Account Created Successfully.", Toast.LENGTH_SHORT).show();
                                                FirebaseAuth.getInstance().signOut();
                                                finish();
                                            }else{
                                                Common.pd.cancel();
                                                Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Common.pd.cancel();
                                    Toast.makeText(SignUpActivity.this, "Authentication Error: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });

    }
}