package com.example.tileshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{
    private final static String LOG_TAG = MainActivity.class.getName();
    private final static int KEY = 3;
    private final static int SING_IN_KEY = 55;
    private static final String PREF = MainActivity.class.getPackage().toString();

    EditText userNameET;
    EditText passwordET;

    private SharedPreferences spreferences;
    private FirebaseAuth fAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameET = findViewById(R.id.userNameEditText);
        passwordET = findViewById(R.id.passwordEditText);

        spreferences = getSharedPreferences(PREF,MODE_PRIVATE);
        fAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gsio = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(String.valueOf(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gsio);

        getSupportLoaderManager().restartLoader(0, null, this);
    }

    public void login(View view) {
        String userName = userNameET.getText().toString();
        String password = passwordET.getText().toString();

        fAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Login successfully");
                startTileShopping();
            }else{
                Log.d(LOG_TAG, "Failed login");
                Toast.makeText(MainActivity.this, "Login failed: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startTileShopping(){
        Intent intent = new Intent(this, TileListActivity.class);
        startActivity(intent);
    }

    public void registration(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("KEY",KEY);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = spreferences.edit();
        editor.putString("email",userNameET.getText().toString());
        editor.putString("password",passwordET.getText().toString());
        editor.apply();
        Log.i(LOG_TAG,"onPause");
    }

    public void loginAsGuest(View view) {
        fAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Anonym login");
                startTileShopping();
            }else{
                Log.d(LOG_TAG, "Anonym login failed");
                Toast.makeText(MainActivity.this, "Anonym login failed: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == SING_IN_KEY){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "Google auth "+account.getId());
                firebaseWithGoogle(account.getIdToken());
            }catch (ApiException e){
                Log.w(LOG_TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseWithGoogle(String token){
        AuthCredential ac = GoogleAuthProvider.getCredential(token, null);
        fAuth.signInWithCredential(ac).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Google login");
                startTileShopping();
            }else{
                Log.d(LOG_TAG, "Google login failed");
                Toast.makeText(MainActivity.this, "Google login failed: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void googleLogin(View view) {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, SING_IN_KEY);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new HelperAsyncLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Button button = findViewById(R.id.loginButton);
        button.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {}
}