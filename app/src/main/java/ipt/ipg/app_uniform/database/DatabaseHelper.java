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

    /** Tag for the log messages */
    public static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    /**
     * Constructs a new instance of {@link DatabaseHelper}.
     *
     * @param context of the app
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * This method is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        updateDatabase(db, 0, DB_VERSION);
        // Create a String that contains the SQL statement to create the product table
        String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE_PATH + " TEXT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
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

    /**
     * This is called when the database needs to be upgraded.
     */
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


    public static Cursor login(SQLiteDatabase db, String email, String password) {
        return db.query("ACCOUNT", new String[]{"_id", "EMAIL", "PASSWORD", "ACCOUNT_CLIENT"},
                "EMAIL = ? AND PASSWORD = ? ", new String[]{email, password},
                null, null, null, null);
    }

    public static void deleteAccount(SQLiteDatabase db, String clientID) {
        db.delete("CLIENT", "_id = ? ", new String[]{clientID});
        db.delete("ACCOUNT", "_id = ? ", new String[]{clientID});

    }

    public static void updateClientImage(SQLiteDatabase db, byte[] image, String id) {
        ContentValues employeeValues = new ContentValues();
        employeeValues.put("IMAGE", image);
        db.update("CLIENT", employeeValues, " _id = ? ", new String[]{id});
    }

    public static void updatePassword(SQLiteDatabase db, String password, String id) {
        ContentValues clientValues = new ContentValues();
        clientValues.put("PASSWORD", password);
        db.update("ACCOUNT", clientValues, " _id = ? ", new String[]{id});
    }

    public static Cursor selectImage(SQLiteDatabase db, int clientID) {
        return db.query("CLIENT", new String[]{"IMAGE"}, "_id = ? ",
                new String[]{Integer.toString(clientID)}, null, null,
                null, null);
    }


    public static Cursor selectClientPassword(SQLiteDatabase db, int clientID) {
        return db.query("ACCOUNT", new String[]{"PASSWORD"}, "_id = ? ",
                new String[]{Integer.toString(clientID)}, null, null,
                null, null);
    }

    public static void updateClient(SQLiteDatabase db, String firstName, String lastName, int clientID) {
        ContentValues clientValues = new ContentValues();
        clientValues.put("FIRSTNAME", HelperUtilities.capitalize(firstName.toLowerCase()));
        clientValues.put("LASTNAME", HelperUtilities.capitalize(lastName.toLowerCase()));
        db.update("CLIENT", clientValues, "_id = ?", new String[]{String.valueOf(clientID)});
    }

    public static void updateAccount(SQLiteDatabase db, String email, int clientID) {
        ContentValues accountValues = new ContentValues();
        accountValues.put("EMAIL", email);
        db.update("ACCOUNT", accountValues, " ACCOUNT_CLIENT = ?",
                new String[]{String.valueOf(clientID)});
    }

    public static Cursor selectClientID(SQLiteDatabase db, String firstName, String lastName) {
        return db.query("CLIENT", new String[]{"_id"},
                "FIRSTNAME = ? AND LASTNAME = ?",
                new String[]{firstName, lastName},
                null, null, null, null);
    }

    public static Cursor selectClientJoinAccount(SQLiteDatabase db, int clientID) {
        return db.rawQuery("SELECT FIRSTNAME, LASTNAME, EMAIL FROM CLIENT " +
                "JOIN ACCOUNT " +
                "ON CLIENT._id = ACCOUNT.ACCOUNT_CLIENT " +
                "WHERE " +
                "CLIENT._id = '" + clientID + "'", null);
    }

    public static Cursor selectClient(SQLiteDatabase db, int clientID) {
        return db.query("CLIENT", null, " _id = ? ",
                new String[]{String.valueOf(clientID)}, null, null, null, null);
    }



    public static Cursor selectAccount(SQLiteDatabase db, String email) {
        return db.query("ACCOUNT", null, " EMAIL = ? ",
                new String[]{email}, null, null, null, null);
    }

}