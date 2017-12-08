package esprit.tn.cinecasa.utils;

import android.app.Application;
import android.support.multidex.MultiDex;

/**
 * Created by Yessine on 11/26/2017.
 */

public class MyApplication extends Application {

    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}