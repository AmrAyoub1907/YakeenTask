package com.amrayoub.yakeentask;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Amr Ayoub on 7/3/2017.
 */

public class VolleySingleton {
        private static VolleySingleton mVolleySingleton;
        private RequestQueue mRequestQueue;
        private static Context mContext;

        private VolleySingleton(Context context) {
            mContext = context;
            mRequestQueue = getRequestQueue();
        }
        public static synchronized VolleySingleton getInstance(Context context) {
            if (mVolleySingleton == null) {
                mVolleySingleton = new VolleySingleton(context);
            }
            return mVolleySingleton;
        }

        public RequestQueue getRequestQueue() {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
            }
            return mRequestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req, String tag) {
            req.setTag(tag);
            getRequestQueue().add(req);
        }
        public void cancelPendingRequests(Object tag) {
            if (mRequestQueue != null) {
                mRequestQueue.cancelAll(tag);
            }
        }
    }
