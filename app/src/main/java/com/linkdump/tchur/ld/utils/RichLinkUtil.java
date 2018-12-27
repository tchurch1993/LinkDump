package com.linkdump.tchur.ld.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by tchurh on 12/21/2018.
 * Bow down to my greatness.
 */
public class RichLinkUtil {
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private static final String TAG = RichLinkUtil.class.getSimpleName();

    public RichLinkUtil() {
    }


    public boolean containsUrl(String message) {

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(message);//replace with string to compare
        return m.find();
    }

    public static void test(Context context, String url, final VolleyCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final Map<String, String> data = new HashMap<>();

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    //Log.d(TAG, response);
                    if (response.contains("</head>")) {
                        int indexOfClosingHead = response.indexOf("</head>");
                        String headTag = response.substring(0, indexOfClosingHead);
                        Log.d(TAG, headTag);
                        callback.onSuccess(headTag);
                    } else {
                        callback.onFailure("none");
                    }
                }, error -> {
            error.printStackTrace();
            Log.e(TAG, "failed", error);
            callback.onFailure("error");
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static Map<String, String> getOGTags(String headTag) {
        Map<String, String> ogMap = new HashMap<>();
        if (headTag.contains("og:")) {
            int index = headTag.indexOf("og:");
            String headCopy = headTag;
            while (index != -1) {
                headCopy = headCopy.substring(index + 3);
                int spaceIndex = headCopy.indexOf(" ");
                String property = headCopy.substring(0, spaceIndex);
                headCopy = headCopy.substring(spaceIndex + 9);
                int closingIndex = headCopy.indexOf("/>");
                String content = headCopy.substring(0, closingIndex);
                Log.d(TAG, "OG tag: " + property + " found");
                if (property.equals("description") || property.equals("image") || property.equals("title"))
                    ogMap.put(property, content);
                index = headCopy.indexOf("og:");
            }
            Log.d(TAG, ogMap.toString());
        }
        return ogMap;
    }

    public static void getRichLinkProperties(Context context, String url, OGTagCallback callback) {
        String guessedURL = URLUtil.guessUrl(url);
//        guessedURL = guessedURL.replace("http", "https");
        Log.d(TAG, guessedURL);


        test(context, guessedURL, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                callback.onSuccess(getOGTags(result));
            }

            @Override
            public void onFailure(String error) {
                if (error.equals("none")) {
                    Log.d(TAG, "no header tag was found");
                } else {
                    test(context, guessedURL.replace("http", "https"), new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            callback.onSuccess(getOGTags(result));
                        }

                        @Override
                        public void onFailure(String error) {
                            if (error.equals("none")) {
                                Log.d(TAG, "no header tag was found");
                            } else {
                                Log.d(TAG, "request Failed");
                            }
                        }
                    });

                }
            }
        });
    }

    public interface OGTagCallback {
        void onSuccess(Map<String, String> data);
    }

    public interface VolleyCallback {
        void onSuccess(String result);

        void onFailure(String error);
    }

    public static String getFirstUrlFromString(String input) {
        Log.d(TAG, "inside getFirstUrlFromString");
        List<String> links = new ArrayList();
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(input);
        while (m.find()) {
            Log.d(TAG, "found url");
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            }
            links.add(urlStr);

        }
        if (links.isEmpty()) {
            Log.d(TAG, "no links found");
            return null;
        }
        return links.get(0);
    }
}
