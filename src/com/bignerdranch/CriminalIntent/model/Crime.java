package com.bignerdranch.CriminalIntent.model;

import com.bignerdranch.CriminalIntent.model.photo.Photo;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by badgateway on 13.04.14.
 */
public class Crime {


    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";


    private Photo mPhoto;
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }


    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));

        if (json.has(JSON_PHOTO)) {
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        }
    }


    public JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();

        object.put(JSON_ID, mId.toString());
        object.put(JSON_TITLE, mTitle);
        object.put(JSON_SOLVED, mSolved);
        object.put(JSON_DATE, mDate.getTime());

        if (mPhoto != null) {
            object.put(JSON_PHOTO, mPhoto.toJSON());
        }
        return object;
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solced) {
        mSolved = solced;
    }

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo photo) {
        mPhoto = photo;
    }


    @Override
    public String toString() {
        return mTitle;

    }
}
