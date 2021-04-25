package com.example.foodstonks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Post_Register extends AppCompatActivity{

    EditText Age;
    EditText Poids;
    Button save;
    TextView tvSelectedInto;
    Button btnSelectInto;
    String[] listInto;
    boolean[] checkedInto;
    ArrayList<Integer> mUserInto = new ArrayList<>();
    RadioGroup rg;
    RadioButton rb;
    int sex = 1;// 1 = homme , 0 = femme
    FirebaseFirestore db;
    String userID;
    Spinner spinner;
    FirebaseAuth auth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post__register);
        getSupportActionBar().hide();
        spinner = findViewById(R.id.regimes);
        db = FirebaseFirestore.getInstance();
        rg = findViewById(R.id.rgroup);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.regimes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        btnSelectInto = (Button) findViewById(R.id.btn_into);
        tvSelectedInto = (TextView) findViewById(R.id.list_into);

        listInto = getResources().getStringArray(R.array.into_list);
        checkedInto = new boolean[listInto.length];

        btnSelectInto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Post_Register.this);
                mBuilder.setTitle(R.string.ah);
                mBuilder.setMultiChoiceItems(listInto, checkedInto, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                        if(isChecked){
                            mUserInto.add(position);
                        }else{
                            mUserInto.remove((Integer.valueOf(position)));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String item = "";
                        for (int i = 0; i < mUserInto.size(); i++) {
                            item = item + listInto[mUserInto.get(i)];
                            if (i != mUserInto.size() - 1) {
                                item = item + ", ";
                            }
                        }
                        tvSelectedInto.setText(item);
                    }
                });

                mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        for (int i = 0; i < checkedInto.length; i++) {
                            checkedInto[i] = false;
                            mUserInto.clear();
                            tvSelectedInto.setText("");
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


    }

    public void rbClick (View v){
        int rbid = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(rbid);
        if(rb.getText().toString() == "HOMME")
        {
            sex = 1;
        }
        else
        {
            sex = 0;
        }

    }
    public  void save (View v){
        Age = (EditText) findViewById(R.id.age);
        Poids = (EditText) findViewById(R.id.poids);
        userID = auth.getCurrentUser().getUid();
        String intolist = tvSelectedInto.getText().toString();
        int age = Integer.parseInt(Age.getText().toString());
        int poids = Integer.parseInt(Poids.getText().toString());
        String regime = spinner.getSelectedItem().toString();
        DocumentReference df = db.collection("users").document(userID);
        Map<String,Object> user = new HashMap<>();
        user.put("age",age);
        user.put("poids",poids);
        user.put("gender",sex);
        user.put("regime",regime);
        user.put("intos",intolist);

        df.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {


            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","Succés: user créé");
            }
        });
        startActivity(new Intent(Post_Register.this, Home.class));
        finish();



    }
    public void cancel(View v)
    {

        if(v.getId() == R.id.cancel){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.delete().addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(Post_Register.this, Register.class));
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}