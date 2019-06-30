package com.linkdump.tchur.ld.utils.website_parsing;

import android.util.Log;
import android.webkit.URLUtil;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.linkdump.tchur.ld.activities.ChatActivity;
import com.linkdump.tchur.ld.constants.FirebaseConstants;
import com.linkdump.tchur.ld.data.ChatActivityContainer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;

public class DomParsing {

    private String messageId = null;



    public Document getWebsiteDocument(String url, String guessedUrl) {

        Document document;
        try {

            document = Jsoup.connect(url).get();
        }
        catch (Exception ex)
        {
            url = url.replace("http", "https");
            Log.d(TAG, "second URL after http replaced with https is: " + guessedUrl);
            try
            {
                document = Jsoup.connect(url).get();
            }
            catch (IOException error)
            {
                error.printStackTrace();
            }
            ex.printStackTrace();
        }
    }





    public Element getElementHead(Document document) {

        if (document != null) {


            Log.d(TAG, document.title());
            boolean hasSchemaThing = false;

            if (document.attr("itemprop") != null) {
                hasSchemaThing = true;
            }

            //this separated the head element from the response string

            return document.head();
        }
        return null;
    }






    public Elements getElementsFromHead(Element head) {
        Elements metaElements = head.getElementsByAttribute("property");

        return metaElements;
    }








    public void updateUiFromElements(Elements elements){

        Map<String, String> ogTags = new HashMap<>();

        Element[] elementArray = new Element[elements.size()];
        for(int i = 0; i < elements.size(); i++)
        {
             elementArray[i] = elements.get(i);
        }


        Observable.fromArray(elementArray)
                  .subscribeOn(Schedulers.newThread())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Observer<Element>()
                  {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(Element e) {
                        if (e.attributes()
                             .get("property")
                             .matches(ChatActivityContainer.getOgRegex()) || e.attributes().get("Property").matches(ChatActivityContainer.getOgRegex()))
                        {
                            ogTags.put(e.attr("property"), e.attr("content"));
                        }
                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onComplete() {

                    }
                });




        // grabs the meta data that matches the REGEX expression
        for (Element e : elements) {
            if (e.attributes()
                    .get("property")
                    .matches(ChatActivityContainer.getOgRegex()) || e.attributes().get("Property").matches(ChatActivityContainer.getOgRegex())) {
                ogTags.put(e.attr("property"), e.attr("content"));

            }
        }



}



    @Override
    protected void onPostExecute(Map<String, String> s) {


        // Takes the map of iamge, title, and description and pushes it into the DB into a message format

        if (s != null) {
            Log.d(TAG, "Map in onPostExecute: " + s.toString());
            firebaseDbContext.groupRef.collection(FirebaseConstants.MESSAGES)
                    .document(messageId)
                    .get()
                    .addOnCompleteListener(task ->
                    {
                        if ((task.getResult() != null) && task.isSuccessful()) {


                            DocumentSnapshot message = task.getResult();
                            Map<String, Object> data = message.getData();

                            if (!s.isEmpty()) {
                                data.put("messageType", "LINK");
                                if (s.get("og:image") != null) {
                                    data.put("linkImage", s.get("og:image"));
                                }
                                if (s.get("og:title") != null) {
                                    data.put("linkTitle", s.get("og:title"));
                                }
                                if (s.get("og:description") != null) {
                                    data.put("linkDescription", s.get("og:description"));
                                }
                                if (s.get("og:url") != null) {
                                    data.put("linkUrl", s.get("og:url"));
                                }
                                if (s.get("og:video") != null) {
                                    data.put("linkVideo", s.get("og:video"));
                                }
                            } else {
                                if (data.get("imageLink") == null) {
                                    data.put("messageType", "TEXT");
                                }
                            }

                            DocumentReference messageRef = firebaseDbContext.groupRef
                                    .collection(FirebaseConstants.MESSAGES)
                                    .document(messageId);

                            messageRef.set(data, SetOptions.merge());
                            Log.d(TAG, "in post execute DB call");
                        }
                    });
        }
        super.onPostExecute(s);
    }
}
