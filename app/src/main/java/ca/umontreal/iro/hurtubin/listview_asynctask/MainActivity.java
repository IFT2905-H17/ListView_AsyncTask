package ca.umontreal.iro.hurtubin.listview_asynctask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FetchArticle fetcher = new FetchArticle();

        fetcher.execute("foo", "bar", "twado");
    }

    public class FetchArticle extends AsyncTask<String, Object, WikipediaArticle> {

        @Override
        protected WikipediaArticle doInBackground(String... params) {

            Log.i("TEST", params[0]);

            // Fetcher un article au hasard

            WikipediaAPI api = new WikipediaAPI();

            WikipediaArticle article = null;

            try {
                 article = api.random();

            } catch (IOException|JSONException e) {
                e.printStackTrace();
            }

            return article;
        }

        @Override
        protected void onPostExecute(WikipediaArticle article) {
            Toast.makeText(getApplicationContext(), article.title, Toast.LENGTH_LONG).show();
        }
    }
}
