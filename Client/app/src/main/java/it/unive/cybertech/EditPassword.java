package it.unive.cybertech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class EditPassword extends AppCompatActivity {
    FloatingActionButton editPwd;
    Context context = EditPassword.this;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.EditPassword);

        EditText oldPwd = findViewById(R.id.EditPassword_currentPwd);
        EditText newPwd = findViewById(R.id.EditPassword_newPwd);
        EditText confirmNewPwd = findViewById(R.id.EditPassword_confirmNewPwd);
        editPwd = findViewById(R.id.EditPassword_confirmButton);


        editPwd.setOnClickListener( v -> {
            boolean stato = true;
            if(oldPwd.length() <= 0) {
                oldPwd.setError(getString(R.string.requiredField));
            }
            if (newPwd.length() <= 0) {
                newPwd.setError(getString(R.string.requiredField));
                stato = false;
            }
            if (confirmNewPwd.length() <= 0) {
                confirmNewPwd.setError(getString(R.string.requiredField));
                stato = false;
            }
            if (newPwd.getText().toString().equals(oldPwd.getText().toString())) {
                String message = getString(R.string.same_password);
                Toast samePwd = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                samePwd.show();
                stato = false;
            }
            if (!newPwd.getText().toString().equals(confirmNewPwd.getText().toString())) {
                String message = getString(R.string.password_mismatch);
                Toast differentPwds = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                differentPwds.show();
                stato = false;
            }
            if (stato) {
                changePwdAndLogout(newPwd.getText().toString());
            }
        });
    }

    private void changePwdAndLogout(String newPwd) {
        currentUser.updatePassword(newPwd)
                .addOnCompleteListener( (task) -> {
                        if (task.isSuccessful()) {
                            String message = getString(R.string.pwdUpdated);
                            Toast editPwdOk = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                            editPwdOk.show();
                            Handler handler = new Handler();
                            handler.postDelayed( () -> {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(context, SplashScreen.class));
                            }, 800);
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                String message = getString(R.string.WeakPwd);
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