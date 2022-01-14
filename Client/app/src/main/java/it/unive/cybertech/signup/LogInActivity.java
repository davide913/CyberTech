package it.unive.cybertech.signup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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

/**
 * This class provide a way to login to the user
 * The allowed methods are: email or Google
 * Note: we don't check if the email exists
 *
 * @author Mattia Musone
 */
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
                email.setError(getString(R.string.field_required));
                ok = false;
            }
            if (password.getText().length() == 0) {
                password.setError(getString(R.string.field_required));
                ok = false;
            }
            if (ok)
                loginWithCredentials(email.getText().toString(), password.getText().toString());
        });
        signup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
    }

    /**
     * This function get the app id in order to authenticate with Google Firebase login system and starts the activity to manage the Google-signin account
     */
    private void getIDForAuthWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_key))
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, 0);
    }

    /**
     * Proceed to atuhenticate a user to the app with the token id provided
     *
     * @param idToken The token id of the application authenticated
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Context c = this;
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        new Thread(() -> {
                            final User[] u = {null};
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Split the name and surname provided by google
                            String name = user.getDisplayName(), surname = null;
                            if (name.contains(" ")) {
                                surname = name.substring(name.indexOf(" ")).trim();
                                name = name.split(" ")[0];
                            }
                            try {
                                //If the user already exists in the database (registered with the firebase user unique id provided) then get it
                                u[0] = User.obtainUserById(user.getUid());
                                //otherwise register it
                            } catch (NoUserFoundException e) {
                                e.printStackTrace();
                                String finalName = name;
                                String finalSurname = surname;
                                try {
                                    Utils.getLocation(this, new Utils.TaskResult<Utils.Location>() {
                                        @Override
                                        public void onComplete(Utils.Location location) {
                                            try {
                                                //Create the user in the databse
                                                u[0] = User.createUser(user.getUid(), finalName.trim(), finalSurname, Sex.nonBinary, null, location.address, location.city, location.country, (long) location.latitude, (long) location.longitude, false);
                                            } catch (ExecutionException | InterruptedException executionException) {
                                                executionException.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Utils.logout(getApplicationContext());
                                        }
                                    });
                                } catch (Utils.PermissionDeniedException permissionDeniedException) {
                                    permissionDeniedException.printStackTrace();
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            //If everything gone right, then open the splashscreen and let user proceed
                            if (u[0] != null) {
                                startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                                finish();
                            } else
                                Utils.logout(getApplicationContext());
                        }).start();
                    } else {
                        manageFirebaseException(task.getException());
                    }
                });
    }

    /**
     * manage the type of exception thrown by firebase.
     * The firebase exception are abount something wrong with the user credential, so we need to manage it
     *
     * @param ex The exception thrown
     */
    private void manageFirebaseException(Exception ex) {
        ex.printStackTrace();
        //Means that the credentials are wrong
        if (ex instanceof FirebaseAuthInvalidCredentialsException)
            new Utils.Dialog(getApplicationContext()).show(getString(R.string.login_failed), getString(R.string.wrong_credentials));
            //Means that the user does not exists
        else if (ex instanceof FirebaseAuthInvalidUserException)
            new Utils.Dialog(getApplicationContext()).show(getString(R.string.login_failed), getString(R.string.user_not_found));
            //Otherwise print a generic error
        else
            new Utils.Dialog(getApplicationContext()).show(getString(R.string.login_failed), getString(R.string.generic_error));
    }

    /**
     * Login with username and password
     *
     * @param email The email of the user
     * @param pwd   The password associated to the account
     */
    private void loginWithCredentials(String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                        finish();
                    } else {
                        manageFirebaseException(task.getException());
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //if the user does not granted the permission display an alert
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
                    .show(getString(R.string.position_required), getString(R.string.position_required_description));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Callback of the google login
        if (requestCode == 0) {
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                new Utils.Dialog(this).show(getString(R.string.login_failed), getString(R.string.retry_later));
                e.printStackTrace();
            }
        }
    }
}