package com.hasanalpzengin.typeandtalk;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class Category extends Fragment {

    FavoritesRecyclerAdapter recyclerAdapter;
    RecyclerView recyclerView;
    private ArrayList<Favorite> favorites;
    DBOperations dbOperations;
    String title;
    private TextToSpeech textToSpeech;

    public Category() {
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setFavorites(ArrayList<Favorite> favorites) {
        this.favorites = favorites;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        //define recyclerview
        recyclerView = view.findViewById(R.id.favoritesRecycler);
        //define favorites arrayList
        favorites = new ArrayList<>();
        //define db operations class
        dbOperations = new DBOperations(getContext());
        //define recyclerAdapter
        recyclerAdapter = new FavoritesRecyclerAdapter(getContext(), favorites);
        //define linearlayout for recyclerview
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //update adapter
        updateAdapter();
        recyclerView.setAdapter(recyclerAdapter);

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(new Locale(Locale.getDefault().getLanguage()));

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("textToSpeech", "This LanguageActivity is not supported");
                    }
                } else {
                    Log.e("textToSpeech", "Initilization Failed!");
                }
            }
        });

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("RecyclerView", "clicked");
            }
        });

        return view;
    }

    public boolean isExists(String text){
        for (Favorite favorite : favorites){
            Log.i("Favorite Check: ", favorite.getText()+":"+text);
            if (favorite.getText().contentEquals(text)){
                return true;
            }
        }
        return false;
    }

    public void updateAdapter(){
        dbOperations.open_readable();
        recyclerAdapter.favorites = dbOperations.getCategoryList(title);
        dbOperations.close_db();
        favorites = recyclerAdapter.favorites;
        recyclerAdapter.notifyDataSetChanged();
    }

    class FavoritesRecyclerAdapter extends RecyclerView.Adapter<FavoritesRecyclerAdapter.FavoritesViewHolder>{

        ArrayList<Favorite> favorites = new ArrayList<>();
        private Context context;

        public FavoritesRecyclerAdapter(Context context, ArrayList<Favorite> favoritesArray) {
            this.context = context;
            this.favorites = favoritesArray;
        }

        @Override
        public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflate
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.favorite_card, parent, false);
            return new FavoritesViewHolder(view);
        }

        @Override
        public void onBindViewHolder(FavoritesViewHolder holder, int position) {
            Favorite favorite = favorites.get(position);

            holder.activityName.setText(favorite.getText());
            if (favorite.getFavorite()==0){
                holder.starImage.setImageResource(R.mipmap.star);
            }else{
                holder.starImage.setImageResource(R.mipmap.selected_star);
            }
            holder.position = position;
        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }


        class FavoritesViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnCreateContextMenuListener{
            //view elements from activities_card layout
            String text;
            String category;
            TextView activityName;
            ImageView starImage;
            int position;

            public FavoritesViewHolder(View itemView) {
                super(itemView);
                //init
                activityName = itemView.findViewById(R.id.titleText);
                starImage = itemView.findViewById(R.id.starImage);
                itemView.setOnCreateContextMenuListener(this);

                starImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dbOperations.open_writable();
                        dbOperations.changeFavorite(activityName.getText().toString());
                        updateAdapter();
                        dbOperations.close_db();
                        Snackbar.make(recyclerView, activityName.getText().toString()+" "+getResources().getString(R.string.favoriteAdded), Snackbar.LENGTH_SHORT).show();
                    }
                });

                itemView.setOnClickListener(this);
                itemView.setOnCreateContextMenuListener(this);

            }

            @Override
            public void onClick(View view) {
                TextView tv = view.findViewById(R.id.titleText);
                textToSpeech.speak(tv.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuItem delete = contextMenu.add(0, 1, 0, R.string.delete);
                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        text = activityName.getText().toString();
                        category = favorites.get(position).getCategory();
                        //delete operation
                        dbOperations.open_writable();
                        dbOperations.deleteText(activityName.getText().toString());
                        dbOperations.close_db();
                        Snackbar.make(recyclerView, activityName.getText().toString()+" "+getResources().getString(R.string.textDeleted), Snackbar.LENGTH_SHORT).setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dbOperations.open_writable();
                                dbOperations.addText(text, category);
                                updateAdapter();
                                dbOperations.close_db();
                            }
                        }).show();
                        updateAdapter();
                        return true;
                    }
                });
            }
        }
    }

}
