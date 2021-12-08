package it.unive.cybertech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class EditEmail extends AppCompatActivity {
    private final @NonNull Context context = EditEmail.this;
    private final @NonNull FirebaseUser currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    private @Nullable EditText oldMail, newEmail, confirmNewEmail;
    private FloatingActionButton editEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        // Header
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.EditEmail);
        }


        // UI fields
        oldMail = findViewById(R.id.EditEmail_currentEmail);
        oldMail.setText(currentUser.getEmail());
        newEmail = findViewById(R.id.EditEmail_newEmail);
        confirmNewEmail = findViewById(R.id.EditEmail_confirmNewEmail);
        editEmail = findViewById(R.id.EditEmail_confirmButton);

        editEmail.setOnClickListener(v -> {
            boolean stato = true;
            int newMailLength = Objects.requireNonNull(newEmail).length();
            int confirmNewMail = Objects.requireNonNull(confirmNewEmail).length();

            if (newMailLength <= 0) {
                newEmail.setError(getString(R.string.requiredField));
                stato = false;
            }
            if (confirmNewMail <= 0) {
                confirmNewEmail.setError(getString(R.string.requiredField));
                stato = false;
            }
            if (!newEmail.getText().toString().equals(confirmNewEmail.getText().toString())) {
                showShortToast(getString(R.string.email_mismatch));
                stato = false;
            }
            if (stato) {
                changeEmailAndLogout(newEmail.getText().toString());
            }
        });
    }

    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showDialog(@NonNull String currentEmail, @NonNull String newEmail) {
        @NonNull LayoutInflater layoutInflater = this.getLayoutInflater();
        @NonNull View view = layoutInflater.inflate(R.layout.dialog_rilogin, null);
        @NonNull AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.authNecessary)
                .setPositiveButton(R.string.Ok, (dialog, which) -> {
                    dialog.dismiss();
                    @NonNull EditText pwd = view.findViewById(R.id.dialogRilogin);
                    @NonNull String pwdString = pwd.getText().toString();
                    @NonNull AuthCredential credential = EmailAuthProvider.getCredential(currentEmail, pwdString);
                    currentUser.reauthenticate(credential).addOnCompleteListener(task -> changeEmailAndLogout(newEmail));
                })
                .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.dismiss())
                .setView(view);
        builder.show();
    }

    private void changeEmailAndLogout(@NonNull String newEmail) {
        currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showShortToast(getString(R.string.email_updated));
                @NonNull Handler handler = new Handler();
                handler.postDelayed(() -> {
                    FirebaseAuth.getInstance().signOut();
                    @NonNull Intent intent = new Intent(context, SplashScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }, 800);
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    showShortToast(getString(R.string.WrongFormatEmail));
                } catch (FirebaseAuthUserCollisionException e) {
                    showShortToast(getString(R.string.existingUser));
                } catch (FirebaseAuthInvalidUserException e) {
                    showShortToast(getString(R.string.InvalidUser));
                } catch (FirebaseAuthRecentLoginRequiredException e) {
                    if (currentUser.getEmail() != null)
                        showDialog(currentUser.getEmail(), newEmail);
                } catch (Exception e) {
                    showShortToast(getString(R.string.genericError));
                    e.printStackTrace();
                }
            }
        });
    }
}