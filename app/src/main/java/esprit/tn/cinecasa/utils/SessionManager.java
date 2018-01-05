package esprit.tn.cinecasa.utils;

/**
 * Created by ahmed on 11-Nov-17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import esprit.tn.cinecasa.datastorage.SQLiteHandler;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "UserLogin";
    private static final String PREF_UID = "uId";
    private static final String PREF_MDP = "mdp";
    private static final String PREF_MAIL = "mail";
    private static boolean b;

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String FIRST_TIME = "isFirstTime";

    public SessionManager(Context context) {
        pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,String uId, String mdp, String mail) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(PREF_UID, uId);
        editor.putString(PREF_MDP, mdp);
        editor.putString(PREF_MAIL, mail);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }
    public void setIntro(boolean isFirstTime) {

        editor.putBoolean(FIRST_TIME, isFirstTime);
        // commit changes
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getPrefMail(){
        return pref.getString(PREF_MAIL, "none");
    }

    public String getPrefMdp(){
        return pref.getString(PREF_MDP, "none");
    }
    public String getUID(){ return pref.getString(PREF_UID,"no uid");}
    public boolean isFirstTime(){return pref.getBoolean(FIRST_TIME, false);}

}
