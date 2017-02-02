package ca.umontreal.iro.hurtubin.listview_asynctask;

import android.util.Pair;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class WikipediaAPI {

    protected static String search_url = ".wikipedia.org/w/api.php?action=query&format=json&pageids=";
    protected static String links_url = ".wikipedia.org/w/api.php?action=query&format=json&generator=links&prop=&pageids=";

    protected static String img_url = ".wikipedia.org/w/api.php?action=query&list=allimages&ailimit=1&aiprop=&format=json&aifrom=";
    protected static String rnd_url = ".wikipedia.org/w/api.php?action=query&format=json&list=random&rnlimit=10&rnnamespace=0";

    protected static OkHttpClient http = null;

    protected String lang = "en";

    public WikipediaAPI() {
    }

    public WikipediaAPI(String lang) {
        this.lang = lang;
    }

    public JSONObject getJSON(String url) throws IOException, JSONException {

        if (http == null)
            http = new OkHttpClient();

        Request request = new Request.Builder().url("https://" + this.lang + url).build();

        Response response = http.newCall(request).execute();

        String json = response.body().string();

        return new JSONObject(json);
    }

    public WikipediaArticle random() throws IOException, JSONException {

        // Parse le JSON
        JSONObject object = getJSON(WikipediaAPI.rnd_url);
        JSONObject article = object.getJSONObject("query").getJSONArray("random").getJSONObject(0);

        return new WikipediaArticle(
                article.getString("title"),
                article.getString("id")
        );
    }

    public Pair<WikipediaArticle, ArrayList<WikipediaArticle>> find(String id) throws IOException, JSONException {

        // Fetch l'article
        JSONObject object = getJSON(WikipediaAPI.search_url + id);
        JSONObject article = object.getJSONObject("query").getJSONObject("pages").getJSONObject(id);

        // Fetch les liens
        ArrayList<WikipediaArticle> links = new ArrayList<>();

        object = getJSON(WikipediaAPI.links_url + id);

        JSONObject cont = null;

        do {
            JSONObject json_links = object.getJSONObject("query").getJSONObject("pages");

            for (int i = 0; i < json_links.length(); i++) {
                String name = json_links.names().getString(i);

                JSONObject a = json_links.getJSONObject(name);

                if (a.getInt("ns") != 0 || !a.has("pageid"))
                    continue;

                links.add(new WikipediaArticle(
                        a.getString("title"),
                        a.getString("pageid")
                ));
            }

            // Continue (liens obtenus en plusieurs requêtes)
            cont = object.optJSONObject("continue");

            if (cont != null) {
                object = getJSON(WikipediaAPI.links_url + id + "&gplcontinue=" + cont.getString("gplcontinue"));
            }

        } while (cont != null);

        return new Pair<>(
                new WikipediaArticle(
                        article.getString("title"),
                        article.getString("pageid")),
                links
        );
    }
}