package com.example.tileshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final static String LOG_TAG = RegistrationActivity.class.getName();
    private static final String PREF = RegistrationActivity.class.getPackage().toString();
    EditText userNameET;
    EditText passwordET;
    EditText passwordConfirmET;
    EditText emailET;
    EditText phoneET;
    RadioGroup accountRG;

    private SharedPreferences spreferences;
    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Bundle bundle = getIntent().getExtras();
        int key = bundle.getInt("KEY");
        if(key != 3){
            finish();
        }

        userNameET = findViewById(R.id.userNameEditText);
        passwordET = findViewById(R.id.passwordEditText);
        passwordConfirmET = findViewById(R.id.passworAgainEditText);
        emailET = findViewById(R.id.emailEditText);
        phoneET = findViewById(R.id.phoneEditText);
        accountRG = findViewById(R.id.accountType);
        accountRG.check(R.id.buyerRadioButton);

        spreferences = getSharedPreferences(PREF,MODE_PRIVATE);
        String userEmail = spreferences.getString("email", "");
        String password = spreferences.getString("password", "");

        emailET.setText(userEmail);
        passwordET.setText(password);
        passwordConfirmET.setText(password);

        fAuth = FirebaseAuth.getInstance();
    }

    public void registration(View view) {
        String userName = userNameET.getText().toString();
        String password = passwordET.getText().toString();
        String passwordConfirm = passwordConfirmET.getText().toString();
        String email = emailET.getText().toString();
        String phoneNumber = phoneET.getText().toString();
        int chacked = accountRG.getCheckedRadioButtonId();
        RadioButton radioButton = accountRG.findViewById(chacked);
        String accountType = radioButton.getText().toString();

        if(!password.equals(passwordConfirm)){
            Log.e(LOG_TAG,"A két jelszó nem egyezik meg!");
            return;
        }

        Log.i(LOG_TAG,"Regisztrált: "+userName+ ", ezzel az email címmel: "+email);

        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "User created successfully");
                startTileShopping();
            }else{
                Log.d(LOG_TAG, "User was not created successfully");
                Toast.makeText(RegistrationActivity.this, "Exception: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void cancel(View view) {
        finish();
    }

    private void startTileShopping(){
        Intent intent = new Intent(this, TileListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }
}