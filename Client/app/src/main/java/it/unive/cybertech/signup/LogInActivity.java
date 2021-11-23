package it.unive.cybertech.signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LinearLayout googleSignIn = findViewById(R.id.google_sign_in);
        Button login = findViewById(R.id.login);
        TextView signup = findViewById(R.id.signup);
        EditText email = findViewById(R.id.email_login),
                password = findViewById(R.id.password_login);

        googleSignIn.setOnClickListener(v -> {
            getIDForAuthWithGoogle();
        });
        login.setOnClickListener(v -> {
            boolean ok = true;
            if (email.getText().length() == 0) {
                email.setError("Campo obbligatorio");
                ok = false;
            }
            if (password.getText().length() == 0) {
                password.setError("Campo obbligatorio");
                ok = false;
            }
            if (ok)
                loginWithCredentials(email.getText().toString(), password.getText().toString(), this);
        });
        signup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    private void getIDForAuthWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_key))
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Context c = this;
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String name = user.getDisplayName(), surname = null;
                        if (name.contains(" ")) {
                            surname = name.substring(name.indexOf(" "));
                            name = name.split(" ", 1)[0];
                        }
                        User.createUser(name, surname, "N", null, null, null, (long) location.getLatitude(), (long) location.getLongitude(), false);
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Utils.showGenericDialog("Login fallito", "Credenziali errate", c);
                            e.printStackTrace();
                        } catch (FirebaseAuthInvalidUserException e) {
                            Utils.showGenericDialog("Login fallito", "Utente inesistente", c);
                        } catch (Exception e) {
                            Utils.showGenericDialog("Login fallito", "Errore generico", c);
                        }
                    }
                });
    }

    private void loginWithCredentials(String email, String pwd, Context c) {
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Utils.showGenericDialog("Login fallito", "Credenziali errate", c);
                            e.printStackTrace();
                        } catch (FirebaseAuthInvalidUserException e) {
                            Utils.showGenericDialog("Login fallito", "Utente inesistente", c);
                        } catch (Exception e) {
                            Utils.showGenericDialog("Login fallito", "Errore generico", c);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkGPSPermission();
    }

    private void showGPSDialogInformation() {
        AppCompatActivity c = this;
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(c, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION},
                                1);
                    }
                })
                .show();
    }

    private void checkGPSPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            showGPSDialogInformation();
        else
            initGPS();
    }

    private void initGPS() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            this.location = location;
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            Utils.showGenericDialog("Impossibile continuare", "Senxa l'accesso alla posizione non è possibile continuare la registrazione", this);
        else
            initGPS();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Utils.showGenericDialog("Login fallito", "Si prega di riprovare più tardi", this);
                e.printStackTrace();
            }
        }
    }
}