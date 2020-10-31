package com.laverne.mymoviememoir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.textfield.TextInputLayout;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CreateMemoirActivity extends AppCompatActivity {

    private final static int REQUEST_CODE = 777;

    private TextView movieNameTextView;
    private TextView releaseDateTextView;
    private ImageView imageView;
    private TextInputLayout dateTIL;
    private EditText dateEditText;
    private TextInputLayout timeTIL;
    private EditText timeEditText;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    private TextView spinnerErrorTextView;
    private EditText commentEditText;
    private RatingBar ratingBar;
    private Button submitBtn;
    private TextView addNewCinemaTextView;
    private TextView ratingErrorTextView;

    private String watchedDate = null;
    private String watchedTime = null;
    private String cineName = null;
    private String ratingScore = null;
    private String memId;
    private String userId;
    private String movieName;
    private String releaseDate;
    private String cineId;

    private NetworkConnection networkConnection = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memoir);

        networkConnection = new NetworkConnection();
        // get max memoir id in database
        GetMaxMemIdTask getMaxMemIdTask = new GetMaxMemIdTask();
        getMaxMemIdTask.execute();

        configureUI();
        // configure spinner
        final GetAllCinemaTask getAllCinemaTask = new GetAllCinemaTask();
        getAllCinemaTask.execute();

        setValuesFromPreviousActivity();

        setDatetimeDialog();

        configureAddCinemaLink();

        configureRatingBar();

        configureSubmitButton();
    }


    private void configureUI() {
        setTitle("Create Memoir");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        movieNameTextView = findViewById(R.id.tv_create_memoir_name);
        releaseDateTextView = findViewById(R.id.tv_create_memoir_release);
        imageView = findViewById(R.id.create_memoir_image);
        dateTIL = findViewById(R.id.til_create_memoir_date);
        dateEditText = findViewById(R.id.et_create_memoir_date);
        dateEditText.addTextChangedListener(new dateEditTextWatcher());
        timeTIL = findViewById(R.id.til_create_memoir_time);
        timeEditText = findViewById(R.id.et_create_memoir_time);
        timeEditText.addTextChangedListener(new timeEditTextWatcher());
        commentEditText = findViewById(R.id.et_create_memoir_comment);
        spinner = findViewById(R.id.create_memoir_spinner);
        addNewCinemaTextView = findViewById(R.id.tv_add_cinema);
        spinnerErrorTextView = findViewById(R.id.tv_spinner_error_create_memoir);
        ratingBar = findViewById(R.id.create_memoir_rating_bar);
        ratingErrorTextView = findViewById(R.id.tv_create_memoir_rating);
        submitBtn = findViewById(R.id.btn_memoir_submit);

        configureCommentEditText();
    }


    // Back button in action bar
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void setValuesFromPreviousActivity() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        movieName = bundle.getString("movieName");
        movieNameTextView.setText(movieName);
        releaseDate = bundle.getString("releaseDate");
        releaseDateTextView.setText("Release Date: " + releaseDate);
        String imageSrc = bundle.getString("imageSrc");
        userId = bundle.getString("userId");
        // setImage
        Picasso.get().load(imageSrc)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView);
    }


    private void configureRatingBar() {
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingScore = String.valueOf(rating);
            }
        });
    }


    private void configureAddCinemaLink() {
        // configure add cinema link
        String text = "Not in the list? Click here to add one.";
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        addNewCinemaTextView.setText(content);
        // add new cinema
        addNewCinemaTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateMemoirActivity.this, AddCinemaActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }


    // after user adding a new cinema, update the spinner
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE) {
            String newCineName = data.getStringExtra("cineName");
            String newPostcode = data.getStringExtra("cinePostcode");
            String item = newCineName + ", " + newPostcode;
            spinnerAdapter.add(item);
            spinnerAdapter.notifyDataSetChanged();
            spinner.setSelection(spinnerAdapter.getPosition(item), true);

        }
    }


    private void setDatetimeDialog() {
        Calendar calendar = Calendar.getInstance();
        // get current date and set popup date picker
        Date today = calendar.getTime();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateMemoirActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        watchedDate = year + "-" + monthStr + "-" + dayStr;
                        dateEditText.setText(watchedDate);
                    }
                }, year, month, day);
                // memoir records the movie you have watched!
                datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        timeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateMemoirActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hourStr = "";
                        String minStr = "";
                        if (hourOfDay < 10) {
                            hourStr = "0" + hourOfDay;
                        } else {
                            hourStr = String.valueOf(hourOfDay);
                        }
                        if (minute < 10) {
                            minStr = "0" + minute;
                        } else {
                            minStr = String.valueOf(minute);
                        }
                        watchedTime = hourStr + ":" + minStr + ":00";
                        timeEditText.setText(watchedTime);
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }


    private void configureSubmitButton() {
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                if (watchedDate != null && watchedTime != null && cineName != null && ratingScore != null) {
                    GetCinemaByNameTask getCinemaByNameTask = new GetCinemaByNameTask();
                    getCinemaByNameTask.execute(cineName);

                }
                if (watchedDate == null) {
                    dateTIL.setError("* Please select a date.");
                }
                if (watchedTime == null) {
                    timeTIL.setError("* Please select a time");
                }
                if (cineName == null || cineName.equals("Select a Cinema")) {
                    spinnerErrorTextView.setText("* Please select a cinema or add a new one.");
                }
                if (ratingScore == null) {
                    ratingErrorTextView.setText("Please Give a Score!");
                    ratingErrorTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        });
    }


    private class GetAllCinemaTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return networkConnection.getAll("cinema");
        }

        @Override
        protected void onPostExecute(String cinema) {
            List<String> cinemaList = new ArrayList<>();
            cinemaList.add("Select the Cinema");
            if (cinema != null) {
                try {
                    JSONArray jsonArray = new JSONArray(cinema);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String cineName = jsonObject.getString("cineName");
                        String cinePostcode = jsonObject.getString("cinePostcode");
                        cinemaList.add(cineName + ", " + cinePostcode);
                    }
                    configureSpinner(cinemaList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialogwithOkButton(CreateMemoirActivity.this, "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private class GetCinemaByNameTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... name) {
            return networkConnection.getCinemaByName(name[0]);
        }

        @Override
        protected void onPostExecute(String cinema) {
            if(cinema != null) {
                try {
                    JSONArray jsonArray = new JSONArray(cinema);
                    if (jsonArray.length() != 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        cineId = jsonObject.getString("cineId");

                        String comment = commentEditText.getText().toString().trim();
                        String memDatetime = watchedDate + "T" + watchedTime + "+11:00";
                        releaseDate = releaseDate + "T00:00:00+10:00";
                        String[] details = {memId, movieName, releaseDate, memDatetime, ratingScore, comment, cineId, userId};
                        // after retrieving the cinema id, add memoir
                        AddMemoirTask addMemoirTask = new AddMemoirTask();
                        addMemoirTask.execute(details);
                    } else {
                        Utilities.showAlertDialogwithOkButton(CreateMemoirActivity.this, "Error", "Something went wrong, please try again later.");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Utilities.showAlertDialogwithOkButton(CreateMemoirActivity.this, "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private void configureSpinner(List<String> list) {
        spinnerAdapter = new ArrayAdapter<String>(this,  R.layout.spinner_item, list) {
            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                }else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view =  super.getDropDownView(position, convertView, parent);
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
                    String[] cinema = parent.getItemAtPosition(position).toString().split(", ");
                    cineName = cinema[0];
                    spinnerErrorTextView.setText("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private class GetMaxMemIdTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return networkConnection.findMaxId("memoir");
        }
        @Override
        protected void onPostExecute(String maxId) {
            if (maxId == null || maxId.length() == 0) {
                Utilities.showAlertDialogwithOkButton(CreateMemoirActivity.this, "Error", "Something went wrong, please try again later.");
            } else {
                int id = Integer.parseInt(maxId) + 1;
                memId = String.valueOf(id);
            }
        }
    }


    private class AddMemoirTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... details) {
            return networkConnection.addMemoir(details);
        }

        @Override
        protected void onPostExecute(Integer resultcode) {
            if (resultcode == 0) {
                // create a alert dialog let user know create success
                AlertDialog.Builder alert = new AlertDialog.Builder(CreateMemoirActivity.this);
                alert.setTitle("Success");
                alert.setMessage("This memoir has been created successfully!");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(666);
                        finish();
                    }
                });
                alert.create().show();
            } else {
                Log.i("addResult", resultcode.toString());
                Utilities.showAlertDialogwithOkButton(CreateMemoirActivity.this, "Error", "Something went wrong, please try again later.");
            }
        }
    }


    private class dateEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            dateTIL.setErrorEnabled(false);
        }
        @Override
        public void afterTextChanged(Editable s) {}
    }


    private class timeEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            timeTIL.setErrorEnabled(false);
        }
        @Override
        public void afterTextChanged(Editable s) {}
    }


    private void configureCommentEditText() {
        commentEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // hide virtual keyboard
                    InputMethodManager imm = (InputMethodManager) CreateMemoirActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }
}
