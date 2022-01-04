package it.unive.cybertech;

import static it.unive.cybertech.utils.Utils.HANDLER_DELAY;
import static it.unive.cybertech.utils.Utils.logout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

/**
 * EditPassword is the activity that allow user to edit own password to login on "Families Share".
 *
 * @author Daniele Dotto
 * @since 1.0
 */
public class EditPassword extends AppCompatActivity {
    private final @NonNull
    Context context = EditPassword.this;
    private final @NonNull
    FirebaseUser currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    private @Nullable
    EditText newPwd, confirmNewPwd;
    private @Nullable
    FloatingActionButton editPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        initActionBar();
        bindLayoutObjects();

        getEditPwd().setOnClickListener(v -> {
            if (checkFields())
                changePwdAndLogout(getNewPwd().getText().toString());
        });
    }

    /**
     * Bind all objects contained in layout.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects() {
        newPwd = findViewById(R.id.EditPassword_newPwd);
        confirmNewPwd = findViewById(R.id.EditPassword_confirmNewPwd);
        editPwd = findViewById(R.id.EditPassword_confirmButton);
    }

    /**
     * Initialize the action bar of this Activity.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void initActionBar() {
        @NonNull ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.EditPassword);
    }

    /**
     * Check if the required fields (name and description) are filled.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private boolean checkFields() {
        boolean stato = true;
        if (getNewPwd().length() <= 0) {
            getNewPwd().setError(getString(R.string.requiredField));
            stato = false;
        }
        if (getConfirmNewPwd().length() <= 0) {
            getConfirmNewPwd().setError(getString(R.string.requiredField));
            stato = false;
        }
        if (!getNewPwd().getText().toString().equals(getConfirmNewPwd().getText().toString())) {
            showShortToast(getString(R.string.password_mismatch));
            stato = false;
        }
        return stato;
    }

    /**
     * Useful function that create and show a short-length toast (@see "{@link Toast}".
     *
     * @since 1.0
     */
    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Method that allow user to reauthenticate if too much time is passed from the last session
     * login.
     *
     * @since 1.0
     */
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

    /**
     * Method that allow user to change password;
     * The user will have to login again.
     *
     * @since 1.0
     */
    private void changePwdAndLogout(@NonNull String newPwd) {
        currentUser.updatePassword(newPwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showShortToast(getString(R.string.pwdUpdated));
                        @NonNull Handler handler = new Handler();
                        handler.postDelayed(() -> logout(context), HANDLER_DELAY);
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e) {
                            showShortToast(getString(R.string.WeakPwd));
                        } catch (FirebaseAuthRecentLoginRequiredException e) {
                            if (currentUser.getEmail() != null)
                                showDialog(currentUser.getEmail(), newPwd);
                        } catch (Exception e) {
                            showShortToast(getString(R.string.genericError));
                        }
                    }
                });
    }

    /**
     * Allow user to finish the current activity as 'go back' button.
     *
     * @author Daniele Dotto
     * @since 1.0
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Return new password EditText only if that is not null.
     *
     * @return "{@link #newPwd}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getNewPwd() {
        return Objects.requireNonNull(newPwd);
    }

    /**
     * Return confirm of new password EditText only if that is not null.
     *
     * @return "{@link #confirmNewPwd}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getConfirmNewPwd() {
        return Objects.requireNonNull(confirmNewPwd);
    }

    /**
     * Return confirm edit password button only if that is not null.
     *
     * @return "{@link #editPwd}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    FloatingActionButton getEditPwd() {
        return Objects.requireNonNull(editPwd);
    }
}