package com.laverne.mymoviememoir.NetworkConnection;

import android.util.Log;

import com.google.gson.Gson;
import com.laverne.mymoviememoir.Entity.Cinema;
import com.laverne.mymoviememoir.Entity.Credentials;
import com.laverne.mymoviememoir.Entity.Memoir;
import com.laverne.mymoviememoir.Entity.User;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkConnection {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "http://192.168.0.70:8080/FIT5046-MovieMemoir/webresources/";

    private OkHttpClient client = null;
    private String result;
    private int resultCode;


    public NetworkConnection() {
        client = new OkHttpClient();
    }


    public int addCredentials(String[] details) {
        try {
            Credentials credentials = new Credentials(Integer.parseInt(details[0]), details[1], details[2], details[3]);

            Gson gson = new Gson();
            String credJson = gson.toJson(credentials);
            Log.i("credjson", credJson);

            final String methodPath = "moviememoir.credentials/";

            RequestBody body = RequestBody.create(credJson, JSON);
            Request request = new Request.Builder().url(BASE_URL + methodPath).post(body).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                resultCode = 0;
            } else {
                resultCode = response.code();
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return resultCode;
    }


    public int addUser(String[] details) {
        try {
            User user = new User(Integer.parseInt(details[0]), details[1], details[2], details[3], details[4], details[5], details[6], details[7]);
            user.setCredId(Integer.parseInt(details[0]));

            Gson gson = new Gson();
            String userJson = gson.toJson(user);
            Log.i("userjson", userJson);

            final String methodPath = "moviememoir.userprofile/";

            RequestBody body = RequestBody.create(userJson, JSON);
            Request request = new Request.Builder().url(BASE_URL + methodPath).post(body).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                resultCode = 0;
            } else {
                resultCode = response.code();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultCode;
    }


    public int addCinema(String[] details) {
        try {
            Cinema cinema = new Cinema(Integer.parseInt(details[0]), details[1], details[2]);

            Gson gson = new Gson();
            String cineJson = gson.toJson(cinema);
            Log.i("cinejson", cineJson);

            final String methodPath = "moviememoir.cinema/";

            RequestBody body = RequestBody.create(cineJson, JSON);
            Request request = new Request.Builder().url(BASE_URL + methodPath).post(body).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                resultCode = 0;
            } else {
                resultCode = response.code();
            }
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return resultCode;
    }


    public int addMemoir(String[] details) {
        try {
            Memoir memoir = new Memoir(Integer.parseInt(details[0]), details[1], details[2], details[3], Double.parseDouble(details[4]), details[5]);
            memoir.setCineId(Integer.parseInt(details[6]));
            memoir.setUserId(Integer.parseInt(details[7]));

            Gson gson = new Gson();
            String memoirJson = gson.toJson(memoir);
            Log.i("json", memoirJson);

            final String methodPath = "moviememoir.memoir/";

            RequestBody body = RequestBody.create(memoirJson, JSON);
            Request request = new Request.Builder().url(BASE_URL + methodPath).post(body).build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                resultCode = 0;
            } else {
                resultCode = response.code();
                Log.i("code", String.valueOf(resultCode));
            }
        } catch (IOException e) {
            Log.i("message", e.getMessage());
            e.printStackTrace();
        }
        return resultCode;
    }


    public String findCredentialByUsername(String username) {

        final String methodPath = "moviememoir.credentials/findByCredUsername/" + username;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getUserByCredId(int credId) {
        final String methodPath = "moviememoir.userprofile/findByCredId/" + credId;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getUserById(int userId) {
        final String methodPath = "moviememoir.userprofile/" + userId;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getTopFiveMovies(int userId) {
        final String methodPath = "moviememoir.memoir/findTopFiveMoviesByUserId/" + userId;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            Log.i("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getAll(String entityName) {
        final String methodPath = "moviememoir." + entityName +"/";

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getMemoirsByUserId(int userId) {
        final String methodPath = "moviememoir.memoir/findByUserId/" + userId;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
            Log.i("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getCinemaByName(String cineName) {
        final String methodPath = "moviememoir.cinema/findByCineName/" + cineName;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String findCinemaByCineName(String cineName) {

        final String methodPath = "moviememoir.cinema/findByCineName/" + cineName;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String findMaxId(String entityName) {
        final String methodPath = "moviememoir." + entityName + "/findMaxId/";

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getNumberOfMoviesPerCinemaPostcode(String userId, String startDate, String endDate) {
        final String methodPath = "moviememoir.memoir/getNumberOfMoviesPerCinemaPostcode/" + userId + "/" + startDate + "/" + endDate;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public String getNumberOfMoviesPerMonthAYear(String userId, String year) {
        final String methodPath = "moviememoir.memoir/getNumberOfMoviesPerMonth/" + userId + "/" + year;

        Request.Builder builder = new Request.Builder();
        builder.url(BASE_URL + methodPath);
        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();
            result = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
