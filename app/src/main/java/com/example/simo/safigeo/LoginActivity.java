package com.example.simo.safigeo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.nio.file.Files;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    static final int GOOGLE_SIGN = 123;
    GoogleSignInClient mGoogleSignInClient;
    Button mbtnGoogleConnect;

    private FirebaseAuth mAuth;

    public CallbackManager mCallbackManager;

    Button mbtnFaceBookLogin ;

    final public String fbTAG = "fbTAG";
    final public String GoogleTAG = "GoogleTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        /* start google auth */
        mbtnGoogleConnect = (Button) findViewById(R.id.googleConnect);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        mbtnGoogleConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInGoogle();
            }
        });

        /* end google auth */

        /* start facebook auth*/
        mCallbackManager = CallbackManager.Factory.create();


        mbtnFaceBookLogin = (Button)findViewById(R.id.facebookConnect) ;
        mbtnFaceBookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(fbTAG, "facebook:onSuccess:" + loginResult);
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(fbTAG, "facebook:onCancel");

                    }

                    @Override
                    public void onError(FacebookException error) {

                        Log.d(fbTAG, "facebook:onError", error);

                    }
                });
            }
        });

        /* end facebook auth*/

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null)
        {
            Toast.makeText(this, "your are logedd in : " + currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(getApplicationContext(),MainActivity.class));
            startActivity(new Intent(getApplicationContext(),PermissionsActivity.class));
            finish();

        }else
        {
            Toast.makeText(this, "Err i n loging in : ", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK

        /* start google auth */
        if(requestCode == GOOGLE_SIGN)
        {
            Task<GoogleSignInAccount> task  = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            try{

                GoogleSignInAccount account = task.getResult(ApiException.class);
                account.getDisplayName();
                Log.d("DiplayTAG", "onActivityResult: display name :" + account.getDisplayName() + " FamilyName " + account.getFamilyName()+
                        " getGivenName() " + account.getGivenName() +" photo uri " + account.getPhotoUrl()  );
                if( account != null) fireBaseAuthWithGoogle(account);

            }catch (ApiException e)
            {
                e.printStackTrace();
            }
        }
        /* end google auth */
        else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);

        }
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(fbTAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(fbTAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(fbTAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    void SignInGoogle()
    {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);

    }

    void fireBaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(GoogleTAG,"fireBaseAuthWithGoogle" + account.getId());

        AuthCredential credential  = GoogleAuthProvider
                .getCredential(account.getIdToken(),null);

/*
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this , task -> {
                    if(task.isSuccessful())
                    {
                        Log.d(GoogleTAG, "google login secces");
                        FirebaseUser user = mAuth.getCurrentUser();

                        updateUI(user);
                    }else {
                        Log.d(GoogleTAG, "google sign in fail" , task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);

                    }

                });
                */

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful())
                        {
                            Log.d(GoogleTAG, "google login secces");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);
                        }else {
                            Log.d(GoogleTAG, "google sign in fail" , task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);

                        }
                    }
                });
    }



}