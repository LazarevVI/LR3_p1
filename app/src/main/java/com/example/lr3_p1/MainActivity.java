package com.example.lr3_p1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private String site = "https://bodysize.org/ru/top100/";
    private Integer numberOfImage = 0;



    private  static class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String...strings) {
            URL url =  null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection!=null)
                    urlConnection.disconnect();
            }
            return null;
        }
    }

    private Object[] getWeb() throws IOException {

        org.jsoup.nodes.Document doc;

        org.jsoup.nodes.Document doc_parsed;

        Elements photos;
        Elements divnames;
        String url;
        doc = Jsoup.connect(site).get();
        doc_parsed = Jsoup.parse(doc.html());

        photos = doc_parsed.body().getElementsByClass("photo");
        for (Element photo: photos){
            url = photo.attr("style");
            url = url.substring(url.indexOf("(") + 1, url.indexOf(')'));
            urls.add(url);
        }

        divnames = doc_parsed.body().getElementsByClass("name");
        for (Element name: divnames){
            names.add(name.text());
        }

        return new Object[]{urls, names};
    }


    public void OnClickGuess (View v) {

        ImageView image_guess = findViewById(R.id.image_id);
        Button btn_guess = findViewById(R.id.button_id);
        EditText enter_name = findViewById(R.id.enter_name_id);

        String name1 = String.valueOf(names.get(numberOfImage));
        String name2 = String.valueOf(enter_name.getText());
        if (enter_name.length() != 0)
        {
            Toast toast;
            if (name1.replaceAll("\\s","").toLowerCase(Locale.ROOT)
                    .equals(name2.replaceAll("\\s", "").toLowerCase(Locale.ROOT)))

            {
                toast = Toast.makeText(this, "Верно!",
                        Toast.LENGTH_SHORT);
            }
            else {
                toast = Toast.makeText(this, "Неверно!",
                        Toast.LENGTH_SHORT);
            }
            toast.show();

            enter_name.setText("");
            numberOfImage = (int) (Math.random() * urls.size());

            //отображение следующей
            String imageUri = urls.get(numberOfImage);
            Picasso.get().load(imageUri).into(image_guess);
        }

        else {
            Toast toast = Toast.makeText(this, "Введите имя и фамилию!",
                    Toast.LENGTH_SHORT);
            toast.show();
        }

//        if (enter_name.length() != 0) {
//            enter_name.setText("");
//            numberOfImage = (int) (Math.random() * urls.size());
//
//            //отображение следующей
//            String imageUri = urls.get(numberOfImage);
//            Picasso.get().load(imageUri).into(image_guess);
//        }

    }


    public class Runner implements Runnable {
        Object[] obj;
        @Override
        public void run() {
            try {
                obj = getWeb();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public Object[] getobj() {
            return obj;
        }
    }

    private void init() throws InterruptedException {
        Runner r = new Runner();
        Thread thread_2 = new Thread(r);
        thread_2.start();
        thread_2.join();
        Object[] obj = r.getobj();
        urls = (ArrayList<String>) obj[0];
        names = (ArrayList<String>) obj[1];
        Log.d("urlslen", String.valueOf(urls.size()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView image_guess = findViewById(R.id.image_id);

        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // первое отображение при входе в приложение
        numberOfImage = (int) (Math.random()*urls.size());
        String imageUri = urls.get(numberOfImage);
        Picasso.get().load(imageUri).into(image_guess);

     }

}