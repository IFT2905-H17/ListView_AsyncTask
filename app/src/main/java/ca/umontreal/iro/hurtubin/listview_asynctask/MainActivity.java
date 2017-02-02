package ca.umontreal.iro.hurtubin.listview_asynctask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String[] titles = new String[5];

    ListView liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FetchArticle fetcher = new FetchArticle();

        fetcher.execute();

        liste = (ListView) findViewById(R.id.list);
    }

    public class SimpleListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 25;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
                Toast.makeText(MainActivity.this, "Inflate" + position, Toast.LENGTH_SHORT).show();
            }

            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);

            tv.setText(titles[position % 5]);

            return convertView;
        }
    }

    public class FetchArticle extends AsyncTask<String, Object, WikipediaArticle[]> {

        @Override
        protected WikipediaArticle[] doInBackground(String... params) {

            // Fetcher un article au hasard

            WikipediaAPI api = new WikipediaAPI();

            WikipediaArticle[] articles = new WikipediaArticle[5];

            try {
                for(int i=0; i<5; i++)
                    articles[i] = api.random();

            } catch (IOException|JSONException e) {
                e.printStackTrace();
            }

            return articles;
        }

        @Override
        protected void onPostExecute(WikipediaArticle[] articles) {

            for(int i=0; i<5; i++)
                titles[i] = articles[i].title;

            liste.setAdapter(new SimpleListAdapter());
        }
    }
}
