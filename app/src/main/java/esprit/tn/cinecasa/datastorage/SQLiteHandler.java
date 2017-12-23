package esprit.tn.cinecasa.datastorage;

/**
 * Created by ahmed on 11-Nov-17.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

import esprit.tn.cinecasa.entities.User;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "cinecasa";

    // Login table name
    private static final String TABLE_USER = "user";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_CREATED_AT = "createdat";
    private static final String KEY_SALT = "salt";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PASSWORD + " TEXT UNIQUE,"
                + KEY_UID + " TEXT,"
                + KEY_SALT + " TEXT,"
                + KEY_CREATED_AT + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(int id, String name, String email,String password, String uid, String created_at, String salt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id); // Name
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PASSWORD, password); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_SALT, salt); // Email
        values.put(KEY_CREATED_AT, created_at); // Email

        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public User getUserDetails(String UID) {
        User user=new User();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "+ KEY_UID +" =? ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{UID});
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setEmail(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setUid(cursor.getString(4));
            user.setSalt(cursor.getString(5));
            user.setCreated_at(cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER,
                KEY_ID+"="+id,
                null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}