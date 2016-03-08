package com.amusoft.shtaki;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;


public class ViewReports extends ActionBarActivity {
    Firebase myFirebaseRef;
    SharedPreferences SHTAKIprefferences;
    ArrayList<ReportItem> allBrews;
    ArrayList<ReportItem> allReports;

    static final int SET_SOUNDS = 1;


    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;

    FloatingActionButton  addreport;
//    MaterialProgressBar loadbar;


    ProgressDialog pDialog;


    private RecyclerView mRecyclerView;
    private ReportItemRecyclerAdapter adapter;
    ReportItem notificationItem;
    private NotificationManager mNotificationManager;
    private int notificationID = 100;
    private int numMessages = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);
        setToolBar();
        SHTAKIprefferences = getSharedPreferences(Constants.SHTAKI_PREFERENCES,
                Context.MODE_PRIVATE);
        Firebase.setAndroidContext(getApplicationContext());
        myFirebaseRef = new Firebase("https://shtaki.firebaseio.com/");




        allBrews=new ArrayList<ReportItem>();
        allReports=new ArrayList<ReportItem>();









        mRecyclerView = (RecyclerView) findViewById(R.id.listReports);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        final SharedPreferences.Editor editor22 = SHTAKIprefferences.edit();
        editor22.putString(Constants.NOTIFICATION_PREF, "true");
        editor22.commit();

        addreport=(FloatingActionButton)findViewById(R.id.add_button);
        addreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent xbrewtwp = new Intent(getApplicationContext(), PostReport.class);

                startActivity(xbrewtwp);
                finish();

            }
        });




        adapter = new ReportItemRecyclerAdapter(getApplicationContext(),allBrews);



        filldata();
//        for(int arr=allBrews.size();arr<0;arr--){
//            allReports.add((allBrews.get(arr)));
//
//        }









mRecyclerView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        int position = mRecyclerView.getChildPosition(v);

        Intent xbrew = new Intent(getApplicationContext(), ViewSingleReport.class);
        xbrew.putExtra("firekey",
                allBrews.get(position).getFireKey().toString());
        startActivity(xbrew);

    }
});



    }


    private void filldata(){


        Query queryReftwo = myFirebaseRef.child("REPORTS").orderByChild("VOTES").startAt("0");
        queryReftwo.keepSynced(true);

        queryReftwo.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                pDialog = new ProgressDialog(ViewReports.this);
                pDialog.setMessage("Fetching data");
                pDialog.show();

                Map<String, Object> newPost = (Map<String, Object>) snapshot.getValue();
                if (newPost != null) {

                    if (newPost.get("PICTURE") != null & newPost.get("PICTURE") != "") {
                        allBrews.add(new ReportItem(newPost.get("TITLE").toString(),
                                newPost.get("DESCRIPTION").toString(),
                                newPost.get("LOCATION").toString(),
                                newPost.get("INSTITUTION").toString(),
                                newPost.get("PICTURE").toString(),
                                snapshot.getKey(),
                                newPost.get("VOTES").toString()));

                    } else {
                        allBrews.add(new ReportItem(newPost.get("TITLE").toString(),
                                newPost.get("DESCRIPTION").toString(),
                                newPost.get("LOCATION").toString(),
                                newPost.get("INSTITUTION").toString(),
                                "-Jw7R2M88Bo-6AziZxB4",
                                snapshot.getKey(),
                                newPost.get("VOTES").toString()));

                    }
                    mRecyclerView.setAdapter(adapter);
                    pDialog.hide();
                    if (newPost.get("PICTURE") != null & newPost.get("PICTURE") != "") {
                        notificationItem=new ReportItem(newPost.get("TITLE").toString(),
                                newPost.get("DESCRIPTION").toString(),
                                newPost.get("LOCATION").toString(),
                                newPost.get("INSTITUTION").toString(),
                                newPost.get("PICTURE").toString(),
                                snapshot.getKey(),
                                newPost.get("VOTES").toString());

                    } else {
                        notificationItem=new ReportItem(newPost.get("TITLE").toString(),
                                newPost.get("DESCRIPTION").toString(),
                                newPost.get("LOCATION").toString(),
                                newPost.get("INSTITUTION").toString(),
                                "-Jw7R2M88Bo-6AziZxB4",
                                snapshot.getKey(),
                                newPost.get("VOTES").toString());

                    }




                    if (SHTAKIprefferences.contains(Constants.NOTIFICATION_PREF) &
                            SHTAKIprefferences.getString(Constants.NOTIFICATION_PREF, null).equals("true")) {



                    /* Invoking the default notification service */
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext());

                        mBuilder.setContentTitle("New Report");
                        mBuilder.setContentText(notificationItem.getTitle());

                        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
//                    mBuilder.setSound(Uri.parse("android.resource://"
//                            + getPackageName() + "/" + R.raw.notification_loaded));

      /* Increase notification number every time a new notification arrives */
                        mBuilder.setNumber(++numMessages);

      /* Creates an explicit intent for an Activity in your app */
                        Intent resultIntent = new Intent(ViewReports.this, ViewSingleReport.class);
                        resultIntent.putExtra("firekey",notificationItem.getFireKey());


                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                        stackBuilder.addParentStack(ViewSingleReport.class);

      /* Adds the Intent that starts the Activity to the top of the stack */
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_CANCEL_CURRENT
                                );

                        mBuilder.setContentIntent(resultPendingIntent);

                        mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

      /* notificationID allows you to update the notification later on. */
                        mNotificationManager.notify(notificationID, mBuilder.build());


                    }
                }


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
//        for(int arr=allBrews.size();arr<0;arr--){
//            allReports.add((allBrews.get(arr)));
//
//        }






    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarViewReport);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
            setUpActionbar();
            getOverflowMenu();
            toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
            toolbar.setTitleTextColor(getResources().getColor(R.color.white_pure));






        }

    }

    private void setUpActionbar() {
        if(getSupportActionBar()!=null){
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

            if(menuKeyField != null) {

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
        getMenuInflater().inflate(R.menu.menu_view_reports, menu);
        mSearchAction = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_search) {
            handleMenuSearch();
            return true;
        }
        if(id == R.id.action_invite){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    "Join the fight aganist corruption and " +
                            "download the Shtaki app " +
                    "\n https://play.google.com/store/apps/details?id=com.amusoft.shtaki");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);

        }
        if(id==R.id.action_notification_settings){
            ViewReports.this.showDialog(SET_SOUNDS);
        }
        if(id==R.id.action_about_activity){
            Intent upIntent = new Intent(getApplicationContext(),AboutActivity.class);
            startActivity(upIntent);
            finish();
            return true;
        }






        return super.onOptionsItemSelected(item);
    }
    protected void handleMenuSearch(){
        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_open_search));
            allBrews.clear();
            allReports.clear();
            filldata();



            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.searchbar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch(v);
                        return true;
                    }
                    return false;
                }
            });

            edtSeach.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_close_search));

            isSearchOpened = true;
        }
    }




    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    private void doSearch(TextView v) {
        String type=v.getText().toString();
        Query queryRef = myFirebaseRef.child("REPORTS").
                orderByChild("TITLE").
                equalTo(type).limitToFirst(1);

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                Map<String, Object> newPost = (Map<String, Object>) snapshot.getValue();
                if (newPost != null) {
                    allBrews.clear();
                    allReports.clear();
                    pDialog = new ProgressDialog(ViewReports.this);
                    pDialog.setMessage("Fetching data");
                    pDialog.show();

                    if (newPost.get("PICTURE") != null & newPost.get("PICTURE") != "" & newPost.get("PICTURE") != "") {
                        allBrews.add(new ReportItem(newPost.get("TITLE").toString(),
                                newPost.get("DESCRIPTION").toString(),
                                newPost.get("LOCATION").toString(),
                                newPost.get("INSTITUTION").toString(),
                                newPost.get("PICTURE").toString(),
                                snapshot.getKey(),
                                newPost.get("VOTES").toString()));

                    } else {
                        allBrews.add(new ReportItem(newPost.get("TITLE").toString(),
                                newPost.get("DESCRIPTION").toString(),
                                newPost.get("LOCATION").toString(),
                                newPost.get("INSTITUTION").toString(),
                                "-Jw7R2M88Bo-6AziZxB4",
                                snapshot.getKey(),
                                newPost.get("VOTES").toString()));

                    }


//
//                    listAllBrews.setAdapter(adapters);
//                    for(int arr=allBrews.size();arr<0;arr--){
//                        allReports.add((allBrews.get(arr)));
//
//                    }
pDialog.hide();
                    mRecyclerView.setAdapter(adapter);


                }


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



//
    }
    @Override
    protected Dialog onCreateDialog(final int id) {
        switch (id) {
            case SET_SOUNDS:
                final LayoutInflater inflater2 =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layoutset =
                        inflater2.inflate(R.layout.dialog_sounds,
                                (ViewGroup) findViewById(R.id.dia_sounds));

                final CheckBox g = (CheckBox) layoutset.findViewById(R.id.checkBoxSounds);
                if(SHTAKIprefferences.contains(Constants.NOTIFICATION_PREF)
                        &
                SHTAKIprefferences.getString(Constants.NOTIFICATION_PREF,null).equals("true")){
                    g.setChecked(true);
                }



                g.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                        if (isChecked) {
                            final SharedPreferences.Editor editor22 = SHTAKIprefferences.edit();
                            editor22.putString(Constants.NOTIFICATION_PREF, "true");
                            editor22.commit();

                            g.setChecked(true);

                        }
                        else if (!isChecked) {
                            final SharedPreferences.Editor editor222 = SHTAKIprefferences.edit();
                            editor222.putString(Constants.NOTIFICATION_PREF, "false");
                            editor222.commit();
                            g.setChecked(false);




                        }


                    }
                });

                final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setView(layoutset);

                builder2.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int which) {
                                ViewReports.this.removeDialog(SET_SOUNDS);



                            }
                        });
                final AlertDialog lDialog1 = builder2.create();
                return lDialog1;


        }
        return null;
    }

}
