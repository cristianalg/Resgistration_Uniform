package ipt.ipg.app_uniform.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import androidx.annotation.RequiresApi;

import ipt.ipg.app_uniform.HelperUtils.HelperUtilities;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Uniform";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);
    }

    //requires API level 16 and above
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + "CLIENT");
        db.execSQL("DROP TABLE IF EXISTS " + "ACCOUNT");

        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 1) {

            //create tables here
            db.execSQL(createClient());
            db.execSQL(createAccount());

            insertClient(db, "Admin_First", "Admin_Last");

            insertAccount(db, "admin@admin.com", "password", 1);
        }
    }

    public String createClient() {
        return "CREATE TABLE CLIENT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FIRSTNAME TEXT COLLATE NOCASE, " +
                "LASTNAME TEXT COLLATE NOCASE, " +
                "IMAGE BLOB);";
    }

    public String createAccount() {
        return "CREATE TABLE ACCOUNT (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "EMAIL TEXT, " +
                "PASSWORD TEXT, " +
                "ACCOUNT_CLIENT INTEGER, " +
                "FOREIGN KEY (ACCOUNT_CLIENT) REFERENCES CLIENT(_id));";
    }

    public static void insertClient(SQLiteDatabase db, String firstName, String lastName) {
        ContentValues clientValues = new ContentValues();
        clientValues.put("FIRSTNAME", HelperUtilities.capitalize(firstName.toLowerCase()));
        clientValues.put("LASTNAME", HelperUtilities.capitalize(lastName.toLowerCase()));
        db.insert("CLIENT", null, clientValues);
    }

    public static void insertAccount(SQLiteDatabase db, String email, String password, int clientID) {
        ContentValues accountValues = new ContentValues();
        accountValues.put("EMAIL", email);
        accountValues.put("PASSWORD", password);
        accountValues.put("ACCOUNT_CLIENT", clientID);
        db.insert("ACCOUNT", null, accountValues);
    }


}
