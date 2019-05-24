package com.example.user.movieapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private List<MovieResults.ResultsBean> myMovieList;

    public static class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView myTextView;
        public ImageView myImageView;


        public CustomViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textView);
            myImageView = itemView.findViewById(R.id.imageView);
        }
    }


    public CustomAdapter(List<MovieResults.ResultsBean> movieList) {
        myMovieList = movieList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(v);
        return viewHolder;//То есть здесь создается вью для каждой карточки.
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {//в этом методе нужно будет присваивать информацию для элементов вью, сначала создаем объект предмета, который находится в списке, затем ищем нужный объект для вьюшки с помощью get(position), затем просто присваиваем всю информацию во всякие поля. holder будет содержать саму вью.

        MovieResults.ResultsBean currentMovieItem = myMovieList.get(position);

        holder.myTextView.setText(currentMovieItem.getTitle());
        Picasso.get().load("https://image.tmdb.org/t/p/w600_and_h900_bestv2/" + currentMovieItem.getPoster_path()).into(holder.myImageView);


    }

    @Override
    public int getItemCount() {
        return myMovieList.size();
    }



}
