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
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class EditPassword extends AppCompatActivity {
    @NonNull Context context = EditPassword.this;
    @NonNull FirebaseUser currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    FloatingActionButton editPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.EditPassword);

        @NonNull EditText newPwd = findViewById(R.id.EditPassword_newPwd);
        @NonNull EditText confirmNewPwd = findViewById(R.id.EditPassword_confirmNewPwd);
        editPwd = findViewById(R.id.EditPassword_confirmButton);


        editPwd.setOnClickListener( v -> {
            boolean stato = true;
            if (newPwd.length() <= 0) {
                newPwd.setError(getString(R.string.requiredField));
                stato = false;
            }
            if (confirmNewPwd.length() <= 0) {
                confirmNewPwd.setError(getString(R.string.requiredField));
                stato = false;
            }
            if (!newPwd.getText().toString().equals(confirmNewPwd.getText().toString())) {
                showShortToast(getString(R.string.password_mismatch));
                stato = false;
            }
            if (stato) {
                changePwdAndLogout(newPwd.getText().toString());
            }
        });
    }

    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showDialog(@NonNull String currentEmail, @NonNull String newPwd) {
        @NonNull LayoutInflater layoutInflater = this.getLayoutInflater();
        @NonNull View view = layoutInflater.inflate(R.layout.dialog_rilogin, null);
        @NonNull AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.authNecessary)
                .setPositiveButton(R.string.Ok, (dialog, which) -> {
                    dialog.dismiss();
                    @NonNull EditText pwd = view.findViewById(R.id.dialogRilogin);
                    @NonNull String passwd = pwd.getText().toString();
                    @NonNull AuthCredential credential = EmailAuthProvider
                            .getCredential(currentEmail, passwd);
                    currentUser.reauthenticate(credential).addOnCompleteListener(task -> changePwdAndLogout(newPwd));
                })
                .setNegativeButton(R.string.Cancel, (dialog, which) -> dialog.dismiss())
                .setView(view);
        builder.show();
    }

    private void changePwdAndLogout(@NonNull String newPwd) {
        currentUser.updatePassword(newPwd)
                .addOnCompleteListener( task -> {
                        if (task.isSuccessful()) {
                            showShortToast(getString(R.string.pwdUpdated));
                            @NonNull Handler handler = new Handler();
                            handler.postDelayed( () -> {
                                FirebaseAuth.getInstance().signOut();
                                @NonNull Intent intent = new Intent(context, SplashScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }, 800);
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthWeakPasswordException e) {
                                showShortToast(getString(R.string.WeakPwd));
                            } catch (FirebaseAuthRecentLoginRequiredException e) {
                                if (currentUser.getEmail() != null)
                                    showDialog(currentUser.getEmail(), newPwd);
                            }
                            catch (Exception e) {
                                showShortToast(getString(R.string.genericError));
                            }
                        }
                });
    }
}