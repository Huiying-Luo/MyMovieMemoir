package com.laverne.mymoviememoir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9!@#$%^&*()_+-=,./;']{8,}$");
    private static final Pattern POSTCODE_PATTERN = Pattern.compile("^[0-9]{4}$");
    private TextView errorTextView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private TextView radioErrorTextView;
    private TextView stateErrorTextView;
    private EditText et_DoB;
    private Spinner spinner;
    private TextInputLayout firstNameTIL;
    private TextInputLayout lastNameTIL;
    private TextInputLayout addressTIL;
    private TextInputLayout postcodeTIL;
    private TextInputLayout emailTIL;
    private TextInputLayout passwordTIL;
    private TextInputLayout confirmPwdTIL;
    private TextInputLayout dobTIL;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText addressEditText;
    private EditText postcodeEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPwdEditText;
    private EditText dobEditText;
    private String gender = null;
    private String state = null;
    private String dob = null;
    private NetworkConnection networkConnection = null;
    private String credId;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordhash;
    private String address;
    private String postcode;
    private String currentDate;
    private Button signUpButton;
    private Calendar calendar = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        configureUI();
        // hide the soft keyboard on enter key
        configureConfirmPwdEditText();

        networkConnection = new NetworkConnection();
        new GetMaxIdTask().execute();

        setUpDatePicker();

        configureSpinner();
        configureSignUpBtn();
    }


    private void configureUI() {
        getSupportActionBar().setTitle("Sign Up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firstNameTIL = findViewById(R.id.til_firstname);
        lastNameTIL = findViewById(R.id.til_lastname);
        addressTIL = findViewById(R.id.til_address);
        postcodeTIL = findViewById(R.id.til_postcode);
        emailTIL = findViewById(R.id.til_email);
        passwordTIL = findViewById(R.id.til_pwd);
        confirmPwdTIL = findViewById(R.id.til_pwd_confirm);
        dobTIL = findViewById(R.id.til_dob);

        firstNameEditText = findViewById(R.id.et_firstName);
        firstNameEditText.addTextChangedListener(new firstNameEditTextWatcher());
        lastNameEditText = findViewById(R.id.et_lastName);
        lastNameEditText.addTextChangedListener(new lastNameEditTextWatcher());
        dobEditText = findViewById(R.id.et_DoB);
        dobEditText.addTextChangedListener(new dobEditTextWatcher());
        addressEditText = findViewById(R.id.et_address);
        addressEditText.addTextChangedListener(new addressEditTextWatcher());
        postcodeEditText = findViewById(R.id.et_postcode);
        postcodeEditText.addTextChangedListener(new postcodeEditTextWatcher());
        emailEditText = findViewById(R.id.et_email);
        emailEditText.addTextChangedListener(new emailEditTextWatcher());
        passwordEditText = findViewById(R.id.et_pwd);
        passwordEditText.addTextChangedListener(new passwordEditTextWatcher());
        confirmPwdEditText = findViewById(R.id.et_pwd_confirm);
        confirmPwdEditText.addTextChangedListener(new confirmPwdEditTextWatcher());
        signUpButton = findViewById(R.id.btn_signup);
        radioGroup = findViewById(R.id.radioGroup);
        et_DoB = findViewById(R.id.et_DoB);
    }


    private void configureConfirmPwdEditText() {
        confirmPwdEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) SignupActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(confirmPwdEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }


    private void configureSignUpBtn() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (validateAllData()) {
                        Date today = calendar.getTime();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        currentDate = dateFormat.format(today) + "T00:00:00+11:00";
                        String[] details = {credId, email, passwordhash, currentDate};
                        // check username exist or not, if not, create credentials and userprofile
                        new CheckUsernameExistTask().execute(details);
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean validateAllData() throws NoSuchAlgorithmException {
        firstName = firstNameEditText.getText().toString();
        boolean validFirstName = true;
        lastName = lastNameEditText.getText().toString();
        boolean validLastName = true;
        address = addressEditText.getText().toString();
        boolean validAddress = true;
        postcode = postcodeEditText.getText().toString();
        boolean validPostcode = validatePostcode(postcode);
        email = emailEditText.getText().toString();
        boolean validEmail = validateEmail(email);
        String password = passwordEditText.getText().toString();
        boolean validPassword = validatePassword(password);
        String confirmPwd = confirmPwdEditText.getText().toString();
        boolean comparePwd = compairPassword(password, confirmPwd);

        if (firstName.isEmpty()) {
            firstNameTIL.setError("*Field cannot be empty");
            validFirstName = false;
        }
        if (lastName.isEmpty()) {
            lastNameTIL.setError("*Field cannot be empty");
            validLastName = false;
        }
        if (address.isEmpty()) {
            addressTIL.setError("*Field cannot be empty");
            validAddress = false;
        }
        if (gender == null) {
            radioErrorTextView = findViewById(R.id.tv_radioError);
            radioErrorTextView.setText("* Please Select Your Gender");
        }
        if (state == null) {
            stateErrorTextView = findViewById(R.id.tv_stateError);
            stateErrorTextView.setText("* Please Select a State");
        }
        if (dob == null) {
            dobTIL.setError("* Please Select a Date");
        }
        if (validFirstName && validLastName && validAddress && validEmail && validPassword && comparePwd && validPostcode
                && gender != null && state != null && dob != null) {
            passwordhash = Utilities.getMD5Hash(email + password);
            return true;
        } else {
            return false;
        }
    }


    private void setUpDatePicker() {
        et_DoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current date and set popup date picker
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignupActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        String monthStr = "";
                        String dayStr = "";
                        if (month < 10) {
                            monthStr = "0" + month;
                        } else {
                            monthStr = String.valueOf(month);
                        }
                        if (dayOfMonth < 10) {
                            dayStr = "0" + dayOfMonth;
                        } else {
                            dayStr = String.valueOf(dayOfMonth);
                        }
                        dob = year + "-" + monthStr + "-" + dayStr;
                        et_DoB.setText(dob);
                        dob += "T00:00:00+11:00";
                    }
                }, year, month, day);
                // cannot choose future date as birthday!
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });
    }


    private void configureSpinner() {
        List<String> stateList = new ArrayList<String>(Arrays.asList(new String[]{"Select a State", "NSW", "QLD", "SA", "TSA", "VIC", "WA", "ACT", "JBT", "NT"}));
        spinner = findViewById(R.id.stateSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, stateList) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }


            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    state = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void onRadioButtonClick(View view) {
        int checkButtonId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(checkButtonId);
        gender = radioButton.getText().toString().toLowerCase();
    }


    private class AddCredentialsTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... details) {
            return networkConnection.addCredentials(details);
        }


        @Override
        protected void onPostExecute(Integer resultcode) {
            if (resultcode == 0) {
                String[] details = {credId, firstName, lastName, gender, dob, address, state, postcode};

                new AddUserTask().execute(details);
            } else {
                Utilities.showAlertDialogwithOkButton(SignupActivity.this, "Error", "Something went wrong, please try agian later.");
            }
        }
    }


    private class AddUserTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... details) {
            return networkConnection.addUser(details);
        }


        @Override
        protected void onPostExecute(Integer resultcode) {
            if (resultcode == 0) {
                // create a alert dialog let user know create success
                AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                alert.setTitle("Success");
                alert.setMessage("Sign up successfully!");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // add user to server successfully, start the MainActivity
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        intent.putExtra("credId", credId);
                        startActivity(intent);
                    }
                });
                alert.create().show();
            } else {
                Utilities.showAlertDialogwithOkButton(SignupActivity.this, "Error", "Something went wrong, please try agian later.");
            }
        }
    }


    private class GetMaxIdTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return networkConnection.findMaxId("credentials");
        }


        @Override
        protected void onPostExecute(String maxId) {
            if (maxId == null || maxId.length() == 0) {
                Utilities.showAlertDialogwithOkButton(SignupActivity.this, "Error", "Something went wrong, please try again later.");
            } else {
                int id = Integer.parseInt(maxId) + 1;
                credId = String.valueOf(id);
            }
        }
    }


    private class CheckUsernameExistTask extends AsyncTask<String, Void, String> {
        String [] details;

        @Override
        protected String doInBackground(String... params) {
            details = params;
            return networkConnection.findCredentialByUsername(details[1]);
        }


        @Override
        protected void onPostExecute(String credentials) {
            if (credentials != null) {
                try {
                    JSONArray jsonArray = new JSONArray(credentials);
                    if (jsonArray.length() != 0) {
                        emailTIL.setError("* This email has been registered！");
                    } else {
                        new AddCredentialsTask().execute(details);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Utilities.showAlertDialogwithOkButton(SignupActivity.this, "Error", "Something went wrong, please try again later.");
                }
            } else {
                Utilities.showAlertDialogwithOkButton(SignupActivity.this, "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            emailTIL.setError("* Field cannot be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTIL.setError("* Please enter a valid email address.");
            return false;
        } else {
            return true;
        }
    }


    private boolean validatePassword(String pwd) {
        if (pwd.isEmpty()) {
            passwordTIL.setError("*Field cannot be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pwd).matches()) {
            passwordTIL.setError("* Minimum eight characters, at least one letter and one number");
            return false;
        }
        return true;
    }


    private boolean compairPassword(String pwd, String confirmPwd) {
        if (confirmPwd.isEmpty()) {
            confirmPwdTIL.setError("*Field cannot be empty");
            return false;
        }
        if (pwd.compareTo(confirmPwd) == 0) {
            return true;
        } else {
            confirmPwdTIL.setError("* Password does not match!");
            return false;
        }
    }


    private boolean validatePostcode(String postcode) {
        if (postcode.isEmpty()) {
            postcodeTIL.setError("*Field cannot be empty");
            return false;
        } else if (!POSTCODE_PATTERN.matcher(postcode).matches()) {
            postcodeTIL.setError("*Invalid Postcode");
            return false;
        }
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private class firstNameEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            firstNameTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class lastNameEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            lastNameTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class addressEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addressTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class postcodeEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            postcodeTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class emailEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            emailTIL.setErrorEnabled(false);
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


    private class dobEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            dobTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class confirmPwdEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            confirmPwdTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
