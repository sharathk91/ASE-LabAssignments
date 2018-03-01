/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.firebase.quickstart.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirebaseLogin extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private TextView mStatusTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    public FirebaseAuth mAuth;

    private TextView teamno;
    private TextView mem1;
    private TextView mem2;

    String sourcelanguage;
    String targetlanguage;
    String sourcetxt;
    TextView outputTextView;

    int GET_FROM_GALLERY  = 1;
    ImageView userImage ;

    private FirebaseDatabase fdb = FirebaseDatabase.getInstance();
    private DatabaseReference rootdbr = fdb.getReference();
    private DatabaseReference child1dbr = rootdbr.child("Team");
    private DatabaseReference child2dbr = rootdbr.child("Member1");
    private DatabaseReference child3dbr = rootdbr.child("Member2");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);


        mStatusTextView = findViewById(R.id.status);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        teamno = (TextView) findViewById(R.id.teamnoval);
        mem1 = (TextView) findViewById(R.id.mem1val);
        mem2 = (TextView) findViewById(R.id.mem2val);
        outputTextView = (TextView) findViewById(R.id.tgttext);
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);


        userImage = (ImageView) findViewById(R.id.image);

        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        child1dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                  String message = dataSnapshot.getValue(String.class);
                  teamno.setText(message);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        child2dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message1 = dataSnapshot.getValue(String.class);
                mem1.setText(message1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        child3dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message2 = dataSnapshot.getValue(String.class);
                mem2.setText(message2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                        hideProgressDialog();

                    }
                });

    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FirebaseLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }


                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }

                    }
                });

    }

    public void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {

        findViewById(R.id.verify_email_button).setEnabled(false);


        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(FirebaseLogin.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(FirebaseLogin.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    public void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            //mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.verify_email_button).setEnabled(!user.isEmailVerified());

            teamno.setVisibility(View.VISIBLE);
            mem1.setVisibility(View.VISIBLE);
            mem2.setVisibility(View.VISIBLE);

            findViewById(R.id.feat2).setVisibility(View.VISIBLE);
            findViewById(R.id.image).setVisibility(View.VISIBLE);
            findViewById(R.id.upload).setVisibility(View.VISIBLE);

            findViewById(R.id.teamno).setVisibility(View.VISIBLE);
            findViewById(R.id.mem1).setVisibility(View.VISIBLE);
            //findViewById(R.id.mem2).setVisibility(View.VISIBLE);
            findViewById(R.id.rtstore).setVisibility(View.VISIBLE);

            findViewById(R.id.feat3).setVisibility(View.VISIBLE);
            findViewById(R.id.srclang).setVisibility(View.VISIBLE);
            findViewById(R.id.tgtlang).setVisibility(View.VISIBLE);
            findViewById(R.id.srctxt).setVisibility(View.VISIBLE);
            //findViewById(R.id.tgttext).setVisibility(View.VISIBLE);
            findViewById(R.id.convert).setVisibility(View.VISIBLE);

        } else {
            mStatusTextView.setText(R.string.signed_out);
            //mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);

            teamno.setVisibility(View.INVISIBLE);
            mem1.setVisibility(View.INVISIBLE);
            mem2.setVisibility(View.INVISIBLE);

            findViewById(R.id.feat2).setVisibility(View.INVISIBLE);
            findViewById(R.id.image).setVisibility(View.INVISIBLE);
            findViewById(R.id.upload).setVisibility(View.INVISIBLE);

            findViewById(R.id.teamno).setVisibility(View.INVISIBLE);
            findViewById(R.id.mem1).setVisibility(View.INVISIBLE);
            //findViewById(R.id.mem2).setVisibility(View.INVISIBLE);
            findViewById(R.id.rtstore).setVisibility(View.INVISIBLE);

            findViewById(R.id.feat3).setVisibility(View.INVISIBLE);
            findViewById(R.id.srclang).setVisibility(View.INVISIBLE);
            findViewById(R.id.tgtlang).setVisibility(View.INVISIBLE);
            findViewById(R.id.srctxt).setVisibility(View.INVISIBLE);
            findViewById(R.id.tgttext).setVisibility(View.INVISIBLE);
            findViewById(R.id.convert).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
    }

    public void getGallery(View v){

        Intent cameraIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(cameraIntent, GET_FROM_GALLERY);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri ur = data.getData();
            userImage.setImageURI(ur);
            Log.d("GalleryDemo", "Pic saved");
        }
    }

    public void convert(View v) {

        EditText sourcelang = (EditText) findViewById(R.id.srclang);
        EditText targetlang = (EditText) findViewById(R.id.tgtlang);
        EditText sourcetext = (EditText) findViewById(R.id.srctxt);


        sourcelanguage = sourcelang.getText().toString();
        targetlanguage = targetlang.getText().toString();
        sourcetxt = sourcetext.getText().toString();
        String getURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20180206T231433Z.3ec06fb4d394d903.07e9547f7d03a98174830ddd8024a84584e6b1ad&text=\"" + sourcetxt + "\"&lang=" + sourcelanguage + "-" + targetlanguage + "&[format=plain]&[options=1]&[callback=set]";//The API service URL


        //final String response1 = "";
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(getURL)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final JSONObject jsonResult;
                    final String result = response.body().string();
                    try {
                        jsonResult = new JSONObject(result);
                        JSONArray convertedTextArray = jsonResult.getJSONArray("text");
                        final String convertedText = convertedTextArray.get(0).toString();
                        Log.d("okHttp", jsonResult.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                outputTextView.setVisibility(View.VISIBLE);
                                outputTextView.setText(convertedText);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        } catch (Exception ex) {
            outputTextView.setText(ex.getMessage());

        }


    }
}
