package com.dds.core.util;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import androidx.collection.ArrayMap;

/**
 * Application Activity management class, used for Activity management and application exit
 *
 * @author gong
 */
public class ActivityStackManager {
    private static final String TAG = "ActivityStackManager";
    private static volatile ActivityStackManager sInstance;

    private final ArrayMap<String, Activity> mActivitySet = new ArrayMap<>();

    /**
     * Current Activity object tag
     */
    private String mCurrentTag;

    private ActivityStackManager() {
    }

    public static ActivityStackManager getInstance() {
        // Add double check lock
        if (sInstance == null) {
            synchronized (ActivityStackManager.class) {
                if (sInstance == null) {
                    sInstance = new ActivityStackManager();
                }
            }
        }
        return sInstance;
    }

    /**
     *Get the Application object
     */
    public Application getApplication() {
        return getTopActivity().getApplication();
    }

    /**
     * Get the Activity at the top of the stack
     */
    public Activity getTopActivity() {
        return mActivitySet.get(mCurrentTag);
    }

    /**
     * destroy all activities
     */
    public void finishAllActivities() {
        finishAllActivities((Class<? extends Activity>) null);
    }


    /**
     * Get the Activity at the bottom of the stack
     */
    public Activity getBottomActivity() {
        Log.d(TAG, "getBottomActivity mActivitySet.size() = " + mActivitySet.size());
        if (mActivitySet.size() > 0) {
            return mActivitySet.get(mActivitySet.keyAt(0));
        } else {
            return getTopActivity();
        }

    }
    /**
     * Destroy all activities, activities other than these Classes
     */
    @SafeVarargs
    public final void finishAllActivities(Class<? extends Activity>... classArray) {
        String[] keys = mActivitySet.keySet().toArray(new String[]{});
        for (String key : keys) {
            Activity activity = mActivitySet.get(key);
            if (activity != null && !activity.isFinishing()) {
                boolean whiteClazz = false;
                if (classArray != null) {
                    for (Class<? extends Activity> clazz : classArray) {
                        if (activity.getClass() == clazz) {
                            whiteClazz = true;
                        }
                    }
                }
                // If it is not an Activity on the whitelist, it will be destroyed
                if (!whiteClazz) {
                    activity.finish();
                    mActivitySet.remove(key);
                }
            }
        }
    }

    /**
     * Activity method callback with the same name
     */
    public void onCreated(Activity activity) {
        mCurrentTag = getObjectTag(activity);
        mActivitySet.put(getObjectTag(activity), activity);
    }

    /**
     * Activity method callback with the same name
     */
    public void onDestroyed(Activity activity) {
        mActivitySet.remove(getObjectTag(activity));
        //
        //If the current Activity is the last one
        if (getObjectTag(activity).equals(mCurrentTag)) {
            // clear the current marker
            mCurrentTag = null;
        }
        if (mActivitySet.size() != 0) {
            mCurrentTag = mActivitySet.keyAt(mActivitySet.size() - 1);
        }
    }

    /**
     *
     * Get the unique tag of an object
     */
    private static String getObjectTag(Object object) {
        // The package name where the object is located + the memory address of the object
        return object.getClass().getName() + Integer.toHexString(object.hashCode());
    }

}
