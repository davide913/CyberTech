package it.unive.cybertech.groups;

import static it.unive.cybertech.utils.CachedUser.user;
import static it.unive.cybertech.utils.Utils.logout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.R;
import it.unive.cybertech.database.Groups.Group;
import it.unive.cybertech.utils.Utils;

public class GroupInfo extends Fragment {


    private TextView nameGroup;
    private TextView descriptionGroup;
    private TextView nUsers;
    private @Nullable
    GroupHomePage activity;
    private Group thisGroup;
    private FloatingActionButton joinLeftButton;
    private boolean status = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @NonNull View view = inflater.inflate(R.layout.fragment_group_information, container, false);

        // INFORMAZIONI
        nameGroup = view.findViewById(R.id.group_information_tab_name);
        descriptionGroup = view.findViewById(R.id.group_information_tab_description);
        nUsers = view.findViewById(R.id.group_information_tab_nUsers);

        activity = (GroupHomePage) getActivity();
        if (activity != null) {
            thisGroup = activity.getThisGroup();
            nameGroup.setText(thisGroup.getName());
            descriptionGroup.setText(thisGroup.getDescription());
            nUsers.setText(String.valueOf(thisGroup.getMembers().size()));
        }


        joinLeftButton = view.findViewById(R.id.group_information_joinLeftGroup);
        status = checkGroupMember();
        if (!status) {
            setButtonInfoAsNoPartecipant();
        } else {
            setButtonInfoAsPartecipant();
        }

        joinLeftButton.setOnClickListener(v -> {
            if (!status) {
                addGroupPartecipant();
            } else {
                removeGroupPartecipant();
            }
            @NonNull Handler handler = new Handler();
            handler.postDelayed(()-> {
                @NonNull Intent intent = new Intent(getContext(), GroupHomePage.class);
                intent.putExtra("ID", thisGroup.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }, 800);
        });

        return view;
    }

    private void removeGroupPartecipant() {
        @NonNull Thread t = new Thread(() -> {
            try {
                thisGroup.removeMember(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        showShortToast(getString(R.string.LeftGroup));
        setButtonInfoAsNoPartecipant();
        status = false;
    }

    private void addGroupPartecipant() {
        @NonNull Thread t = new Thread(() -> {
            try {
                thisGroup.addMember(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        showShortToast(getString(R.string.NowMember));
        setButtonInfoAsNoPartecipant();
        status = true;
    }

    private void setButtonInfoAsPartecipant() {
        joinLeftButton.setImageResource(R.drawable.ic_baseline_person_remove_24);
        if (activity != null) {
            joinLeftButton.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.red_fs)));
        }
    }

    private void setButtonInfoAsNoPartecipant() {
        joinLeftButton.setImageResource(R.drawable.ic_baseline_person_add_24);
        if (activity != null) {
            joinLeftButton.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.light_green_fs)));
        }
    }

    private boolean checkGroupMember() {
        @NonNull Thread t = new Thread(() -> {
            try {
                status = thisGroup.getMaterializedMembers().contains(user);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * Useful function that create and show a short-length toast (@see "{@link Toast}".
     *
     * @since 1.0
     */
    private void showShortToast(@NonNull String message) {
        @NonNull Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }
}