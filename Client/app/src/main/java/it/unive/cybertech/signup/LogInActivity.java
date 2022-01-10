package it.unive.cybertech.signup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Profile.Exception.NoUserFoundException;
import it.unive.cybertech.database.Profile.Sex;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int PERMISSIONS_FINE_LOCATION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
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
                        new Thread(() -> {
                            final User[] u = {null};
                            FirebaseUser user = mAuth.getCurrentUser();
                            String name = user.getDisplayName(), surname = null;
                            if (name.contains(" ")) {
                                surname = name.substring(name.indexOf(" ")).trim();
                                name = name.split(" ")[0];
                            }
                            try {
                                u[0] = User.obtainUserById(user.getUid());
                            } catch (NoUserFoundException e) {
                                e.printStackTrace();
                                String finalName = name;
                                String finalSurname = surname;
                                try {
                                    Utils.getLocation(this, new Utils.TaskResult<Utils.Location>() {
                                        @Override
                                        public void onComplete(Utils.Location location) {
                                            try {
                                                u[0] = User.createUser(user.getUid(), finalName.trim(), finalSurname, Sex.nonBinary, null, location.address, location.city, location.country, (long) location.latitude, (long) location.longitude, false);
                                            } catch (ExecutionException | InterruptedException executionException) {
                                                executionException.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public OnFailureListener onError(Exception e) {

                                            return null;
                                        }
                                    });
                                } catch (Utils.PermissionDeniedException | ExecutionException | InterruptedException permissionDeniedException) {
                                    permissionDeniedException.printStackTrace();
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            if (u[0] != null) {
                                startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                                finish();
                            } else
                                mAuth.signOut();
                        }).start();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            new Utils.Dialog(c).show("Login fallito", "Credenziali errate");
                            e.printStackTrace();
                        } catch (FirebaseAuthInvalidUserException e) {
                            new Utils.Dialog(c).show("Login fallito", "Utente inesistente");
                        } catch (Exception e) {
                            new Utils.Dialog(c).show("Login fallito", "Errore generico");
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
                            new Utils.Dialog(c).show("Login fallito", "Credenziali errate");
                            e.printStackTrace();
                        } catch (FirebaseAuthInvalidUserException e) {
                            new Utils.Dialog(c).show("Login fallito", "Utente inesistente");
                        } catch (Exception e) {
                            new Utils.Dialog(c).show("Login fallito", "Errore generico");
                        }
                    }
                });
    }

    private void showGPSDialogInformation() {
        final AppCompatActivity ac = this;
        Utils.Dialog dialog = new Utils.Dialog(this);
        dialog.setCallback(new Utils.DialogResult() {
            @Override
            public void onSuccess() {
                ActivityCompat.requestPermissions(ac, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }

            @Override
            public void onCancel() {
                finish();
            }
        }).show(getString(R.string.position_required), getString(R.string.position_required_description));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
            new Utils.Dialog(this)
                    .setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            finish();
                        }

                        @Override
                        public void onCancel() {

                        }
                    })
                    .hideCancelButton()
                    .show("Impossibile continuare", "Senza l'accesso alla posizione non è possibile continuare la registrazione");
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
                new Utils.Dialog(this).show("Login fallito", "Si prega di riprovare più tardi");
                e.printStackTrace();
            }
        }
    }
}