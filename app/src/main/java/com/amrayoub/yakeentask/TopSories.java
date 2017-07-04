package com.amrayoub.yakeentask;

/**
 * Created by Amr Ayoub on 7/3/2017.
 */

public class TopSories {
    private String mTitle;
    private String mPublished_date;
    private String mUrl ;
    TopSories(){}
    TopSories(String mTitle, String mPublished_date, String mUrl) {
        this.mTitle = mTitle;
        this.mPublished_date = mPublished_date;
        this.mUrl = mUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmPublished_date() {
        return mPublished_date;
    }

    public String getmUrl() {
        return mUrl;
    }
}
