package com.dds.core.util;

import android.content.Intent;
import android.util.Log;

import com.dds.App;
import com.dds.LauncherActivity;
import com.dds.skywebrtc.CallSession;
import com.dds.skywebrtc.SkyEngineKit;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "MyUncaughtExceptionHand";

    @Override
    public void uncaughtException( Thread thread, Throwable ex) {
        SkyEngineKit gEngineKit = SkyEngineKit.Instance();
        CallSession session = gEngineKit.getCurrentSession();

        Log.d(TAG, "uncaughtException session = " + session);
        if (session != null) {
            gEngineKit.endCall();
        } else {
            gEngineKit.sendDisconnected(App.getInstance().getRoomId(), App.getInstance().getOtherUserId(),true);
        }
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        //If the exception is thrown by the background thread in the AsyncTask
        //
        //Then the actual exception can still be obtained by getCause
        Throwable cause = ex;
        while (null != cause) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        //stacktraceAsString is the obtained carsh stack information
        final String stacktraceAsString = result.toString();
        printWriter.close();
        restartApp();
    }

    private void restartApp() {
        Intent i = new Intent(App.getInstance(), LauncherActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getInstance().startActivity(i);
    }


}