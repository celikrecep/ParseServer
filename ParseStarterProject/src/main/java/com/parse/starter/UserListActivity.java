package com.parse.starter;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.Manifest;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    //fotoğraf alma işlemi için açılması gereken kısmı ayarladık
    //galeri kısmı açılacak
    public void getPhoto(){

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //zaten izin verişmişse onu ayarladık
        if(requestCode ==1){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                getPhoto();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //burda activty adı
        menuInflater.inflate(R.menu.menu_share,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            //burda menudeki itemid
        if(item.getItemId() == R.id.shareMenu){

            //izni kontrol ettik
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }else{

                    getPhoto();
                }
            }else{

                getPhoto();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    // fotoğraf seçme işlemini yapıyoruz
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // requestcodumuz 1 ve tamam sa datamızı da kontrol ettikten sonra
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            // imyi çekiyoruz
            Uri selectedImage = data.getData();

            try {
                    //bitmap oluşturduk
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);

                Log.i("Photo","Received");

                //parseservera fotoğrafların upload işlemi

                ByteArrayOutputStream stream =  new ByteArrayOutputStream();

                //türünü kalitesini belirledik(100)
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                // fotoğraf import ederken bytearraye convert olmalı
                byte[] byteArray = stream.toByteArray();


                ParseFile file = new ParseFile("image.png",byteArray);

                ParseObject object = new ParseObject("Image");

                object.put("image",file);

                object.put("Username",ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e == null){

                            Toast.makeText(UserListActivity.this,"Image shared!",Toast.LENGTH_SHORT).show();
                        }else{

                            Toast.makeText(UserListActivity.this,"Image could not be shared - please try again later.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);




       final ListView userList = (ListView) findViewById(R.id.userListView);

        final ArrayList<String> usernames = new ArrayList<>();

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //burda fotoğrafların görüntüleneceği activityi açtık
               Intent intent = new Intent(getApplicationContext(),UserFeedActivity.class);
                //hangi kullanıcıda olduğunu görmek için
                intent.putExtra("Username",usernames.get(i));
                //başlattık
                startActivity(intent);
            }
        });



        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,usernames);





        //sunucudan kişileri çekicez şimdi
        ParseQuery<ParseUser> query = ParseUser.getQuery();
            //getobjectıd sayesinde kişilerin idlerini çektik oraua user deseyik şuanki username saymayacaktı tıpkı
        //şuanki idyi saymadığı gibi
        query.whereNotEqualTo("objectId",ParseUser.getCurrentUser().getObjectId());
        // aldığı parametreye göre artandan azalana doğru sıralar yani son olan sonda olur
        //adddescendindorder da aldığı parametreye göre son olanı ilk yazar yani azalandan artana doğru
        query.addAscendingOrder("objectId");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e ==null){

                    if(objects.size()>0){


                        for(ParseUser user : objects){

                            usernames.add(user.getObjectId());
                        }

                        userList.setAdapter(adapter);
                    }
                }
            }
        });


}}
