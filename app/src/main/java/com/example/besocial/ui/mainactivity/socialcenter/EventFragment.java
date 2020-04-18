package com.example.besocial.ui.mainactivity.socialcenter;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.example.besocial.ConstantValues;
import com.example.besocial.R;
import com.example.besocial.data.Event;
import com.example.besocial.data.LiteUserDetails;
import com.example.besocial.databinding.FragmentEventBinding;
import com.example.besocial.ui.mainactivity.MainActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment {
    private static final String TAG = "EventFragment";
    private FragmentEventBinding binding;
    private Event chosenEvent;
    private SocialCenterViewModel socialCenterViewModel;
    private boolean isUserAttending = false;
    private boolean isCheckInPossible = false;


    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        socialCenterViewModel = ViewModelProviders.of(getActivity()).get(SocialCenterViewModel.class);
        chosenEvent = socialCenterViewModel.getEvent().getValue();
        setListeners();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(ConstantValues.USERS_ATTENDING_TO_EVENTS)
                .child(MainActivity.getLoggedUser().getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String path = "/" + chosenEvent.getEventId();
                if (dataSnapshot.hasChild(path)) {
                    isUserAttending = true;
                    Log.d(TAG, "user attending");
                }
                Log.d(TAG, "user not attending");
                setAttendingButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setAttendingButton() {
        //if user is not the host
        if (!chosenEvent.getEventCreatorUid().equals(MainActivity.getLoggedUser().getUserId())) {
            Log.d(TAG, "user is not the host");
            //check if user is attending
            if (isUserAttending) {
                Log.d(TAG, "set to check in");
                binding.fragmentEventAttendBtn.setText("Check in");
            }
            Log.d(TAG, "set to visible");
            binding.fragmentEventAttendBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setListeners() {
        binding.fragmentEventAttendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendToEvent();
            }
        });
    }

    private void attendToEvent() {
        binding.fragmentEventAttendBtn.setEnabled(false);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        LiteUserDetails user = new LiteUserDetails(MainActivity.getLoggedUser().getProfileImage()
                , MainActivity.getLoggedUser().getUserId()
                , MainActivity.getLoggedUser().getUserFirstName()
                , MainActivity.getLoggedUser().getUserLastName());

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/" + ConstantValues.USERS_ATTENDING_TO_EVENTS
                + "/" + MainActivity.getLoggedUser().getUserId() + "/" + chosenEvent.getEventId(), chosenEvent);

        childUpdates.put("/" + ConstantValues.EVENTS_WITH_ATTENDINGS
                + "/" + chosenEvent.getEventId()+ "/" + MainActivity.getLoggedUser().getUserId(), user);

        databaseReference.updateChildren(childUpdates);
        Toast.makeText(getContext(), "Attending to this event!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onStart() {
        super.onStart();
        setEventDetails();
    }

    private void setEventDetails() {
        Glide.with(getContext()).load(chosenEvent.getStrEventPhotoUrl()).placeholder(R.drawable.social_event0).centerCrop().into(binding.fragmentEventPhoto);
        binding.fragmentEventTitle.setText(chosenEvent.getTitle());
        binding.fragmentEventHostFullName.setText("Hosted by: " + chosenEvent.getEventCreatorUserName());
        binding.fragmentEventDateTime.setText(chosenEvent.getBeginDate() + "," + chosenEvent.getBeginTime()
                + "-" + chosenEvent.getFinishDate() + "," + chosenEvent.getFinishTime());
        binding.fragmentEventLocation.setText("Location: " + chosenEvent.getLocationTitle());
        binding.fragmentEventDescription.setText(chosenEvent.getDescription());
    }
}
