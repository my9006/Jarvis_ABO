package com.jarvis_abo;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import com.jarvis_abo.entities.DaoMaster;
import com.jarvis_abo.entities.DaoSession;
import com.jarvis_abo.entities.User;
import com.jarvis_abo.entities.UserDao;
import com.jarvis_abo.jarvis_service.JA_service;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by Mxo on 16-May-16.
 */
public class JA_application extends Application {

    private DaoSession daoSession;

    private static JA_application instance;





    public static JA_application getInstance() {
//        if(instance==null) {
//            instance = new JA_application();
//        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Iconify.with(new FontAwesomeModule());
        startAndCheck();
    }

    public DaoSession getDaoSession() {

        if (daoSession == null) {
            DaoMaster.DevOpenHelper openHelper = new DaoMaster.DevOpenHelper(this, "JA_BASE", null) {
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                }
            };
            SQLiteDatabase database = openHelper.getWritableDatabase();

            DaoMaster daoMaster = new DaoMaster(database);

            daoSession = daoMaster.newSession();

        }
        return daoSession;
    }

    public User loggedUser() {

        UserDao userDao = JA_application.getInstance().getDaoSession().getUserDao();
        SharedPreferences sharedPreferences = getSharedPreferences("com.jarvis_abo_preferences", MODE_PRIVATE);
        if (sharedPreferences.contains("last_successful_login") && userDao.queryBuilder().where(UserDao.Properties.Username.eq(sharedPreferences.getString("last_successful_login", "BAD"))).list().size() > 0) {

            return userDao.queryBuilder().where(UserDao.Properties.Username.eq(sharedPreferences.getString("last_successful_login", "BAD"))).unique();
        }
        return null;
    }
/////////////////////////////////

    private Boolean isBound;
    private JA_service jaService;
    private ServiceConnection connection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        isBound = true;

        JA_service.LocalBinder binder = (JA_service.LocalBinder) service;
        jaService = binder.getJA_service();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        isBound = false;

    }
};


    public JA_service getJaService() {
        return jaService;
    }

    private void startAndCheck() {

        Intent intent = new Intent(this, JA_service.class);

        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }


}
