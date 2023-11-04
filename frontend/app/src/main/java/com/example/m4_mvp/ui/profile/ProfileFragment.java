package com.example.m4_mvp.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m4_mvp.ProfileViewModel;
import com.example.m4_mvp.R;
import com.example.m4_mvp.databinding.FragmentProfileBinding;
import com.example.m4_mvp.databinding.FragmentRecommendBinding;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProfileFragment extends Fragment {
    final static String TAG = "ProfileFragment";

    private ProfileViewModel profileViewModel;

    private FragmentProfileBinding binding;
    private View profileView;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<String> networkTaskResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get the data binding and view
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        profileView = root;

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "current max distance: " + profileViewModel.getMaxDistance());

        TextView email = view.findViewById(R.id.emailAddress);
        email.setText(profileViewModel.getGoogleAccount().getEmail());

        view.findViewById(R.id.updateProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Update Profile!");

                networkTaskResult = executorService.submit(() -> {
                    try {
                        URL url = new URL(getResources().getString(R.string.profile_url) + "/update/" + profileViewModel.getuid());
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        // Set up the connection for a DELETE request
                        connection.setRequestMethod("PUT");
                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(15000);

                        // Set the request body
                        connection.setDoOutput(true);
                        // TODO: check valid input range
                        EditText inputDistance = profileView.findViewById(R.id.maxDistInput);
                        String requestBody = "{\"email\": \"" + profileViewModel.getGoogleAccount().getEmail() +
                                "\", \"distance\": \"" + inputDistance.getText().toString() + "\", \"notificationToken\": \"" + profileViewModel.getToken() + "\"}";

                        // Set up the request body
                        byte[] requestBytes = requestBody.getBytes("UTF-8");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Content-Length", String.valueOf(requestBytes.length));

                        // Write the JSON data to the connection
                        OutputStream out = connection.getOutputStream();
                        out.write(requestBytes);
                        out.close();

                        // Get the response code
                        int responseCode = connection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            Log.d(TAG, "profile updated");
                            Toast.makeText(requireActivity(), "Profile updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "profile update failed with response code: " + responseCode);
                        }

                        connection.disconnect();
                        return null;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                });

                // On success, update profileViewModel
                //TODO: make sure the distance input is not null
//                EditText maxDistance = profileView.findViewById(R.id.maxDistInput);
//                profileViewModel.setMaxDistance(maxDistance.toString());
            }
        });
    }
}