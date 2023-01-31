package com.dds.core;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dds.App;
import com.dds.LauncherActivity;
import com.dds.core.base.BaseActivity;
import com.dds.core.socket.IUserState;
import com.dds.core.socket.SocketManager;
import com.dds.core.voip.Consts;
import com.dds.core.voip.VoipReceiver;
import com.dds.webrtc.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements IUserState {
    private static final String TAG = "MainActivity";
    boolean isFromCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_user, R.id.navigation_room, R.id.navigation_setting)
                .build();
        // Set ActionBar to follow the linkage
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //
        //Set Nav to follow the linkage
        NavigationUI.setupWithNavController(navView, navController);
        // Set login status callback
        SocketManager.getInstance().addUserStateCallback(this);
        isFromCall = getIntent().getBooleanExtra("isFromCall", false);
        Log.d(TAG, "onCreate isFromCall = " + isFromCall);
        if (isFromCall) { //No permission, call to apply for permission will go here
            initCall();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart isFromCall = " + isFromCall);
    }

    @Override
    public void userLogin() {

    }

    private void initCall() {
        //In the foreground, send a broadcast to call up the permission judgment pop-up window
        Intent viop = new Intent();
        Intent intent = getIntent();
        viop.putExtra("room", intent.getStringExtra("room"));
        viop.putExtra("audioOnly", intent.getBooleanExtra("audioOnly", false));
        viop.putExtra("inviteId", intent.getStringExtra("inviteId"));
        viop.putExtra("inviteUserName", intent.getStringExtra("inviteUserName"));
//        viop.putExtra("msgId", intent.getLongExtra("msgId", 0));
        viop.putExtra("userList", intent.getStringExtra("userList"));
        viop.setAction(Consts.ACTION_VOIP_RECEIVER);
        viop.setComponent(new ComponentName(App.getInstance().getPackageName(), VoipReceiver.class.getName()));
        sendBroadcast(viop);
    }

    @Override
    public void userLogout() {
        if (!this.isFinishing()) {
            Intent intent = new Intent(this, LauncherActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            super.onBackPressed();
        }

    }
}
