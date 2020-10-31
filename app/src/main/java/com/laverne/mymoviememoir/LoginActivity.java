package com.laverne.mymoviememoir;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    NetworkConnection networkConnection = null;
    private String pwdEnteredHash;
    private TextView errorTextView;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextInputLayout usernameTIL;
    private TextInputLayout passwordTIL;
    private Button loginBtn;
    private Button signUpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        configureUI();
        networkConnection = new NetworkConnection();

        setUpLoginBtn();
        setUpSignUpBtn();
    }


    private void configureUI() {
        usernameEditText = findViewById(R.id.et_username);
        usernameEditText.addTextChangedListener(new usernameEditTextWatcher());
        passwordEditText = findViewById(R.id.et_pwd);
        passwordEditText.addTextChangedListener(new passwordEditTextWatcher());
        usernameTIL = findViewById(R.id.til_username);
        passwordTIL = findViewById(R.id.til_pwd);
        errorTextView = findViewById(R.id.tv_error);
        loginBtn = findViewById(R.id.btn_login);
        signUpBtn = findViewById(R.id.btn_signup);

        configurePwdEditText();
    }


    private void setUpSignUpBtn() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }


    private void setUpLoginBtn() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String pwdEntered = passwordEditText.getText().toString();
                try {
                    pwdEnteredHash = Utilities.getMD5Hash(username + pwdEntered);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                if (!username.isEmpty() && !pwdEntered.isEmpty()) {
                    // authentication
                    new GetCredentialsByUsernameTask().execute(username);
                } else {
                    if (pwdEntered.isEmpty()) {
                        passwordTIL.setError("* Password cannot be empty!");
                    }
                    if (username.isEmpty()) {
                        usernameTIL.setError("* Username cannot be empty!");
                    }
                }
            }
        });
    }


    private class GetCredentialsByUsernameTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return networkConnection.findCredentialByUsername(params[0]);
        }


        @Override
        protected void onPostExecute(String credentials) {
            if (credentials != null) {
                try {
                    JSONArray jsonArray = new JSONArray(credentials);
                    if (jsonArray.length() != 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String pwdHash = jsonObject.getString("credPasswordhash");
                        String credId = jsonObject.getString("credId");
                        Log.i("pwd", pwdHash);
                        Log.i("enterpwd", pwdEnteredHash);
                        if (pwdHash.equals(pwdEnteredHash)) {

                            // authentication success, start the MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("credId", credId);
                            startActivity(intent);
                        } else {
                            errorTextView.setText("* Invalid Username or Password.");
                        }
                    } else {
                        errorTextView.setText("* Username does not exist!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorTextView.setText("* Something went wrong, please try again later.");
                }
            } else {
                errorTextView.setText("* Something went wrong, please try again later.");
            }
        }
    }


    private void configurePwdEditText() {
        passwordEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) LoginActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }


    private class usernameEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            usernameTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class passwordEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            passwordTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}

