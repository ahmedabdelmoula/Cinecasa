package esprit.tn.cinecasa.utils;

/**
 * Created by ahmed on 11-Nov-17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "UserLogin";
    private static final String PREF_UID = "uId";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String FIRST_TIME = "isFirstTime";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,String uId) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(PREF_UID, uId);
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
    public String getUID(){ return pref.getString(PREF_UID,"no uid");}
    public boolean isFirstTime(){return pref.getBoolean(FIRST_TIME, false);}

}
