package com.google.firebase.quickstart.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TwitterLogin extends BaseActivity
        implements View.OnClickListener {

    private static final String TAG = "TwitterLogin";
    private TextView mStatusTextView;
    private FirebaseAuth mAuth;
    private TwitterLoginButton mLoginButton;

    String sourcelanguage;
    String targetlanguage;
    String sourcetxt;
    TextView outputTextView;

    private TextView team_no;
    private TextView mem_1;
    private TextView mem_2;

    int GET_FROM_GALLERY  = 1;
    ImageView user_Image ;

    private FirebaseDatabase fd_base = FirebaseDatabase.getInstance();
    private DatabaseReference root_dbr = fd_base.getReference();
    private DatabaseReference child1_dbr = root_dbr.child("Team");
    private DatabaseReference child2_dbr = root_dbr.child("Member1");
    private DatabaseReference child3_dbr = root_dbr.child("Member2");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();
        Twitter.initialize(twitterConfig);
        setContentView(R.layout.activity_twitter);

        mStatusTextView = findViewById(R.id.status);
        outputTextView = (TextView) findViewById(R.id.tgt_txt);
        team_no = (TextView) findViewById(R.id.team_val);
        mem_1 = (TextView) findViewById(R.id.mem1_val);
        mem_2 = (TextView) findViewById(R.id.mem2_val);
        user_Image = (ImageView) findViewById(R.id.image_view);
        findViewById(R.id.button_twitter_signout).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mLoginButton = findViewById(R.id.button_twitter_login);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
                updateUI(null);
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        child1_dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message_1 = dataSnapshot.getValue(String.class);
                team_no.setText(message_1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        child2_dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message_2 = dataSnapshot.getValue(String.class);
                mem_1.setText(message_2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        child3_dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String message_3 = dataSnapshot.getValue(String.class);
                mem_2.setText(message_3);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLoginButton.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri url = data.getData();
            user_Image.setImageURI(url);
            Log.d("GalleryDemo", "Pic saved");
        }
    }

    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        showProgressDialog();
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(TwitterLogin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressDialog();

                    }
                });
    }


    private void signOut() {
        mAuth.signOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();

        updateUI(null);
    }


    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.twitter_status_fmt, user.getDisplayName()));
            findViewById(R.id.button_twitter_login).setVisibility(View.GONE);
            findViewById(R.id.button_twitter_signout).setVisibility(View.VISIBLE);

            findViewById(R.id.head1).setVisibility(View.VISIBLE);
            findViewById(R.id.head2).setVisibility(View.VISIBLE);
            findViewById(R.id.head3).setVisibility(View.VISIBLE);
            findViewById(R.id.team_no).setVisibility(View.VISIBLE);
            findViewById(R.id.mem_label).setVisibility(View.VISIBLE);
            findViewById(R.id.upload_button).setVisibility(View.VISIBLE);
            findViewById(R.id.translate_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.src_lang).setVisibility(View.VISIBLE);
            findViewById(R.id.tgt_lang).setVisibility(View.VISIBLE);
            findViewById(R.id.src_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.tgt_txt).setVisibility(View.VISIBLE);
            findViewById(R.id.image_view).setVisibility(View.VISIBLE);
            findViewById(R.id.team_val).setVisibility(View.VISIBLE);
            findViewById(R.id.mem1_val).setVisibility(View.VISIBLE);
            findViewById(R.id.mem2_val).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            findViewById(R.id.button_twitter_login).setVisibility(View.VISIBLE);
            findViewById(R.id.button_twitter_signout).setVisibility(View.GONE);

            findViewById(R.id.head1).setVisibility(View.GONE);
            findViewById(R.id.head2).setVisibility(View.GONE);
            findViewById(R.id.head3).setVisibility(View.GONE);
            findViewById(R.id.team_no).setVisibility(View.GONE);
            findViewById(R.id.mem_label).setVisibility(View.GONE);
            findViewById(R.id.upload_button).setVisibility(View.GONE);
            findViewById(R.id.translate_txt).setVisibility(View.GONE);
            findViewById(R.id.src_lang).setVisibility(View.GONE);
            findViewById(R.id.tgt_lang).setVisibility(View.GONE);
            findViewById(R.id.src_txt).setVisibility(View.GONE);
            findViewById(R.id.tgt_txt).setVisibility(View.GONE);
            findViewById(R.id.image_view).setVisibility(View.GONE);
            findViewById(R.id.team_val).setVisibility(View.GONE);
            findViewById(R.id.mem1_val).setVisibility(View.GONE);
            findViewById(R.id.mem2_val).setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_twitter_signout) {
            signOut();
        }
    }

    public void get_text(View v) {

        EditText sourcelang = (EditText) findViewById(R.id.src_lang);
        EditText targetlang = (EditText) findViewById(R.id.tgt_lang);
        EditText sourcetext = (EditText) findViewById(R.id.src_txt);
        sourcelanguage = sourcelang.getText().toString();
        targetlanguage = targetlang.getText().toString();
        sourcetxt = sourcetext.getText().toString();
        String getURL = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20180206T231433Z.3ec06fb4d394d903.07e9547f7d03a98174830ddd8024a84584e6b1ad&text=\"" + sourcetxt + "\"&lang=" + sourcelanguage + "-" + targetlanguage + "&[format=plain]&[options=1]&[callback=set]";//The API service URL
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(getURL)
                    .build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
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

    public void get_gallery(View v){

        Intent cameraIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(cameraIntent, GET_FROM_GALLERY);
    }

}
