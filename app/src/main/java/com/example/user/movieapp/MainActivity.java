package com.example.user.movieapp;

import android.graphics.Movie;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static String BASE_URL = "https://api.themoviedb.org/";
    public static int PAGE = 1;
    public static String API_KEY = "5e7322ae45455e34f09e2adaa3c029d7";
    public static String LANGUAGE = "en-US";
    private ApiInterface apiInterface;
    private List<MovieResults.ResultsBean> listOfMovies;
//    private TextView myTextView;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    Retrofit retrofit;
    String currentCategory;

    int timesScrolled = 0;
    public boolean hasReachedBottomOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Now playing");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        myTextView = (TextView)findViewById(R.id.myTextView);


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        currentCategory = "now_playing";
        getMoviesInCategory(currentCategory);


        recyclerView = findViewById(R.id.recyclerView);
        hasReachedBottomOnce = false;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //Log.i("OKAY", "Scrolled");
                if (!recyclerView.canScrollVertically(1) && !hasReachedBottomOnce) {
                    Toast.makeText(getBaseContext(), "at the end of the view", Toast.LENGTH_LONG).show();

                    timesScrolled++;
                    Log.i("OKAY", timesScrolled + " Scrolled");

                    addMoreMovies();
                    hasReachedBottomOnce = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hasReachedBottomOnce = false;//через несколько секунд hasReachedBottom опять становится false, делаем это в другом треде, чтобы не мешать ui.
                        }
                    }, 2500);
                }
            }
        });
    }





    private void getMoviesInCategory(final String category){
            if (apiInterface == null) apiInterface = retrofit.create(ApiInterface.class);

            Call<MovieResults> call = apiInterface.listOfMovies(category, API_KEY, LANGUAGE, PAGE, "RU");

            call.enqueue(new Callback<MovieResults>() {
                @Override
                public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {//это будет срабатывать только один раз
                    MovieResults results = response.body();//в этом объекте будет результат нашего вызыва, то есть вернется то, что мы хотели в коллбэке.
                    listOfMovies = results.getResults();
                    //Здесь нужно будет заполнять все, потому что это будет вызываться, если все прошло удачно
//                myTextView.setText(listOfMovies.get(0).getTitle());
                    recyclerView = findViewById(R.id.recyclerView);
//                recyclerView.setHasFixedSize(true);
                    adapter = new CustomAdapter(listOfMovies);//тут мы поставляем список фильмов в наш кастомный адаптер
                    layoutManager = new LinearLayoutManager(getBaseContext());

                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);




                }

                @Override
                public void onFailure(Call<MovieResults> call, Throwable t) {
                    t.printStackTrace();
                }
            });


    }


    private void addMoreMovies(){
        Log.i("OKAY", "here");

        Call<MovieResults> call;
        call = apiInterface.listOfMovies(currentCategory, API_KEY, LANGUAGE, PAGE + timesScrolled, "RU");
        call.enqueue(new Callback<MovieResults>() {//наверное это все нужно переделать в метод, если это вообще возможно
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                MovieResults movieResults = response.body();
                if (PAGE + timesScrolled > movieResults.getTotal_pages()) {
                    Log.i("OKAY", "no more pages");
                    return;
                }//проверка на то, есть ли еще страницы.
                Log.i("OKAY", "adding more movies");

                List<MovieResults.ResultsBean> tempList = movieResults.getResults();
                listOfMovies.addAll(tempList);
                adapter.notifyItemInserted(listOfMovies.size());

            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {

                t.printStackTrace();
            }
        });
    }


}

//короче, завтра надо будет разобраться, куда пихать проверку на конец скролла, нужно выбрать метод, издеально было бы сделать onScrollChanged, но там нужна другая версия api.

//В общем, у нас будет один метод, в который мы будем передавать название категории, нужно еще на клике создать метод, который будет сбрасывать текущую страницу, делать ее нулем. Клики у нас будут в меню