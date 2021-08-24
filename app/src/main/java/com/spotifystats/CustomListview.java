package com.spotifystats;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomListview<T> extends ArrayAdapter<T> {

    private ArrayList<T> data;
    private int[] rImage;
    private Activity context;

    public CustomListview(Activity context,@LayoutRes int layout, ArrayList<T> data) {

        super(context, layout, data);
        this.context = context;
        this.data = data;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View r = convertView;
        ViewHolder viewHolder = null;
        if(r == null){
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.listview_layout,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) r.getTag();
        }

        T obj = data.get(position);
        //Listener when user click on the artist
        r.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               //Call event
                Intent intent = new Intent(Intent.ACTION_VIEW, ((SpotifyData)obj).getUri());


                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }else {
                    Log.d("ImplicitIntents", "Can't handle this intent!");
                }
            }

        });

        viewHolder.imageView.setImageBitmap(((SpotifyData)obj).getImage());
        viewHolder.textView1.setText(Integer.toString(position+1));
        viewHolder.textView2.setText(((SpotifyData)obj).getName());


        return r;
    }





    class ViewHolder{
        TextView textView1;
        TextView textView2;
        ImageView imageView;
        ViewHolder(View v){
            textView1 = v.findViewById(R.id.rank_label);
            textView2 = v.findViewById(R.id.name_label);
            imageView = v.findViewById(R.id.cover_image);

        }
    }
}
