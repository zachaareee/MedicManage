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
import androidx.navigation.NavOptions; // Import this
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
        NavigationUI.setupWithNavController(binding.navView, navController);


        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Prevent re-selecting the same item, which can cause issues.
            if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == itemId) {
                return false;
            }

            if (itemId == R.id.navigation_profile) {
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", username);
                bundle.putString("USER_TYPE", userType);

                // **THE FIX IS HERE:**
                // Create NavOptions to mimic the default behavior of popping the
                // back stack to the start destination. This keeps navigation consistent.
                NavOptions navOptions = new NavOptions.Builder()
                        .setLaunchSingleTop(true) // Don't re-create a fragment if it's already on top
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), false)
                        .build();

                // Navigate to profile using the bundle AND the new options
                navController.navigate(R.id.navigation_profile, bundle, navOptions);
                return true; // We've handled this item.
            }

            // For all other items, use the default helper method
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }
}