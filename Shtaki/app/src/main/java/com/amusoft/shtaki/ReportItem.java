package com.amusoft.shtaki;

/**
 * Created by irving on 8/28/15.
 */
public class ReportItem {
    String  Title;
    String Description;
    String Location;
    String Institution;
    String Picture;
    String FireKey;
    String Votes;

    public ReportItem(String title,String description,String location,
                      String institition,String picture,String fireKey,String votes){
        this.Title=title;
        this.Description=description;
        this.Location=location;
        this.Institution=institition;
        this.Picture=picture;
        this.FireKey=fireKey;
        this.Votes=votes;



    }

    public String getVotes() {
        return Votes;
    }

    public void setVotes(String votes) {
        Votes = votes;
    }

    public String getDescription() {
        return Description;
    }

    public String getFireKey() {
        return FireKey;
    }

    public String getInstitution() {
        return Institution;
    }

    public String getLocation() {
        return Location;
    }

    public String getPicture() {
        return Picture;
    }

    public String getTitle() {
        return Title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setFireKey(String fireKey) {
        FireKey = fireKey;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setInstitution(String institution) {
        Institution = institution;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }
}
