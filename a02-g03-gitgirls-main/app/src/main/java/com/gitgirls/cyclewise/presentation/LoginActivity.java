package com.gitgirls.cyclewise.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.userlogin.AuthResult;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;
import com.gitgirls.cyclewise.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerText;

    private AuthService authService;

    private final ActivityResultLauncher<Intent> registerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            clearLoginFields();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authService = ((PeriodTracker) getApplication()).getAuthService();

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        loginButton.setOnClickListener(v -> handleLogin());

        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            registerLauncher.launch(intent);
        });
    }

    private void handleLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.login_username_required));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.login_password_required));
            return;
        }

        try {
            AuthResult result = authService.login(username, password);

            if (!result.isSuccess()) {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            User loggedInUser = authService.getUserByUsername(username);

            if (loggedInUser == null) {
                Toast.makeText(this, getString(R.string.login_failed_user_data), Toast.LENGTH_LONG).show();
                return;
            }

            ((PeriodTracker) getApplication()).setLoggedInUser(loggedInUser);

            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, PeriodLogActivity.class);
            startActivity(intent);
            finish();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            String message = ex.getMessage() != null ? ex.getMessage() : getString(R.string.login_failed_default);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void clearLoginFields() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        usernameEditText.setError(null);
        passwordEditText.setError(null);
    }
}