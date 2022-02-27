package com.suyal.newsgatik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements CategoryRVAdapter.CategoryClickInterface{

    Button logoutBtn;
    private RecyclerView newsRV, categoryRV;
    private ProgressBar loadingPB;
    private ArrayList<Articles> articlesArrayList;
    private ArrayList<CategoryRVModal> categoryRVModalArrayList;
    private CategoryRVAdapter categoryRVAdapter;
    private  NewsRVAdapter newsRVAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        newsRV = findViewById(R.id.idRVNews);
        categoryRV = findViewById(R.id.idRCategoties);
        loadingPB = findViewById(R.id.idPBLoading);
        articlesArrayList = new ArrayList<>();
        categoryRVModalArrayList = new ArrayList<>();
        newsRVAdapter = new NewsRVAdapter(articlesArrayList,this);
        categoryRVAdapter = new CategoryRVAdapter(categoryRVModalArrayList,this,this::onCategoryClick);
        newsRV.setLayoutManager(new LinearLayoutManager(this));
        newsRV.setAdapter(newsRVAdapter);
        categoryRV.setAdapter(categoryRVAdapter);


//        logoutBtn = findViewById(R.id.logout);
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(),SignInActivity.class));
//                finish();
//            }
//        });
        getCategories();
        getNews("All");
        newsRVAdapter.notifyDataSetChanged();
    }

    private void getCategories(){
        categoryRVModalArrayList.add(new CategoryRVModal("All",""));
        categoryRVModalArrayList.add(new CategoryRVModal("Technology","https://images.unsplash.com/photo-1488590528505-98d2b5aba04b?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8dGVjaG5vbG9neXxlbnwwfHwwfHw%3D&auto=format&fit=crop&w=1000&q=60"));
        categoryRVModalArrayList.add(new CategoryRVModal("Science","https://media.istockphoto.com/photos/vaccine-in-laboratory-flu-shot-and-covid19-vaccination-picture-id1289345741?b=1&k=20&m=1289345741&s=170667a&w=0&h=oG8iaDNP4rOLSgXWfeSziU3Vyu6KJS9Hn2ORohzSsRg="));
        categoryRVModalArrayList.add(new CategoryRVModal("Sports","https://media.istockphoto.com/photos/beautiful-young-black-boy-training-on-the-football-pitch-picture-id1295248329?b=1&k=20&m=1295248329&s=170667a&w=0&h=jfcc98lBsoGb2FpCtsfh61egArW4_oFM1ZE9mYTMUFI="));
        categoryRVModalArrayList.add(new CategoryRVModal("General","https://media.istockphoto.com/photos/young-woman-at-the-dentist-picture-id1301303290?b=1&k=20&m=1301303290&s=170667a&w=0&h=yroDhdhPMNTGMkuOPGQexBAzlZlU7agO0ciAI7USUao="));
        categoryRVModalArrayList.add(new CategoryRVModal("Business","https://images.unsplash.com/photo-1600880292203-757bb62b4baf?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8YnVzaW5lc3N8ZW58MHx8MHx8&auto=format&fit=crop&w=1000&q=60"));
        categoryRVModalArrayList.add(new CategoryRVModal("Entertainment","https://media.istockphoto.com/photos/parents-and-their-two-children-watching-tv-together-at-home-picture-id1320021417?b=1&k=20&m=1320021417&s=170667a&w=0&h=3ls77y0IxMOIpK1GZFXl6bp2COzIEIFRixKJR4PYbOo="));
        categoryRVModalArrayList.add(new CategoryRVModal("Health","https://images.unsplash.com/photo-1498837167922-ddd27525d352?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxzZWFyY2h8M3x8aGVhbHRofGVufDB8fDB8fA%3D%3D&auto=format&fit=crop&w=1000&q=60"));
        categoryRVAdapter.notifyDataSetChanged();
    }

    private void getNews(String category){
        loadingPB.setVisibility(View.VISIBLE);
        articlesArrayList.clear();
        String categoryURL = "https://newsapi.org/v2/top-headlines?country=in&category="+category+"&apikey=bb46865230944d228d3c176e4855e767";
        String url = "https://newsapi.org/v2/top-headlines?country=in&excludeDomains=stackoverflow.com&sortBy=publishedAt&language=en&apikey=bb46865230944d228d3c176e4855e767";
        String BASE_URL = "https://newsapi.org/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi retrofitApi=retrofit.create(RetrofitApi.class);
        Call<NewsModal> call;
        if(category.equals("All")){
            call = retrofitApi.getAllNews(url);
        }else{
            call=retrofitApi.getNewsByCategory(categoryURL);
        }

        call.enqueue(new Callback<NewsModal>() {
            @Override
            public void onResponse(Call<NewsModal> call, Response<NewsModal> response) {
                NewsModal newsModal = response.body();
                loadingPB.setVisibility(View.GONE);
                ArrayList<Articles> articles= newsModal.getArticles();
                for(int i=0;i<articles.size();i++){
                    articlesArrayList.add(new Articles(articles.get(i).getTitle(),articles.get(i).getDescription(),articles.get(i).getUrlToImage(),
                            articles.get(i).getUrl(),articles.get(i).getContent()));
                }
                newsRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<NewsModal> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fail to get news", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onCategoryClick(int position) {
        String category = categoryRVModalArrayList.get(position).getCategory();
        getNews(category);

    }
}