package it.unive.cybertech.groups;

import static it.unive.cybertech.groups.HomePage.RELOAD_GROUP;
import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Showables.showShortToast;
import static it.unive.cybertech.utils.Utils.HANDLER_DELAY;
import static it.unive.cybertech.utils.Utils.executeAsync;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;

import it.unive.cybertech.utils.Utils.TaskResult;

/**
 * Fragment that belongs to "{@link it.unive.cybertech.groups.GroupHomePage}".
 * It needs to show all group info.
 *
 * @author Daniele Dotto
 * @since 1.1
 */
public class GroupInfo extends Fragment {

    private @Nullable
    TextView nameGroup;
    private @Nullable
    TextView descriptionGroup;
    private @Nullable
    TextView nUsers;
    private @Nullable
    GroupHomePage activity;
    private @Nullable
    Group thisGroup;
    private @Nullable
    FloatingActionButton joinLeftButton;
    private boolean status = false;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        @NonNull final View view = inflater.inflate(R.layout.fragment_group_information, container, false);
        initFragment();
        bindLayoutObjects(view);
        status = checkGroupMember();
        setTextViews();

        getJoinLeftButton().setOnClickListener(v -> {
            if (!status) {
                addGroupParticipant();
            } else {
                removeGroupParticipant();
            }
            updateParticipants();
            @NonNull Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (activity != null) {
                    activity.setResult(RELOAD_GROUP);
                    activity.finish();
                }
            }, HANDLER_DELAY);
        });

        return view;
    }

    /**
     * Refresh the # of participants of "{@link #thisGroup}" in layout TextView
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void updateParticipants() {
        Objects.requireNonNull(nUsers).setText(String.valueOf(Objects.requireNonNull(thisGroup).getMembers().size()));
    }

    /**
     * Set TextViews text contained in this layout.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void setTextViews() {
        Objects.requireNonNull(nameGroup).setText(Objects.requireNonNull(thisGroup).getName());
        Objects.requireNonNull(descriptionGroup).setText(thisGroup.getDescription());
        updateParticipants();

        if (!status) {
            setButtonInfoAsNoParticipant();
        } else {
            setButtonInfoAsParticipant();
        }
    }

    /**
     * Bind all object contained in this layout.
     *
     * @param view The fragment view.
     * @author Daniele Dotto
     * @since 1.1
     */
    private void bindLayoutObjects(@NonNull final View view) {
        nameGroup = view.findViewById(R.id.group_information_tab_name);
        descriptionGroup = view.findViewById(R.id.group_information_tab_description);
        nUsers = view.findViewById(R.id.group_information_tab_nUsers);

        joinLeftButton = view.findViewById(R.id.group_information_joinLeftGroup);
    }

    /**
     * Find the fragment activity and group in DB based on current selected group.
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void initFragment() {
        activity = (GroupHomePage) requireActivity();
        thisGroup = activity.getThisGroup();
    }

    /**
     * Remove current user from the current selected group "{@link #thisGroup}".
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void removeGroupParticipant() {
        executeAsync(() -> getThisGroup().removeMember(user), null);
        showShortToast(getString(R.string.LeftGroup), requireContext());
        setButtonInfoAsNoParticipant();
        status = false;
    }

    /**
     * Add current user in the current selected group "{@link #thisGroup}".
     *
     * @author Daniele Dotto
     * @since 1.1
     */
    private void addGroupParticipant() {
        executeAsync(() -> getThisGroup().addMember(user), null);
        showShortToast(getString(R.string.NowMember), requireContext());
        setButtonInfoAsNoParticipant();
        status = true;
    }

    /**
     * Set source image and color (red) for left the group.
     *
     * @author Daniele Dotto
     * @see "{@link #joinLeftButton}"
     * @since 1.1
     */
    private void setButtonInfoAsParticipant() {
        getJoinLeftButton().setImageResource(R.drawable.ic_baseline_person_remove_24);
        if (activity != null) {
            getJoinLeftButton().setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.red_fs)));
        }
    }

    /**
     * Set source image and color (green) for join the group.
     *
     * @author Daniele Dotto
     * @see "{@link #joinLeftButton}"
     * @since 1.1
     */
    private void setButtonInfoAsNoParticipant() {
        getJoinLeftButton().setImageResource(R.drawable.ic_baseline_person_add_24);
        if (activity != null) {
            getJoinLeftButton().setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.light_green_fs)));
        }
    }

    /**
     * Check if current user is already member of "{@link #thisGroup}".
     *
     * @return true: current user is already member ||| false: current user is not member yet
     * @author Daniele Dotto
     * @since 1.1
     */
    private boolean checkGroupMember() {
        executeAsync(() -> getThisGroup().getMaterializedMembers().contains(user), new TaskResult<Boolean>() {

            @Override
            public void onComplete(@NonNull Boolean result) {
                status = result;
            }

            @Override
            public void onError(@NonNull Exception e) {
                try {
                    throw new Exception(e);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        return status;
    }

    /**
     * Return the entire group only if that is not null.
     *
     * @return "{@link #thisGroup}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    Group getThisGroup() {
        return Objects.requireNonNull(thisGroup);
    }

    /**
     * Return the button that allow to left or join the group (only if that is not null).
     *
     * @return "{@link #joinLeftButton}"
     * @author Daniele Dotto
     * @since 1.1
     */
    private @NonNull
    FloatingActionButton getJoinLeftButton() {
        return Objects.requireNonNull(joinLeftButton);
    }
}