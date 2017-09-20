package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class UserFeedActivity extends AppCompatActivity {


    LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);
        linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);

        Intent intent = getIntent();

        //önceki ekranda aldığımız usernami çekiyoruz

        String activeUser = intent.getStringExtra("Username");

        //başlıkta yazdırıcaz

        setTitle(activeUser + "'s Feed");


        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");

        query.whereEqualTo("objectId",activeUser);
        query.orderByDescending("createdAT");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if(e == null){
                    if(objects.size() > 0){

                        for(ParseObject object : objects){

                            ParseFile file = (ParseFile) object.get("image");

                            file.getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] data, ParseException e) {
                                    if(e == null){

                                        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                        //imageview sınıfını uluşturup
                                        ImageView imageView = new ImageView(getApplicationContext());

                                        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                                                //buraya widt değerini
                                                ViewGroup.LayoutParams.MATCH_PARENT,
                                                //burayada height değerini
                                                ViewGroup.LayoutParams.WRAP_CONTENT
                                        ));

                                        //fotoğragı ilk baştaki instagram logosuna ekledik sanırım
                                        imageView.setImageBitmap(bitmap);


                                        //sonra da oluşturduğumuz linearlayouta ekledik
                                        linearLayout.addView(imageView);


                                    }
                                }
                            });
                        }
                    }
                }
            }
        });





    }

}
