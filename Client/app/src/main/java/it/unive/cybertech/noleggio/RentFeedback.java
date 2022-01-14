package it.unive.cybertech.noleggio;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.unive.cybertech.R;
import it.unive.cybertech.utils.Utils;

/**
 * This class display a form in order to evaluate the lending and the material's condition
 * <p>
 * If the feedback is negative, the points will be subtracted from the user lending point and if the user has a negative score, then he cannot gets lending until he pay for the damage
 * Otherwise a positive score will be added to his lending points
 *
 * @author Mattia Musone
 */
public class RentFeedback extends AppCompatActivity {
    private static final int BAD_SCORE_BOUND = 3;
    static final int SUCCESS = 1, FAIL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_feedback);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.feedback);
        RatingBar rating = findViewById(R.id.rating_rent_feedback);
        LinearLayout feedbackLayout = findViewById(R.id.feedback_rent_feedback);
        FloatingActionButton done = findViewById(R.id.done_rent_feedback);
        RadioGroup lowGravity = findViewById(R.id.low_gravity_rent_feedback_group),
                highGravity = findViewById(R.id.high_gravity_rent_feedback_group),
                notWorking = findViewById(R.id.not_working_rent_feedback_group),
                lost = findViewById(R.id.lost_rent_feedback_group);

        rating.setOnRatingBarChangeListener((ratingBar, rating1, fromUser) -> {
            //If the stars are under a bound score, then shows the layout with more information about the negative feedback
            if (rating1 <= BAD_SCORE_BOUND)
                feedbackLayout.setVisibility(View.VISIBLE);
            else
                feedbackLayout.setVisibility(View.GONE);
        });
        done.setOnClickListener(v -> {
            //Calculate the score
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
            double finalScore = score;
            new Utils.Dialog(this)
                    .hideCancelButton()
                    .setCallback(new Utils.DialogResult() {
                        @Override
                        public void onSuccess() {
                            Intent data = new Intent();
                            data.putExtra("Points", finalScore);
                            //Set the result for the caller activity
                            setResult(SUCCESS, data);
                            finish();
                        }

                        @Override
                        public void onCancel() {

                        }
                    })
                    .show(getString(score >= 0 ? R.string.rent_feedback_positive : R.string.rent_feedback_negative), getString(score >= 0 ? R.string.rent_feedback_positive_message : R.string.rent_feedback_negative_message));
        });
    }
}