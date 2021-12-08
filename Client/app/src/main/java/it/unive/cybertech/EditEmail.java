package it.unive.cybertech;


import static it.unive.cybertech.utils.CachedUser.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.signup.LogInActivity;


public class EditEmail extends AppCompatActivity {
    FloatingActionButton editEmail;
    Context context = EditEmail.this;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.EditEmail);


        EditText oldMail = findViewById(R.id.EditEmail_currentEmail);
        oldMail.setText(currentUser.getEmail());
        EditText newEmail = findViewById(R.id.EditEmail_newEmail);
        EditText confirmNewEmail = findViewById(R.id.EditEmail_confirmNewEmail);
        editEmail = findViewById(R.id.EditEmail_confirmButton);

        editEmail.setOnClickListener( v -> {
            boolean stato = true;
            if(newEmail.length() <= 0) {
                newEmail.setError(getString(R.string.requiredField));
                stato = false;
            }
            if(confirmNewEmail.length() <= 0) {
                confirmNewEmail.setError(getString(R.string.requiredField));
                stato = false;
            }
            if(!newEmail.getText().toString().equals(confirmNewEmail.getText().toString())) {
                String message = getString(R.string.email_mismatch);
                Toast differentPwds = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                differentPwds.show();
                stato = false;
            }
            if(stato) {
                changeEmailAndLogout(newEmail.getText().toString());
            }
        });

    }

    private void changeEmailAndLogout(String newEmail) {
        currentUser.updateEmail(newEmail).addOnCompleteListener( (task) -> {
                if (task.isSuccessful()) {
                    String message = getString(R.string.email_updated);
                    Toast editEmailOk = Toast.makeText(EditEmail.this, message, Toast.LENGTH_SHORT);
                    editEmailOk.show();
                    Handler handler = new Handler();
                    handler.postDelayed( () -> {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(context, SplashScreen.class));
                    }, 800);
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        String message = getString(R.string.WrongFormatEmail);
                        Toast invalidCretentials = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                        invalidCretentials.show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        String message = getString(R.string.existingUser);
                        Toast invalidCretentials = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                        invalidCretentials.show();
                    } catch (FirebaseAuthInvalidUserException e) {
                        String message = getString(R.string.InvalidUser);
                        Toast invalidCretentials = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                        invalidCretentials.show();
                    } catch (Exception e) {
                        String message = getString(R.string.genericError);
                        Toast invalidCretentials = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                        invalidCretentials.show();
                    }
                }
        });
    }
}