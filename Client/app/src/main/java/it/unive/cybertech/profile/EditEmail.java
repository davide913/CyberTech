package it.unive.cybertech.profile;

import static it.unive.cybertech.utils.Showables.showShortToast;
import static it.unive.cybertech.utils.Utils.HANDLER_DELAY;
import static it.unive.cybertech.utils.Utils.logout;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import it.unive.cybertech.R;

/**
 * EditEmail is the activity that allow user to edit own email address to login on "Families Share (Plugin)".
 *
 * @author Daniele Dotto
 * @since 1.0
 */
public class EditEmail extends AppCompatActivity {
    private final @NonNull
    Context context = EditEmail.this;
    private final @NonNull
    FirebaseUser currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser());
    private @Nullable
    EditText oldMail, newEmail, confirmNewEmail;
    private @Nullable
    FloatingActionButton editEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);
        initActionBar();
        bindLayoutObjects();

        getOldMail().setText(currentUser.getEmail());

        getEditEmail().setOnClickListener(v -> {
            if (checkFields())
                changeEmailAndLogout(getNewEmail().getText().toString());
        });
    }

    /**
     * Check if the required fields (name and description) are filled.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private boolean checkFields() {
        boolean stato = true;
        int newMailLength = getNewEmail().length();
        int confirmNewMail = getConfirmNewEmail().length();

        if (newMailLength <= 0) {
            getNewEmail().setError(getString(R.string.requiredField));
            stato = false;
        }
        if (confirmNewMail <= 0) {
            getConfirmNewEmail().setError(getString(R.string.requiredField));
            stato = false;
        }
        if (!getNewEmail().getText().toString().equals(getConfirmNewEmail().getText().toString())) {
            showShortToast(getString(R.string.email_mismatch), context);
            stato = false;
        }
        return stato;
    }

    /**
     * Bind all layout objects contained in this Activity.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects() {
        oldMail = findViewById(R.id.EditEmail_currentEmail);
        newEmail = findViewById(R.id.EditEmail_newEmail);
        confirmNewEmail = findViewById(R.id.EditEmail_confirmNewEmail);

        editEmail = findViewById(R.id.EditEmail_confirmButton);
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
        actionBar.setTitle(R.string.EditEmail);
    }

    /**
     * Allow current user to reauthenticate if too much time is passed from the last login.
     *
     * @author Daniele Dotto
     * @since 1.0
     */
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

    /**
     * Allow user to change email;
     * The user will have to login again.
     *
     * @author Daniele Dotto
     * @since 1.0
     */
    private void changeEmailAndLogout(@NonNull String newEmail) {
        currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showShortToast(getString(R.string.email_updated), context);
                @NonNull Handler handler = new Handler();
                handler.postDelayed(() -> logout(context), HANDLER_DELAY);
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    showShortToast(getString(R.string.WrongFormatEmail), context);
                } catch (FirebaseAuthUserCollisionException e) {
                    showShortToast(getString(R.string.existingUser), context);
                } catch (FirebaseAuthInvalidUserException e) {
                    showShortToast(getString(R.string.InvalidUser), context);
                } catch (FirebaseAuthRecentLoginRequiredException e) {
                    if (currentUser.getEmail() != null)
                        showDialog(currentUser.getEmail(), newEmail);
                } catch (Exception e) {
                    showShortToast(getString(R.string.genericError), context);
                    e.printStackTrace();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Return current email EditText only if that is not null.
     *
     * @return "{@link #oldMail}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getOldMail() {
        return Objects.requireNonNull(oldMail);
    }

    /**
     * Return new email EditText only if that is not null.
     *
     * @return "{@link #newEmail}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getNewEmail() {
        return Objects.requireNonNull(newEmail);
    }

    /**
     * Return confirm of new email EditText only if that is not null.
     *
     * @return "{@link #confirmNewEmail}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    EditText getConfirmNewEmail() {
        return Objects.requireNonNull(confirmNewEmail);
    }

    /**
     * Return confirm edit email button only if that is not null.
     *
     * @return "{@link #editEmail}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    FloatingActionButton getEditEmail() {
        return Objects.requireNonNull(editEmail);
    }
}