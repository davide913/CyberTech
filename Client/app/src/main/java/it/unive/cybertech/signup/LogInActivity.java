package it.unive.cybertech.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.utils.Utils;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

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
            firebaseAuthWithGoogle("");
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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //User.cre
                        startActivity(new Intent(getApplicationContext(), SplashScreen.class));
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException)
                            Utils.showGenericDialog("Login fallito", task.getException().toString(), getApplicationContext());
                    }
                });
    }

    private void loginWithCredentials(String email, String pwd, Context c) {
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //User.cre
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
}