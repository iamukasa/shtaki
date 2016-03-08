package com.amusoft.shtaki;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by irving on 9/2/15.
 */
public class ReportItemRecyclerAdapter  extends RecyclerView.Adapter<ReportItemRecyclerAdapter.CustomViewHolder> {
    private List<ReportItem> feedItemList;
    private Context mContext;

    public ReportItemRecyclerAdapter(Context context, List<ReportItem> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.report_item, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, int i) {
        final ReportItem feedItem = feedItemList.get(i);



        customViewHolder.tool.setTitle(feedItem.getTitle());
        customViewHolder.tool.setTitleTextColor(mContext.getResources().getColor(R.color.white_pure));
        customViewHolder.tool.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
        customViewHolder.tool.inflateMenu(R.menu.menu_post_item);
        customViewHolder.tool.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id==R.id.action_relate){
                    Integer vot=Integer.valueOf(feedItem.getVotes());
                    vot++;
                    feedItem.setVotes(String.valueOf(vot));


                    Firebase.setAndroidContext(mContext);
                Firebase  myFirebaseRef = new Firebase("https://shtaki.firebaseio.com/");
                    myFirebaseRef.child("REPORTS").child(feedItem.getFireKey()).child("VOTES").setValue(String.valueOf(vot));
                    Toast.makeText(mContext, "Vote Sucesfully Added", Toast.LENGTH_SHORT).show();
                    customViewHolder.tvType.setText(feedItem.getVotes() + " people relate to this report");

                }
                if(id == R.id.action_share){
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                                     feedItem.getTitle()+"\n"+
                                    feedItem.getLocation()+"\n"
                                    +feedItem.Description+
                                             "\n \n" +"Read more reports through the Staki app"+
                                             " https://play.google.com/store/apps/details?id=com.amusoft.shtaki");
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    sendIntent.setType("text/plain");
                    mContext.startActivity(sendIntent);

                }
                return false;
            }
        });
        if (customViewHolder.tvType != null) {
            customViewHolder.tvType.setText(feedItem.getVotes()+" people relate to this report");
        }



//
//        if ( customViewHolder.tvDescription != null) {
//            customViewHolder.tvDescription.setText(feedItem.getDescription());
//        }
        if ( customViewHolder.tvSender!= null) {
            customViewHolder.tvSender.setText("at :" + feedItem.getLocation());
        }
        if( customViewHolder.imPhoto!=null){
            if(feedItem.getPicture() !=null){
                byte[] imageAsBytes = Base64.decode(feedItem.getPicture().getBytes(), Base64.DEFAULT);
                customViewHolder. imPhoto.setImageURI(null);
                customViewHolder. imPhoto.setImageBitmap(null);
                customViewHolder.imPhoto.setImageBitmap(
                        BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
                );

            }else if("-Jw7R2M88Bo-6AziZxB4".contentEquals(feedItem.getPicture())){
                customViewHolder.imPhoto.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));

            }
            else {
                customViewHolder.imPhoto.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
            }


        }


        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomViewHolder holder = (CustomViewHolder) view.getTag();
                int position = holder.getPosition();


                Intent xbrew = new Intent(mContext, ViewSingleReport.class);
                xbrew.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    xbrew.putExtra("firekey",
                            feedItemList.get(position).getFireKey().toString());

                mContext.startActivity(xbrew);


            }
        };
        //Handle click event on both title and image click
        customViewHolder.tvType.setOnClickListener(clickListener);
//        customViewHolder.tvDescription.setOnClickListener(clickListener);
        customViewHolder.tvSender.setOnClickListener(clickListener);
        customViewHolder.imPhoto.setOnClickListener(clickListener);
//        customViewHolder.t.setOnClickListener(clickListener);

        customViewHolder.tvType.setTag(customViewHolder);
//        customViewHolder.tvDescription.setTag(customViewHolder);
        customViewHolder.tvSender.setTag(customViewHolder);
        customViewHolder.imPhoto.setTag(customViewHolder);
        setAnimation(customViewHolder, i);


    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(CustomViewHolder viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        int lastPosition=position-1;
        if (position > lastPosition)
        {
            final Animation fade = AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
            viewToAnimate.t.setAnimation(fade);
            lastPosition = position;
        }
    }






    @Override
    public int getItemCount()
    {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
       protected TextView tvType ;
//       protected TextView tvDescription ;
        protected TextView tvSender ;
       protected ImageView imPhoto;
        protected Toolbar  tool;
         protected CardView t;


        public CustomViewHolder(View v) {
            super(v);
            this.tvType= (TextView) v.findViewById(R.id.memType);
//            this.tvDescription = (TextView) v.findViewById(R.id.memDescription);
            this.imPhoto = (ImageView)v.findViewById(R.id.reportImage);
            this.tvSender=(TextView) v.findViewById(R.id.memSender);
            this.tool=(Toolbar)v.findViewById(R.id.toolbarreportItem);
            this.t=(CardView)v.findViewById(R.id.toAnimate);





        }
    }
}