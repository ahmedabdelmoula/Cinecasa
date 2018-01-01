package esprit.tn.cinecasa.utils;

import android.content.*;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import esprit.tn.cinecasa.RegisterActivity;

/**
 * Created by Yessine on 12/31/2017.
 */

public class ConnectionChangeReceiver extends BroadcastReceiver {

    Context con;


    @Override
    public void onReceive(android.content.Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (activeNetInfo != null && esprit.tn.cinecasa.utils.Context.SS != null) {
//            esprit.tn.cinecasa.utils.Context.SS.start();
            Toast.makeText(context, "Active Network Type : " + activeNetInfo.getTypeName(), Toast.LENGTH_LONG).show();
        }
        if (mobNetInfo != null && esprit.tn.cinecasa.utils.Context.SS != null) {
//            esprit.tn.cinecasa.utils.Context.SS.start();
            Toast.makeText(context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_LONG).show();
        }
    }
}