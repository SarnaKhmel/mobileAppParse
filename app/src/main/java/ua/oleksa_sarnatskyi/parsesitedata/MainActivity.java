package ua.oleksa_sarnatskyi.parsesitedata;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ParseAdapter adapter;
    private ArrayList<ParseItem> parseItems = new ArrayList<>();
    private ProgressBar progressBar;

    Button buttonAnalyse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ParseAdapter(parseItems, this);
        recyclerView.setAdapter(adapter);

        Content content = new Content();
        content.execute();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAnalyse = (Button) findViewById(R.id.buttonAnalyse);
        buttonAnalyse.setOnClickListener((View.OnClickListener) this);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_item, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        // Get the search view and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); //Do not iconfy the widget; expand it by default

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();
                ArrayList<ParseItem> newList = new ArrayList<>();
                for (ParseItem parseItem : parseItems) {
                    String title = parseItem.getTitle().toLowerCase();

                    // you can specify as many conditions as you like
                    if (title.contains(newText)) {
                        newList.add(parseItem);
                    }
                }
                // create method in adapter
                adapter.setFilter(newList);

                return true;
            }
        };

        searchView.setOnQueryTextListener(queryTextListener);

        return true;

    }

    private class Content extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_in));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            progressBar.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out));
            adapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String url = "https://afisha.vash.ua/index/kultura/";

                Document doc = Jsoup.connect(url).get();

                Elements data = doc.select("div.new-overlay");
                int size = data.size();
                Log.d("doc", "doc: "+doc);
                Log.d("data", "data: "+data);
                Log.d("size", ""+size);
                for (int i = 0; i < size - 1; i++) {
                    String addUrl = "https://afisha.vash.ua";
                    String imgUrl = data.select("div.new-img")
                            .select("img")
                            .eq(i)
                            .attr("src");
                    Log.d("Лог йобаної картинки",imgUrl);
                    String imgFuckU = addUrl + imgUrl;
                    Log.d(" Лог картинки разом", imgFuckU);
                    String title = data.select("div.new-img")
                            .select("img")
                            .eq(i)
                            .attr("title");

//                    String detailUrl = data.select("div.new-img")
//                            .select("img")
//                            .eq(i)
//                            .attr("title");

                    String detailUrl = data.select("div.new-desc")
                            .select("a")
                            .eq(i)
                            .attr("href");
                    int price = 10;

                    parseItems.add(new ParseItem(imgFuckU, title, detailUrl));
                    Log.d("items", "img: " + "https://afisha.vash.ua"+imgUrl + " . title: " + title + "   details    " + detailUrl);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }




    }


    public void onCreate(@org.jetbrains.annotations.NotNull View v) {
        switch (v.getId()) {
            case R.id.buttonAnalyse:
                // TODO Call second activity
                break;
            default:
                break;
        }
    }

}
