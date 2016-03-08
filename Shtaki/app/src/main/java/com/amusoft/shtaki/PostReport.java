package com.amusoft.shtaki;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostReport extends ActionBarActivity {
    Firebase myFirebaseRef;
    SharedPreferences SHTAKIprefferences;
    EditText bTitle, bDescription, bLocation, binstitution;
    Button bPost;
    ImageButton photo;
    static final int AVATAR_DIALOG_ID = 2;
    static final int TAKE_AVATAR_CAMERA_REQUEST = 1;
    static final int TAKE_AVATAR_GALLERY_REQUEST = 2;

    List<Address> locationList;

    private Geocoder gc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_report);
        setToolBar();
        SHTAKIprefferences = getSharedPreferences(Constants.SHTAKI_PREFERENCES,
                Context.MODE_PRIVATE);
        Firebase.setAndroidContext(getApplicationContext());
        myFirebaseRef = new Firebase("https://shtaki.firebaseio.com/");
        mgetLocationPosted();
        bTitle = (EditText) findViewById(R.id.brewTitle);
        bDescription = (EditText) findViewById(R.id.brewDescription);
        binstitution = (EditText) findViewById(R.id.brewInstitution);
        bLocation = (EditText) findViewById(R.id.brewLocation);
        bPost = (Button) findViewById(R.id.postBrew);
        photo = (ImageButton) findViewById(R.id.postImage);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostReport.this.showDialog(AVATAR_DIALOG_ID);


            }
        });

        bPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> postBrew = new HashMap<String, Object>();
                postBrew.put("TITLE",
                        bTitle.getText().toString());
                postBrew.put("DESCRIPTION",
                        bDescription.getText().toString());
                postBrew.put("LOCATION",
                        bLocation.getText().toString());
                postBrew.put("INSTITUTION",
                        binstitution.getText().toString());
                if(SHTAKIprefferences.contains(Constants.CURRENT_IMAGE)){
                    postBrew.put("PICTURE",
                            SHTAKIprefferences.getString(Constants.CURRENT_IMAGE, null));

                }else {
                    postBrew.put("PICTURE","");

                }

                postBrew.put("VOTES",String.valueOf(0));
                Map<String, Object> postGeo = new HashMap<String, Object>();
                postGeo.put("Lat",SHTAKIprefferences.getString(Constants.CURRENT_LATITUDE,null));
                postGeo.put("Long",SHTAKIprefferences.getString(Constants.CURRENT_LONGITUDE, null));

                postBrew.put("GEOTAG", postGeo);



                myFirebaseRef.child("REPORTS").push().setValue(postBrew);
                bTitle.setText("");
                bTitle.setText("");
                bDescription.setText("");
                binstitution.setText("");
                bLocation.setText("");
                photo.setImageDrawable(null);
                photo.setImageBitmap(null);
                photo.setImageURI(null);
                photo.setImageDrawable(getResources().getDrawable(R.drawable.index));
                SharedPreferences.Editor erem = SHTAKIprefferences.edit();
                erem.remove(Constants.CURRENT_IMAGE);
                erem.commit();
                Toast.makeText(getApplicationContext(),
                        "Successfully submitted your report", Toast.LENGTH_SHORT).show();


                Intent i = new Intent(getApplicationContext(), ViewReports.class);
                startActivity(i);
                finish();

            }
        });


    }

    private void mgetLocationPosted() {
        Context ctx = getApplicationContext();

        gc = new Geocoder(ctx);


        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (lm.isProviderEnabled(WIFI_SERVICE))

        {
            try {
                locationList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                SharedPreferences.Editor editorlat = SHTAKIprefferences.edit();
                editorlat.putString(Constants.CURRENT_LATITUDE, String.valueOf(location.getLatitude()));
                editorlat.commit();

                SharedPreferences.Editor editorlong = SHTAKIprefferences.edit();
                editorlong.putString(Constants.CURRENT_LONGITUDE, String.valueOf(location.getLongitude()));
                editorlong.commit();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }


        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                try {
//                    cord.setText( "Latitude : "+location.getLongitude()+" Longitude : "+location.getLongitude());

                    if (lm.isProviderEnabled(WIFI_SERVICE)) {
                        locationList = gc.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        SharedPreferences.Editor editorlat = SHTAKIprefferences.edit();
                        editorlat.putString(Constants.CURRENT_LATITUDE, String.valueOf(location.getLatitude()));
                        editorlat.commit();

                        SharedPreferences.Editor editorlong = SHTAKIprefferences.edit();
                        editorlong.putString(Constants.CURRENT_LONGITUDE, String.valueOf(location.getLongitude()));
                        editorlong.commit();
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub

            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub

            }
        };

        //Change to LocationManager.GPS_PROVIDER to access GPS cordinates, Recommend way is to use Crietria
        //The update frequency is set to 500 which will drain battery change it to higher value for increasing

        //update time
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)
        lm.getBestProvider(criteria, true);

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);


    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case AVATAR_DIALOG_ID:
                LayoutInflater inflater2 =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout2 =
                        inflater2.inflate(R.layout.avatar_picker,
                                (ViewGroup) findViewById(R.id.chooseAva));
                final Button btnCamera = (Button) layout2.findViewById(R.id.camera);
                final Button btnGallery = (Button) layout2.findViewById(R.id.gallery);
                btnCamera.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent pictureIntent = new Intent(
                                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(pictureIntent, TAKE_AVATAR_CAMERA_REQUEST);
                        PostReport.this
                                .removeDialog(AVATAR_DIALOG_ID);


                    }
                });
                btnGallery.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                        pickPhoto.setType("image/*");
                        startActivityForResult(pickPhoto, TAKE_AVATAR_GALLERY_REQUEST);
                        PostReport.this
                                .removeDialog(AVATAR_DIALOG_ID);

                    }
                });

                AlertDialog.Builder builder2 = new AlertDialog.Builder(PostReport.this);
                builder2.setView(layout2);
                builder2.setTitle("pick photo");
                builder2.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                PostReport.this
                                        .removeDialog(AVATAR_DIALOG_ID);

                            }
                        });

                builder2.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                PostReport.this
                                        .removeDialog(AVATAR_DIALOG_ID);
                            }
                        });

                AlertDialog chooseDialog = builder2.create();
                return chooseDialog;

        }


        return null;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_AVATAR_CAMERA_REQUEST:
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Avatar camera mode was canceled.
                } else if (resultCode == Activity.RESULT_OK) {
                    // TODO: HANDLE PHOTO TAKEN
                    Bitmap cameraPic = (Bitmap) data.getExtras().get("data");
                    saveAvatar(cameraPic);
                }
                break;
            case TAKE_AVATAR_GALLERY_REQUEST:
                if (resultCode == Activity.RESULT_CANCELED) {
                    // Avatar gallery request mode was canceled.
                } else if (resultCode == Activity.RESULT_OK) {
                    // TODO: HANDLE IMAGE CHOSEN
                    Uri photoUri = data.getData();
                    try {
                        Bitmap galleryPic = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);

                        saveAvatar(galleryPic);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void saveAvatar(Bitmap avatar) {
// TODO: Save the Bitmap as a local file called avatar.jpg
        String strAvatarFilename = "avatar.jpg";
        try {
            avatar.compress(Bitmap.CompressFormat.JPEG,
                    100, openFileOutput(strAvatarFilename, MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

// TODO: Determine the Uri to the local avatar.jpg file

// TODO: Save the Uri path as a String preference
        Uri imageUri = Uri.fromFile(new File(getFilesDir(), strAvatarFilename));
// TODO: Update the ImageButton with the new image
        photo.setImageBitmap(null);
        photo.setImageDrawable(null);
        photo.setImageBitmap(avatar);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        avatar.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        SharedPreferences.Editor editorwa = SHTAKIprefferences.edit();
        editorwa.putString(Constants.CURRENT_IMAGE, image);
        editorwa.commit();


    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSingleStack);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
            setUpActionbar();
            getOverflowMenu();
            toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white_pure));


        }

    }

    private void setUpActionbar() {
        if (getSupportActionBar() != null) {
            ActionBar bar = getSupportActionBar();
            bar.setTitle(getResources().getString(R.string.app_name));
            bar.setHomeButtonEnabled(false);
            bar.setDisplayShowHomeEnabled(false);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowTitleEnabled(true);


            bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        }


    }

    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);

            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {

                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
                menuKeyField.isSynthetic();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_report, menu);
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
        if(id == R.id.action_invite_post){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Join the fight aganist corruption and " +
                            "download the Shtaki app " +
                            "\n https://play.google.com/store/apps/details?id=com.amusoft.shtaki");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        }

        return super.onOptionsItemSelected(item);
    }


}
