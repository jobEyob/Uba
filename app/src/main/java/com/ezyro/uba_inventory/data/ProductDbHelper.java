package com.ezyro.uba_inventory.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//import com.ezyro.uba_inventory.data.ProductContract.ProductEntry;
//import com.ezyro.uba_inventory.data.ProductContract.CategoryEntry;
import com.ezyro.uba_inventory.data.ProductContract.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Database helper for Products app. It manages database creation and version management.
 */
public class ProductDbHelper extends SQLiteOpenHelper {

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductDbHelper.class.getSimpleName();

    /** Declaration of database name and initialisation of its version number */
    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ProductDbHelper}.
     *
     * @param context of the app
     */
    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method is called when the database is created for the first time.
     */// Create a String that contains the SQL statement to create the product table
    String SQL_CREATE_PRODUCTS_TABLE =  "CREATE TABLE " + ProductEntry.TABLE_NAME + " ("
            + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
//            + ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, "
//            + ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL,"
            + ProductEntry.COLUMN_PRODUCT_CATEGORY_NAME + " TEXT, "
            + ProductEntry.COLUMN_PRODUCT_UNIT_PRICE + " INTEGER NOT NULL DEFAULT 1, "
            + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + ProductEntry.COLUMN_PRODUCT_IMAGE_PATH + " TEXT, "
            + ProductEntry.COLUMN_STATUS + " INTEGER, "
            + ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID + " TEXT NOT NULL DEFAULT 0); ";


     String SQL_CREATE_CATEGORY_TABLE =  "CREATE TABLE " + CategoryEntry.CATEGORY_TABLE_NAME + " ("
            + CategoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CategoryEntry.COLUMN_CATEGORY_NAME + " TEXT NOT NULL , "
            + CategoryEntry.COLUMN_CATEGORY_ACTIVE + " INTEGER NOT NULL DEFAULT 1); ";

     String SQL_CREATE_SUPPLIER_TABLE =  "CREATE TABLE " + SupplierEntry.SUPPLIER_TABLE_NAME + " ("
            + SupplierEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SupplierEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL , "
            + SupplierEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + " TEXT, "
            + SupplierEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " STRINGE NOT NULL , "
            + SupplierEntry.COLUMN_PRODUCT_SUPPLIER_CITY + " TEXT ); ";

String SQL_CREATE_STATISTICS_TABLE =  "CREATE TABLE " + StatisticEntry.STATISTICS_TABLE_NAME + " ("
            + StatisticEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + StatisticEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL , "
            + StatisticEntry.COLUMN_PRODUCT_UNIT_PRICE + " INTEGER, "
            + StatisticEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL , "
            + StatisticEntry.COLUMN_PRODUCT_SELL_DATE + " STRINGE ); ";






    @Override
    public void onCreate(SQLiteDatabase db) {


        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_SUPPLIER_TABLE);
        db.execSQL(SQL_CREATE_STATISTICS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedProduct() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + ProductEntry.TABLE_NAME + " WHERE " + ProductEntry.COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * This method taking two arguments
     * first one is the id of the product for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateProductStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_STATUS, status);
        db.update(ProductEntry.TABLE_NAME, contentValues, ProductEntry._ID + "=" + id, null);
       // db.close();
        return true;
    }

    public boolean statistic (String Pname, int price, int quantity, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(StatisticEntry.COLUMN_PRODUCT_NAME, Pname);
        contentValues.put(StatisticEntry.COLUMN_PRODUCT_UNIT_PRICE, price);
        contentValues.put(StatisticEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        contentValues.put(StatisticEntry.COLUMN_PRODUCT_SELL_DATE, date);



        db.insert(StatisticEntry.STATISTICS_TABLE_NAME, null, contentValues);
        //db.close();
        return true;
    }

    // Get User Details
    public ArrayList<HashMap<String, String>> GetStatistics(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT * FROM "+ StatisticEntry.STATISTICS_TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("pname",cursor.getString(cursor.getColumnIndex(StatisticEntry.COLUMN_PRODUCT_NAME)));
            user.put("unit_price",cursor.getString(cursor.getColumnIndex(StatisticEntry.COLUMN_PRODUCT_UNIT_PRICE)));
            user.put("quantity",cursor.getString(cursor.getColumnIndex(StatisticEntry.COLUMN_PRODUCT_QUANTITY)));
            user.put("date",cursor.getString(cursor.getColumnIndex(StatisticEntry.COLUMN_PRODUCT_SELL_DATE)));
            userList.add(user);
        }
        return  userList;
    }
}
