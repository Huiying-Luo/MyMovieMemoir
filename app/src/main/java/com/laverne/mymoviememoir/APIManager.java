package com.laverne.mymoviememoir;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class APIManager {

    public static class SearchGoogleAPI {
        private static final String API_KEY = "AIzaSyCN_LhPS3br3M2JgNp7ML0y1v54ijCcmsY";
        private static final String SEARCH_ID_cx = "015211114172613121852:gtaqkcd4etg";

        public static String search(String keyword, String[] parmas, String[] values) {
            keyword = keyword.replace(" ", "+");
            URL url = null;
            HttpURLConnection connection = null;
            String textResult = "";
            String query_parameter = "";

            if (parmas != null && values != null) {
                for (int i = 0; i < parmas.length; i++) {
                    query_parameter += "&";
                    query_parameter += parmas[i];
                    query_parameter += "=";
                    query_parameter += values[i];
                }
            }

            try {
                url = new URL("https://www.googleapis.com/customsearch/v1?key=" + API_KEY + "&cx=" +
                        SEARCH_ID_cx + "&q=" + keyword + query_parameter);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    textResult += scanner.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return textResult;
        }
    }


    public static class OMDbAPI {
        private static final String API_KEY = "d5a747ad";

        public static String search(String keyword, String[] parmas, String[] values) {
            keyword = keyword.replace(" ", "+");
            URL url = null;
            HttpURLConnection connection = null;
            String textResult = "";
            String query_parameter = "";

            if (parmas != null && values != null) {
                for (int i = 0; i < parmas.length; i++) {
                    query_parameter += "&";
                    query_parameter += parmas[i];
                    query_parameter += "=";
                    query_parameter += values[i];
                }
            }

            try {
                url = new URL("http://www.omdbapi.com/?apikey=" + API_KEY +"&s=" + keyword + query_parameter);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    textResult += scanner.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return textResult;
        }


        public static String searchByName(String name, String[] parmas, String[] values) {
            URL url = null;
            HttpURLConnection connection = null;
            String textResult = "";
            String query_parameter = "";

            if (parmas != null && values != null) {
                for (int i = 0; i < parmas.length; i++) {
                    query_parameter += "&";
                    query_parameter += parmas[i];
                    query_parameter += "=";
                    query_parameter += values[i];
                }
            }
            try {
                url = new URL("http://www.omdbapi.com/?apikey=" + API_KEY +"&t=" + name + query_parameter);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    textResult += scanner.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return textResult;
        }
    }
}
