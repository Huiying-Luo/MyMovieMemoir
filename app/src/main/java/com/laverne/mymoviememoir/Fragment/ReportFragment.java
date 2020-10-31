package com.laverne.mymoviememoir.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.TextInputLayout;
import com.laverne.mymoviememoir.CreateMemoirActivity;
import com.laverne.mymoviememoir.NetworkConnection.NetworkConnection;
import com.laverne.mymoviememoir.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ReportFragment extends Fragment {
    private TextInputLayout startDateTIL;
    private TextInputLayout endDateTIL;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private PieChart pieChart;
    private Button showBtn;
    private BarChart barGraph;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;
    // a dictionary stores the data used in piechart
    private Hashtable<Integer, Integer> barGraphDataDict = new Hashtable<>();
    private Hashtable<String, Integer> pieChartDataDict = new Hashtable<>();

    private NetworkConnection networkConnection;
    private String startDate = null;
    private String endDate = null;
    private String userId;


    public ReportFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the View for this fragment
        View view = inflater.inflate(R.layout.report_fragment, container, false);

        networkConnection = new NetworkConnection();
        getUserIdFromSharedPref();
        configureUI(view);
        configureDatePicker();
        configureButton();
        configureSpinner();

        return view;
    }


    private void configureUI(View view) {
        getActivity().setTitle("Report");
        startDateTIL = view.findViewById(R.id.til_report_start_date);
        endDateTIL = view.findViewById(R.id.til_report_end_date);

        startDateEditText = view.findViewById(R.id.et_report_start_date);
        startDateEditText.addTextChangedListener(new startDateEditTextWatcher());
        endDateEditText = view.findViewById(R.id.et_report_end_date);
        endDateEditText.addTextChangedListener(new endDateEditTextWatcher());

        pieChart = view.findViewById(R.id.piechart);
        barGraph = view.findViewById(R.id.barchart);
        spinner = view.findViewById(R.id.report_spinner);
        showBtn = view.findViewById(R.id.showBtn_report);
    }

//A map entry (key-value pair).
// The Map.entrySet method returns a collection-view of the map,
// whose elements are of this class. The only way to obtain a reference to a map entry is from the iterator of this collection-view.
// These Map.Entry objects are valid only for the duration of the iteration
    private void setPieChartData() {
        if (pieChartDataDict.isEmpty()) {
            pieChart.clear();
        } else {
            ArrayList<PieEntry> values = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : pieChartDataDict.entrySet()) {
                String postcode = entry.getKey();
                int number = entry.getValue();
                values.add(new PieEntry(number, postcode));
            }
            PieDataSet pieDataSet = new PieDataSet(values, "Cinema Postcode");
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pieDataSet.setValueTextColor(Color.BLACK);
            pieDataSet.setValueTextSize(14f);


            PieData pieData = new PieData(pieDataSet);
            // show in percentage %
            pieData.setValueFormatter(new PercentFormatter(pieChart));

            pieChart.setData(pieData);
            // no description label
            pieChart.getDescription().setEnabled(false);

            pieChart.setUsePercentValues(true);
            pieChart.setEntryLabelTextSize(14f);
            // no hole
            pieChart.setHoleRadius(0);
            pieChart.setTransparentCircleRadius(0);

            pieChart.animate();
            pieChart.invalidate();
        }
    }


    private void configureBarGraphAxis() {
        String[] months = {"", "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};
        XAxis xAxis = barGraph.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(months.length);
        xAxis.setTextSize(11.5f);

        YAxis leftAxis = barGraph.getAxisLeft();
        //display value as int
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        //start with zero
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextSize(14f);

        // remove the whole right axis
        YAxis rightAxis = barGraph.getAxisRight();
        rightAxis.setEnabled(false);
    }


    private void setBarGraphData() {
        if (barGraphDataDict.isEmpty()) {
            barGraph.clear();
        } else {
            configureBarGraphAxis();

            ArrayList<BarEntry> values = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : barGraphDataDict.entrySet()) {
                int month = entry.getKey();
                int number = entry.getValue();
                values.add(new BarEntry(month, number));
            }
            BarDataSet barDataSet = new BarDataSet(values, "Numbers of Movie");
            barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            barDataSet.setValueTextSize(16f);
            // value show as integer
            barDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.valueOf((int) Math.floor(value));
                }
            });

            BarData barData = new BarData(barDataSet);
            barData.setBarWidth(0.8f);

            barGraph.setData(barData);
            barGraph.setFitBars(true);
            barGraph.getDescription().setEnabled(false);

            barGraph.animateY(2000);
            barGraph.invalidate();
        }
    }


    private void configureButton() {
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDate != null && endDate != null) {

                    String[] details = {userId, startDate, endDate};

                    new GetNumbersOfMoviesPerCinemaPostcodeTask().execute(details);
                }
                if (startDate == null) {
                    startDateTIL.setError("* Field can't be empty.");
                }
                if (endDate == null) {
                    endDateTIL.setError("* Field can't be empty.");
                }
            }
        });
    }


    private void configureSpinner() {
        String[] years = new String[]{"Select a Year", "2015", "2016", "2017", "2018", "2019", "2020"};

        final List<String> yearList = new ArrayList<String>(Arrays.asList(years));

        spinnerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, yearList) {
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
                    String year = parent.getItemAtPosition(position).toString();

                    new GetNumbersOfMoviesPerMonthTask().execute(new String[]{userId, year});
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void configureDatePicker() {
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // get current date and set popup date picker
        Date today = calendar.getTime();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                        startDate = year + "-" + monthStr + "-" + dayStr;
                        startDateEditText.setText(startDate);
                    }
                }, year, month, day);
                // start date should before end date
                if (endDate != null) {
                    try {
                        datePickerDialog.getDatePicker().setMaxDate(dateFormat.parse(endDate).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                datePickerDialog.show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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
                        endDate = year + "-" + monthStr + "-" + dayStr;
                        endDateEditText.setText(endDate);
                    }
                }, year, month, day);
                // end date should after start date
                if (startDate != null) {
                    try {
                        datePickerDialog.getDatePicker().setMinDate(dateFormat.parse(startDate).getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                datePickerDialog.show();
            }
        });
    }


    private class GetNumbersOfMoviesPerCinemaPostcodeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... details) {
            return networkConnection.getNumberOfMoviesPerCinemaPostcode(details[0], details[1], details[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            pieChartDataDict.clear();
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String postcode = jsonObject.getString("cinemaPostcode");
                        int number = jsonObject.getInt("numberOfMovies");
                        pieChartDataDict.put(postcode, number);
                    }
                    setPieChartData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }


    private class GetNumbersOfMoviesPerMonthTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... details) {
            return networkConnection.getNumberOfMoviesPerMonthAYear(details[0], details[1]);
        }


        @Override
        protected void onPostExecute(String result) {
            barGraphDataDict.clear();
            if (result != null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Calendar calendar = Calendar.getInstance();

                        String monthName = jsonObject.getString("Month");
                        Date date = new SimpleDateFormat("MMMM").parse(monthName);
                        calendar.setTime(date);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int number = jsonObject.getInt("numberOfMovies");
                        barGraphDataDict.put(month, number);
                    }
                    setBarGraphData();
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            } else {

            }
        }
    }


    private void getUserIdFromSharedPref() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("User", Context.MODE_PRIVATE);
        userId = sharedPref.getString("userId", null);
    }


    private class startDateEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            startDateTIL.setErrorEnabled(false);
            endDateTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    private class endDateEditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            endDateTIL.setErrorEnabled(false);
            startDateTIL.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

}
