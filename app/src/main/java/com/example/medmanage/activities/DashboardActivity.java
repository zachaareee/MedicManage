package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.medmanage.R;
import com.example.medmanage.databinding.DashboardBinding;
import com.example.medmanage.ui.profile.profile_fragment;

public class DashboardActivity extends AppCompatActivity {

    private DashboardBinding binding;
    private NavController navController;
    private String username;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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



        binding.navView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_profile) {

                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", username);
                bundle.putString("USER_TYPE", userType);


                try {
                    navController.navigate(R.id.navigation_profile, bundle);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }


            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }
}