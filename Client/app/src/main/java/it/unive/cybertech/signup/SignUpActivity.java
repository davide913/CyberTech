package it.unive.cybertech.signup;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import it.unive.cybertech.R;
import it.unive.cybertech.SplashScreen;
import it.unive.cybertech.database.Profile.User;
import it.unive.cybertech.utils.Utils;

/**
 * Provide a signup method for the user
 * Actually only email is supported
 * See "{@link it.unive.cybertech.signup.LogInActivity}" for Google singin
 *
 * @author Mattia Musone
 * */
public class SignUpActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 5;
    private final @NonNull
    Context context = this;
    private FirebaseAuth mAuth;
    private EditText name, surname, dateOfBirth, email, pwd, confirmPwd;
    private Date inputDateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        @NonNull FloatingActionButton done = findViewById(R.id.signup_done);

        name = findViewById(R.id.name_signup);
        surname = findViewById(R.id.surname_signup);
        dateOfBirth = findViewById(R.id.dateOfBirth_signup);
        email = findViewById(R.id.email_signup);
        pwd = findViewById(R.id.password_signup);
        confirmPwd = findViewById(R.id.password_confirm_signup);


        @NonNull GregorianCalendar calendar = new GregorianCalendar();
        dateOfBirth.setOnClickListener(v -> {
            int y = calendar.get(Calendar.YEAR);
            int m = calendar.get(Calendar.MONTH);
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            @NonNull DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                inputDateOfBirth = calendar.getTime();
                @NonNull String dateStr = Utils.formatDateToString(inputDateOfBirth);
                dateOfBirth.setText(dateStr);
            }, y, m, d);
            dialog.show();
        });


        @NonNull Spinner sex = findViewById(R.id.sex_signup);
        done.setOnClickListener(v -> {
            if (checkFields())
                signInWithEmailAndPassword(email.getText().toString(), pwd.getText().toString(), name.getText().toString(), surname.getText().toString(), inputDateOfBirth, sex.getSelectedItem().toString());
        });
    }

    /**
     * Checks if all the required fields are compiled
     *
     * @return Indicate if all the fields are fill
     * */
    private boolean checkFields() {
        boolean ok = true;
        if (name.getText().length() <= 0) {
            ok = false;
            name.setError(getString(R.string.field_required));
        }
        if (surname.getText().length() <= 0) {
            ok = false;
            surname.setError(getString(R.string.field_required));
        }
        if (dateOfBirth.getText().length() <= 0) {
            ok = false;
            dateOfBirth.setError(getString(R.string.field_required));
        }
        if (email.getText().length() <= 0) {
            ok = false;
            email.setError(getString(R.string.field_required));
        }
        if (pwd.getText().length() <= 0) {
            ok = false;
            pwd.setError(getString(R.string.field_required));
        }
        if (confirmPwd.getText().length() <= 0) {
            ok = false;
            confirmPwd.setError(getString(R.string.field_required));
        }
        if (!pwd.getText().toString().equals(confirmPwd.getText().toString())) {
            ok = false;
            confirmPwd.setError(getString(R.string.password_mismatch));
        }
        return ok;
    }

    /**
     * Do the registration with the provided informations
     *
     * @param email The email for the registration
     * @param password The password to use for login
     * @param name The user name
     * @param surname The user surname
     * @param dateOfBirth The date of birth of the user
     * @param sex The string sex of the user
     * */
    private void signInWithEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull String name, @NonNull String surname, @NonNull Date dateOfBirth, @NonNull String sex) {
        try {
            Utils.getLocation(this, new Utils.TaskResult<Utils.Location>() {
                @Override
                public void onComplete(Utils.Location location) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            new Thread(() -> {
                                try {
                                    //Create the user in the database
                                    User u = User.createUser(Objects.requireNonNull(task.getResult().getUser()).getUid(), name.trim(), surname.trim(), Utils.convertToSex(sex), dateOfBirth, location.address, location.city, location.country, (long) location.latitude, (long) location.longitude, false);
                                    if (u != null) {
                                        @NonNull Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mAuth.signOut();
                                }
                            }).start();
                            //Manage the firebase exception
                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthWeakPasswordException e) {
                                new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.SaferPwd));
                                e.printStackTrace();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.wrongEmailFormat));
                            } catch (FirebaseAuthUserCollisionException e) {
                                new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.ExistingUser));
                            } catch (Exception e) {
                                new Utils.Dialog(context).show(getString(R.string.FailedSignup), getString(R.string.genericError));
                            }
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                }
            });
        } catch (Utils.PermissionDeniedException e) {
            e.printStackTrace();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                    .show(getString(R.string.position_required), getString(R.string.position_required_description));
    }
}