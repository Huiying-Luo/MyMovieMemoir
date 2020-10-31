package com.laverne.mymoviememoir;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.regex.Pattern;

public class AddCinemaActivity extends AppCompatActivity {

    private static final Pattern POSTCODE_PATTERN = Pattern.compile("^[0-9]{4}$");

    private TextInputLayout nameTIL;
    private EditText nameEditText;
    private TextInputLayout postcodeTIL;
    private EditText postcodeEditText;
    private Button submitBtn;

    private String cineId;
    private String cineName;
    private String cinePostcode;
    private boolean cinemaExist;

    private NetworkConnection networkConnection = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cinema);
        setTitle("Add Cinema");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configureUI();

        networkConnection = new NetworkConnection();
        GetMaxCineIdTask getMaxCineIdTask = new GetMaxCineIdTask();
        getMaxCineIdTask.execute();

        setUpSubmitBtn();
    }


    private void configureUI() {
        nameTIL = findViewById(R.id.til_cinema_name);
        nameEditText = findViewById(R.id.et_cinema_name);
        nameEditText.addTextChangedListener(new nameEditTextWatcher());
        postcodeTIL = findViewById(R.id.til_cinema_postcode);
        postcodeEditText = findViewById(R.id.et_cinema_postcode);
        postcodeEditText.addTextChangedListener(new postcodeEditTextWatcher());
        submitBtn = findViewById(R.id.btn_cinema_submit);

        configurePostcodeEditText();
    }


    private void setUpSubmitBtn() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cineName = nameEditText.getText().toString().trim();
                cinePostcode = postcodeEditText.getText().toString().trim();
                boolean dataValid = validateData();
                if (dataValid) {
                    CheckCinemaExistAndPostTask checkCinemaExistAndPostTask = new CheckCinemaExistAndPostTask();
                    checkCinemaExistAndPostTask.execute(cineName);
                }
            }
        });
    }


    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private class GetMaxCineIdTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return networkConnection.findMaxId("cinema");
        }

        @Override
        protected void onPostExecute(String maxId) {
            if (maxId == null || maxId.length() == 0) {
                Utilities.showAlertDialogwithOkButton(AddCinemaActivity.this, "Error", "Something went wrong, please try again later.");
            } else {
                int id = Integer.parseInt(maxId) + 1;
                cineId = String.valueOf(id);
            }
        }
    }


    private class CheckCinemaExistAndPostTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... details) {
            return networkConnection.findCinemaByCineName(details[0]);
        }


        @Override
        protected void onPostExecute(String cinema) {
            if (cinema == null) {
                Utilities.showAlertDialogwithOkButton(AddCinemaActivity.this, "Error", "Something went wrong, please try again later.");
            } else {
                try {
                    JSONArray jsonArray = new JSONArray(cinema);
                    if (jsonArray.length() == 0) {
                        // this cinema is not in the server database
                        // post to server database
                        createAddCinemaTask();

                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String postcode = jsonObject.getString("cinePostcode");
                            if (postcode.equals(cinePostcode)) {
                                // same name, same postcode = same cinema
                                Utilities.showAlertDialogwithOkButton(AddCinemaActivity.this, "Error", "This cinema already exist in the list.");
                            } else {
                                // post to server database
                                createAddCinemaTask();
                            }
                        }
                    }
                } catch (JSONException e) {
                    Utilities.showAlertDialogwithOkButton(AddCinemaActivity.this, "Error", "Something went wrong, please try again later.");
                    e.printStackTrace();
                }
            }
        }
    }


    private void createAddCinemaTask() {
        String[] details = {cineId, cineName, cinePostcode};
        AddCinemaTask addCinemaTask = new AddCinemaTask();
        addCinemaTask.execute(details);
    }


    private class AddCinemaTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... details) {
            return networkConnection.addCinema(details);
        }


        @Override
        protected void onPostExecute(Integer resultcode) {
            if (resultcode == 0) {
                // create a alert dialog to inform user
                AlertDialog.Builder alert = new AlertDialog.Builder(AddCinemaActivity.this);
                alert.setTitle("Success");
                alert.setMessage("Cinema added successfully!");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // go back to create memoir screen
                        Intent resultIntent = new Intent(AddCinemaActivity.this, CreateMemoirActivity.class);
                        resultIntent.putExtra("cineName", cineName);
                        resultIntent.putExtra("cinePostcode", cinePostcode);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                });
                alert.create().show();
            } else {
                Utilities.showAlertDialogwithOkButton(AddCinemaActivity.this, "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private boolean validateData() {
        boolean cineNameEmpty = false;
        if (cineName.length() == 0) {
            cineNameEmpty = true;
            nameTIL.setError("* Field cannot be empty");
        }
        if (validatePostcode(cinePostcode) && !cineNameEmpty) {
            return true;
        } else {
            return false;
        }
    }


    private boolean validatePostcode(String postcode) {
        if (postcode.isEmpty()) {
            postcodeTIL.setError("* Field cannot be empty");
            return false;
        } else if (!POSTCODE_PATTERN.matcher(postcode).matches()) {
            postcodeTIL.setError("*Invalid Postcode");
            return false;
        }
        return true;
    }


    private class nameEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            nameTIL.setErrorEnabled(false);
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


    private void configurePostcodeEditText() {
        postcodeEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) AddCinemaActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(postcodeEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }
}
