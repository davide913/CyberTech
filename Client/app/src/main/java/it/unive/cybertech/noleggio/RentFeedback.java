package it.unive.cybertech.noleggio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.unive.cybertech.R;
import it.unive.cybertech.utils.Utils;

public class RentFeedback extends AppCompatActivity {
    private static final int BAD_SCORE_BOUND = 3;
    static final int SUCCESS = 1, FAIL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_feedback);
        RatingBar rating = findViewById(R.id.rating_rent_feedback);
        LinearLayout feedbackLayout = findViewById(R.id.feedback_rent_feedback);
        FloatingActionButton done = findViewById(R.id.done_rent_feedback);
        RadioGroup lowGravity = findViewById(R.id.low_gravity_rent_feedback_group),
                highGravity = findViewById(R.id.high_gravity_rent_feedback_group),
                notWorking = findViewById(R.id.not_working_rent_feedback_group),
                lost = findViewById(R.id.lost_rent_feedback_group);

        rating.setOnRatingBarChangeListener((ratingBar, rating1, fromUser) -> {
            if (rating1 <= BAD_SCORE_BOUND)
                feedbackLayout.setVisibility(View.VISIBLE);
            else
                feedbackLayout.setVisibility(View.GONE);
        });
        done.setOnClickListener(v -> {
            double score = rating.getRating() / 2;
            if (rating.getRating() <= BAD_SCORE_BOUND) {
                score = -0.5;
                if (lowGravity.getCheckedRadioButtonId() == R.id.low_gravity_yes_rent_feedback)
                    score -= 0.5;
                if (highGravity.getCheckedRadioButtonId() == R.id.high_gravity_rent_yes_feedback_group)
                    score -= 1;
                if (notWorking.getCheckedRadioButtonId() == R.id.not_working_rent_yes_feedback_group)
                    score -= 2;
                if (lost.getCheckedRadioButtonId() == R.id.lost_rent_yes_feedback_group)
                    score -= 1;
            }
            new ConfirmFeedbackDialog(score).show(getSupportFragmentManager(), "ConfirmFeedbackDialog");
        });
    }

    public static class ConfirmFeedbackDialog extends DialogFragment {

        private final double score;

        ConfirmFeedbackDialog(double score) {
            this.score = score;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(score >= 0 ? R.string.rent_feedback_positive_message : R.string.rent_feedback_negative_message)
                    .setTitle(score >= 0 ? R.string.rent_feedback_positive : R.string.rent_feedback_negative)
                    .setPositiveButton(android.R.string.ok, (dialog, id) -> {
                        // FIRE ZE MISSILES!
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                        // User cancelled the dialog
                    });
            return builder.create();
        }
    }
}