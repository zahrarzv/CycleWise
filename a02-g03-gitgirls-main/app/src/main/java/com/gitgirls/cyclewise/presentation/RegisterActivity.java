package com.gitgirls.cyclewise.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gitgirls.cyclewise.R;
import com.gitgirls.cyclewise.application.PeriodTracker;
import com.gitgirls.cyclewise.logic.userlogin.AuthResult;
import com.gitgirls.cyclewise.logic.userlogin.AuthService;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText usernameEditText;
    private EditText birthdateEditText;
    private EditText passwordEditText;
    private Button registerButton;
    private TextView loginText;

    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authService = ((PeriodTracker) getApplication()).getAuthService();

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        birthdateEditText = findViewById(R.id.birthdateEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginText = findViewById(R.id.loginText);

        registerButton.setOnClickListener(v -> handleRegister());
        loginText.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String birthdate = birthdateEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError(getString(R.string.register_first_name_required));
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError(getString(R.string.register_last_name_required));
            return;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError(getString(R.string.register_username_required));
            return;
        }

        if (TextUtils.isEmpty(birthdate)) {
            birthdateEditText.setError(getString(R.string.register_birthdate_required));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.register_password_required));
            return;
        }

        try {
            AuthResult result = authService.register(
                    username,
                    password,
                    firstName,
                    lastName,
                    birthdate
            );

            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();

            if (result.isSuccess()) {
                setResult(RESULT_OK);
                finish();
            }

        } catch (IllegalArgumentException | IllegalStateException ex) {
            String message = ex.getMessage() != null ? ex.getMessage() : getString(R.string.register_failed_default);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
}