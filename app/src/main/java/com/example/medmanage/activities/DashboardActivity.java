package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions; // Make sure this import is here
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.medmanage.R;
import com.example.medmanage.databinding.DashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private DashboardBinding binding;
    private NavController navController;
    private String username;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.topBar.toolbar;
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        TextView greetingTextView = binding.topBar.greetingText;

        Intent intent = getIntent();
        userType = intent.getStringExtra("USER_TYPE");
        username = intent.getStringExtra("USERNAME");

        String greetingMessage = "Hi there 👋";

        if (userType != null) {
            if (userType.equals("student")) {
                String name = intent.getStringExtra("NAME");
                if (name != null && !name.isEmpty()) {
                    greetingMessage = "Hi " + name + " 👋";
                }
            } else if (userType.equals("nurse")) {
                String surname = intent.getStringExtra("SURNAME");
                if (surname != null && !surname.isEmpty()) {
                    greetingMessage = "Hi Nurse " + surname + " 👋";
                }
            }
        }
        greetingTextView.setText(greetingMessage);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_schedule, R.id.navigation_profile)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // We still call this to link the NavController, but we override the listener below
        NavigationUI.setupWithNavController(binding.navView, navController);


        // --- START OF THE FIX ---
        // Replace your setOnItemSelectedListener with this one.
        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Prevent re-selecting the same tab
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == itemId) {
                return false;
            }

            // These options are the key:
            // They pop the navigation stack back to your start destination (Home)
            // before navigating to the new tab. This is the standard behavior
            // you want for bottom navigation.
            NavOptions navOptions = new NavOptions.Builder()
                    .setLaunchSingleTop(true) // Don't re-create fragment if it's on top
                    .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                    .build();

            // Handle each tab manually to ensure consistent behavior

            if (itemId == R.id.navigation_profile) {
                // This is your special case with a bundle
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", username);
                bundle.putString("USER_TYPE", userType);
                navController.navigate(R.id.navigation_profile, bundle, navOptions);
                return true;

            } else if (itemId == R.id.navigation_home) {
                navController.navigate(R.id.navigation_home, null, navOptions);
                return true;

            } else if (itemId == R.id.navigation_schedule) {
                navController.navigate(R.id.navigation_schedule, null, navOptions);
                return true;
            }

            // Fallback for any other item
            return false;
        });
        // --- END OF THE FIX ---
    }
}
