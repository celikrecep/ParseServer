/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.lang.annotation.ElementType;
import java.util.List;

// onclick fonksiyonunu implemente ederek her bir tıklama işlemi için ayrı ayrı yazmak zorunda kalmıycaz
public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

  //burda idyi çekip olacak işlemi if bloğuna soktuk
  TextView changeSignUpModeTextView;
  EditText passwordEditText;


  //userlist için acitivity geçiş işlemlerini yapıyoruz

  public void showUserList()
  {

    Intent intent = new Intent(getApplicationContext(),UserListActivity.class);

    startActivity(intent);

  }

  boolean signUpModeActive = true;

  //klavye ayarları
  @Override
  public boolean onKey(View view, int i, KeyEvent keyEvent) {

    //entera basıldığında ne olacağını seçiyoruz
    //&& sonrasını ekleyerek varsayılan olarak gelen iki kere tıklamayı engelledik. bir kez dokunacak
    if(i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == keyEvent.ACTION_DOWN)
    {
      signUp(view);
    }

    return false;
  }

  @Override
  public void onClick(View view) {

    if(view.getId() == R.id.changeSignUpModeTextView)
    {
      Button signUpButton = (Button) findViewById(R.id.signupButton);

      if(signUpModeActive){

        signUpModeActive = false;
        signUpButton.setText("Login");
        changeSignUpModeTextView.setText("Or, Signup");


      }else{

        signUpModeActive = true;
        signUpButton.setText("Signuo");
        changeSignUpModeTextView.setText("Or, login");

      }
    // klavye dışında bir yere dokunulduğunda klavyeyi kapatma eylemini yapıyoruz
    }else if(view.getId() == R.id.backgroundRelativeLayout || view.getId() == R.id.logoImageView){

      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

    }

  }
  //Server işlemleri ekleme user oluşturma vs
  //fonksiyonumuzu tanımlayalım.

  public void signUp(View view){

    //edittextlerimizi tanımlayalım;
    EditText usernameEditText = (EditText) findViewById(R.id.usernameEditText);


    //her iki edittextimizin de kontrolünü yapıyoruz
    //sağlıklı sonuç alabilmek adına matches fonksiyonunu kullanıcaz
    if(usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")){
      //toast mesajıyla kullanıcıya hata mesajı gönderiyoruz

      Toast.makeText(this,"A username and password required",Toast.LENGTH_LONG).show();

      //eğer boş değilse kullanıcı oluşturuyoruz
    }else {
      if(signUpModeActive){
      ParseUser user = new ParseUser();
      //kullanıcı adımızı aldık
      user.setUsername(usernameEditText.getText().toString());
      //şifremizi alıyoruz
      user.setPassword(passwordEditText.getText().toString());

      //kaydımızı yapıyoruz

      user.signUpInBackground(new SignUpCallback() {
        @Override
        public void done(ParseException e) {

          if(e == null){

            Log.i("signup","Succesfull");

          }else {

            Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
          }
        }
      });
          //kayıt işleminin ardından geçişi yapıyoruz
        showUserList();

    }else{
        ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {

            if(user != null){
              Log.i("Login","Succesful");
            }else{
              Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
            }
          }
        });
      //login işleminin ardından geçiş işlemini yapıyoruz.
        showUserList();
      }


    }


  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // burda da textviewi tanımlayıp tıklama eylemi gerçekleşince olacakları çağırdık
    changeSignUpModeTextView = (TextView) findViewById(R.id.changeSignUpModeTextView);

    changeSignUpModeTextView.setOnClickListener(this);

    passwordEditText = (EditText) findViewById(R.id.passwordEditText);

    passwordEditText.setOnKeyListener(this);

    RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.backgroundRelativeLayout);

    ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

    backgroundRelativeLayout.setOnClickListener(this);

    logoImageView.setOnClickListener(this);

    //sistemde açık bir hesap varsa yine geçiş işlemini yapıyoruz
    if(ParseUser.getCurrentUser() != null){

      showUserList();
    }


        /*
   ParseObject score = new ParseObject("Score");
      score.put("Username","Recep");
      score.put("puan",12);
        //kayıt işlemi burda yapılıyor.
      score.saveInBackground(new SaveCallback() {
          @Override
          public void done(ParseException e) {

              if(e == null)
              {
                  Log.i("saveInBackground","Başarılı");

              }else {

                  Log.i("saveInBackground","Başarısız " + e.toString());
              }
          }
      });
      */

            // sunucudan veri çekmek için
     /* ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
      // sunucudaki verinin idsini giriyoruz
      query.getInBackground("nBdDBu0vFq", new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject object, ParseException e) {

              if(e == null && object != null)
              {
                  //güncellemek için ise bunları yapıyrouz
                  //idsi başta girildiği için sadece bu idye bağlı değişkenlerde işlem yapıyor
                  object.put("puan",20);
                  //tekrar kaydediyoruz
                  object.saveInBackground();

                  Log.i("ObjectValue ", object.getString("Username"));
                  Log.i("OnjectValue", Integer.toString(object.getInt("puan")));
              }
          }
      });*/

     /*ParseObject tweet = new ParseObject("Tweet");
      tweet.put("username","tahir");
      tweet.put("Content","Selam gençler nasılsınız");
      tweet.saveInBackground(new SaveCallback() {
          @Override
          public void done(ParseException e) {
              if(e == null)
              {
                  Log.i("Tweet","Başarılı");
              }else{
                  Log.i("Tweet","Başarısız " + e.toString());
              }
          }
      });
*/
    /* ParseQuery<ParseObject> tweetQuery = ParseQuery.getQuery("Tweet");
      tweetQuery.getInBackground("Bq7ILUvEPT", new GetCallback<ParseObject>() {
          @Override
          public void done(ParseObject object, ParseException e) {

              // bu kontrolü yapmazsan eğer güncellenmiyor
              if(e == null && object != null) {

                  

                  object.put("Content", "merhaba abi hoşgeldin! loooooo");
                  object.saveInBackground();
              }

          }
      });*/


   /* ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");

      //özel arama işlemi yapmak için
      query.whereEqualTo("Username","tahir");
      // kaç tanesini yazdırmasını istiyorsak onu belirledik
      query.setLimit(1);
      //bulma işlemi için find komutunu kullanıyoruz
      query.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> objects, ParseException e) {


              if(e == null)
              {         //kaç tane veri içerdiğine bakıyoruz.
                  Log.i("findInBackGround","Retrieved " + objects.size()+" objects");
                    // eğer veri içeriyorsa bu verilerini yazdırıyoruz
                  if(objects.size()>0)
                  {
                        // bulunan verileri yazdırıyor
                      for(ParseObject object : objects)
                      {     //tüm verileri yazdırıyor
                          //Log.i("findInBackGround" ,object.toString());
                          // istenen kritere göre yazdırıyor
                          Log.i("findInBackGroundResult",object.getString("Username"));
                          //puanı çektik
                          Log.i("findInBackGroundResult",Integer.toString(object.getInt("puan")));
                      }
                  }

              }
          }
      });*/

        //puanları 20den büyükse 30 ekleyen bir kod yazıcaz


     /* ParseQuery<ParseObject> query = ParseQuery.getQuery("Score");
        // bu fonksiyon sayesinde kontrollerimizi yaptık
      query.whereGreaterThanOrEqualTo("puan",20);

      query.findInBackground(new FindCallback<ParseObject>() {
          @Override
          public void done(List<ParseObject> objects, ParseException e) {

              if(e == null && objects != null)
              {
                  for(ParseObject object:objects)
                  {
                      // ekleme işlemini yaptık
                      object.put("puan",object.getInt("puan")+30);
                      // ve kaydettik
                      object.saveInBackground();
                  }

              }

          }
      });*/


     /*
     //user oluşturmak için
      ParseUser user = new ParseUser();
      // kullanıcı adı
      user.setUsername("Raco");
      user.setPassword("recep.1997");

      // giriş yaptık
      user.signUpInBackground(new SignUpCallback() {
          @Override
          public void done(ParseException e) {

              //kontrol ediyoruz hata var mı yok mu diye
              if(e == null)
              {
                  Log.i("Sign Up","Succesful");
              }else{
                  Log.i("Sign Up","Failed");
              }

          }
      }); */

     /*
     //oluşturduğumuz kullanıcı adı ve şifreyi kullanarak
      // giriş yapmayı deniyoruz

      ParseUser.logInInBackground("Recep", "asdf", new LogInCallback() {
          @Override
          public void done(ParseUser user, ParseException e) {

              if(user != null){
                  Log.i("Log In","Succesful");
              }else{

                  Log.i("Log In","Failed" + e.toString());
              }
          }
      });
*/


     // çıkış yapmak için ise

       /*ParseUser.logOut();
     // Sistemde ki şuanki kullanıcıyı görmek istiyoruz
      if(ParseUser.getCurrentUser() != null)
      {
          Log.i("currentUser","Logged in " + ParseUser.getCurrentUser().getUsername().toString());

      }else{
          Log.i("currentUser","not logged in");
      }
      */









    ParseAnalytics.trackAppOpenedInBackground(getIntent());
  }


}