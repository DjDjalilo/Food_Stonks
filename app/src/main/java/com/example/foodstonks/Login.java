package com.example.foodstonks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    int RC_SIGN_IN = 10;
    public Button login,log_gmail;
    public EditText Email, Pwd;
    public FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        log_gmail  = (Button) findViewById(R.id.reg_gmail2);
        log_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        Email = (EditText) findViewById(R.id.email_log);
        Pwd = (EditText) findViewById(R.id.pwd_log);
        login = (Button) findViewById(R.id.log_email);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString();
                String password = Pwd.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    if (password.length() < 6) {
                                        Pwd.setError(getString(R.string.minimum_pwd));
                                    } else {
                                        Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(Login.this, Home.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }
    public void onClick(View v) {
        Intent start_Login_page= new Intent(Login.this,Register.class);
        startActivity(start_Login_page);
        finish();

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");
                            if (isNewUser) {
                                Toast.makeText(getApplicationContext(), "Compte inexistant ! redirection vers l'inscription du compte.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Login.this, Post_Register.class));
                                finish();
                            } else {
                                startActivity(new Intent(Login.this, Home.class));
                                finish();

                            }
                        } else {

                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}