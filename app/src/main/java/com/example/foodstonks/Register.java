package com.example.foodstonks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

import java.util.Arrays;

public class Register extends AppCompatActivity {

    public EditText Email, Pwd;
    public Button register ,log_gmail;;
    public String userID;
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;


    int RC_SIGN_IN = 120;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Register.this, Home.class));
            finish();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        log_gmail  = (Button) findViewById(R.id.reg_gmail);
        log_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        Email = (EditText) findViewById(R.id.email);
        Pwd = (EditText) findViewById(R.id.pwd);
        register = (Button) findViewById(R.id.reg_email);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Email.getText().toString().trim();
                String pwd = Pwd.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Entrer une adresse Email !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(getApplicationContext(), "Entrer un mot de passe !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pwd.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Mot de passe trop court, entrer un minimum de 6 caracteres !", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(Register.this, "Creation succes" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(Register.this, Post_Register.class));
                                    finish();
                                }
                            }
                        });
            }
        });


    }
    public void onClick(View v) {
        Intent start_Login_page= new Intent(Register.this,Login.class);
        startActivity(start_Login_page);
        finish();
    }
    @Override
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
                                startActivity(new Intent(Register.this, Post_Register.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Compte existant ! redirection vers votre profile.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Home.class));
                                finish();
                            }
                        } else {

                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }



    public void reg_fcb(View v){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .build(),RC_SIGN_IN);
    }




}