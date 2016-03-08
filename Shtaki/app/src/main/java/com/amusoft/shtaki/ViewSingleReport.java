package com.amusoft.shtaki;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class ViewSingleReport extends ActionBarActivity {
    Firebase myFirebaseRef;
    SharedPreferences   SHTAKIprefferences;



    TextView singleLocation,singleInstitution,singleTitle,singleDescription,singleVotes;

    ImageView imPhoto;

    ReportItem reportSingle;
    String path;

    ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_report);
        setToolBar();


        SHTAKIprefferences = getSharedPreferences(Constants.SHTAKI_PREFERENCES,
                Context.MODE_PRIVATE);
        Firebase.setAndroidContext(getApplicationContext());
        myFirebaseRef = new Firebase("https://shtaki.firebaseio.com/");


        singleLocation=(TextView)findViewById(R.id.singleLocaton);
        singleInstitution=(TextView)findViewById(R.id.singleInstitution);
        singleTitle=(TextView)findViewById(R.id.singleTitle);
        singleDescription=(TextView)findViewById(R.id.singleDescription);
        imPhoto=(ImageView)findViewById(R.id.showSingleBrewImage);
        singleVotes=(TextView)findViewById(R.id.singleVotes);





        Intent i = getIntent();
        final String key=i.getExtras().getString("firekey");
        pDialog = new ProgressDialog(ViewSingleReport.this);
        pDialog.setMessage("Fetching data");
        pDialog.show();


        Query queryRef = myFirebaseRef.child("REPORTS").orderByKey()
                .equalTo(key);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map<String, Object> newPost = (Map<String, Object>) dataSnapshot.getValue();
                if (newPost != null) {

                reportSingle=new ReportItem(newPost.get("TITLE").toString(),
                            newPost.get("DESCRIPTION").toString(),
                            newPost.get("LOCATION").toString(),
                            newPost.get("INSTITUTION").toString(),
                            newPost.get("PICTURE").toString(),
                            dataSnapshot.getKey(),
                            newPost.get("VOTES").toString());


                }

                singleLocation.setText(reportSingle.getLocation());
                singleInstitution.setText(reportSingle.getInstitution());

                singleTitle.setText(reportSingle.getTitle());
                singleDescription.setText(reportSingle.getDescription());

                String photoBrew = reportSingle.getPicture();
                if (photoBrew != null & photoBrew !="") {
                    byte[] imageAsBytes = Base64.decode(photoBrew.getBytes(), Base64.DEFAULT);
                    imPhoto.setImageURI(null);
                    imPhoto.setImageBitmap(null);

                    Bitmap filanphoto=BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    Drawable d=new BitmapDrawable(getResources(),filanphoto);
                    imPhoto.setImageDrawable(d);


                }else{
                    imPhoto.setImageURI(null);
                    imPhoto.setImageBitmap(null);
                    imPhoto.setImageDrawable(null);
                    imPhoto.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));


                }

                singleVotes.setText(reportSingle.getVotes()+" people relate to this report");
                pDialog.dismiss();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        imPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            
                Bitmap bitmap = ((BitmapDrawable)imPhoto.getDrawable()).getBitmap();


               path= Environment.getExternalStorageDirectory().toString()+ "/image.png";


                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }




                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(path)), "image/png");
                startActivity(intent);
            }
        });


    }
    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewSingleReport);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
            setUpActionbar();

            toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white_pure));






        }

    }

    private void setUpActionbar() {
        if(getSupportActionBar()!=null){
            ActionBar bar = getSupportActionBar();
            bar.setTitle(getResources().getString(R.string.app_name));
            bar.setHomeButtonEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);



            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_single_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id==android.R.id.home){
            Intent upIntent = new Intent(getApplicationContext(),ViewReports.class);
            startActivity(upIntent);
            finish();

        }
        if(id==R.id.action_relate_single){
            Integer vot=Integer.valueOf(reportSingle.getVotes());
            vot++;
            reportSingle.setVotes(String.valueOf(vot));


            myFirebaseRef.child("REPORTS").child(reportSingle.getFireKey()).child("VOTES").setValue(String.valueOf(vot));
    Toast.makeText(getApplicationContext(), "Vote Sucesfully Added", Toast.LENGTH_SHORT).show();
            singleVotes.setText(reportSingle.getVotes()+" people relate to this report");
        }
        if(id == R.id.action_share_single){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                            reportSingle.getTitle() + "\n\n" +
                            reportSingle.getLocation() + "\n"
                            + reportSingle.Description+

                    "\n \n" +"Read more reports through the Staki app"+
                                    " https://play.google.com/store/apps/details?id=com.amusoft.shtaki");

            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        }

        return super.onOptionsItemSelected(item);
    }
}
