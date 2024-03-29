package com.ezyro.uba_inventory.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ezyro.uba_inventory.data.ProductContract.ProductEntry;
import com.ezyro.uba_inventory.data.ProductContract.CategoryEntry;
import com.ezyro.uba_inventory.data.ProductContract.SupplierEntry;

 import java.sql.Blob;

/**
 * {@link ContentProvider} for Inventory Manager app.
 */

public class ProductProvider extends ContentProvider {
    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the products table */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single product in the products table */
    private static final int PRODUCT_ID = 101;

    /** URI matcher code for the content category URI for the category's table */
    private static final int CATEGORY = 200;

    /** URI matcher code for the content category URI for a single category in the category's table */
    private static final int CATEGORY_ID = 201;

     /** URI matcher code for the content supplier URI for the supplier's table */
    private static final int SUPPLIER = 300;

    /** URI matcher code for the content supplier URI for a single supplier in the category's table */
    private static final int SUPPLIER_ID = 301;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);

        /** for category table */
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRO_CATEGORY, CATEGORY);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRO_CATEGORY + "/#", CATEGORY_ID);

         /** for supplier table */
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRO_SUPPLIER, SUPPLIER);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRO_SUPPLIER + "/#", SUPPLIER_ID);


    }

    /** Database helper object */
    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI.
     * Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database.
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query.
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code.
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CATEGORY:
                cursor = database.query(CategoryEntry.CATEGORY_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case CATEGORY_ID:
                selection = CategoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(CategoryEntry.CATEGORY_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SUPPLIER:
                cursor = database.query(SupplierEntry.SUPPLIER_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(SupplierEntry.SUPPLIER_TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the cursor
        return cursor;
    }

//    private Cursor queryjoin(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        String table = ProductEntry.TABLE_NAME;
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(table);
//        sb.append(" INNER JOIN ");
//        sb.append(SupplierEntry.SUPPLIER_TABLE_NAME);
//        sb.append(" ON (");
//        sb.append(ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID);
//        sb.append(" = ");
//        sb.append(SupplierEntry._ID);
//        sb.append(")");
//        table = sb.toString();
//
//        queryBuilder.setTables(table);
//
//
//        Cursor cursor = queryBuilder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null,
//                sortOrder);
//        cursor.setNotificationUri(getContext().getContentResolver(), uri);
//        return cursor;
//    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            case CATEGORY:
                return insertCategory(uri, contentValues);
            case SUPPLIER:
                return insertSupplier(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values.
     * Return the new content URI for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the unit price is valid
        Integer unitPrice = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_UNIT_PRICE);
        if (unitPrice == null) {
            throw new IllegalArgumentException("Product requires a valid unit price");
        }

        // If the quantity is provided, check that it's greater than or equal to 0 unit
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

//        // Check that the name is not null
//        String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
//        if (supplierName == null) {
//            throw new IllegalArgumentException("Product requires a supplier's name");
//        }
//
//        // Check that the name is not null
//        String supplierEmail = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
//        if (supplierEmail == null) {
//            throw new IllegalArgumentException("Product requires a supplier's email address");
//        }

        // Gets writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ProductEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

 /**
     * Insert a category into the database with the given content values.
     * Return the new content category URI for that specific row in the database.
     */
    private Uri insertCategory(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(CategoryEntry.COLUMN_CATEGORY_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Category requires a name");
        }

        // Check that the states is valid
        Integer states = values.getAsInteger(CategoryEntry.COLUMN_CATEGORY_ACTIVE);
        if (states == null) {
            throw new IllegalArgumentException("Category requires a states");
        }

        // Gets writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(CategoryEntry.CATEGORY_TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    private Uri insertSupplier(Uri uri, ContentValues values) {
        // Check that the name is not null
        String Supplier_name = values.getAsString(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (Supplier_name == null) {
            throw new IllegalArgumentException("Supplier requires a name");
        }
        // Check that the email is not null
        String Supplier_email = values.getAsString(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
//        if (Supplier_email == null) {
//            throw new IllegalArgumentException("Supplier requires a email");
//        }
        // Check that the phone is not null
        String Supplier_phone = values.getAsString(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
        if (Supplier_phone == null) {
            throw new IllegalArgumentException("Supplier requires a phone");
        }
        // Check that the city is not null
        String Supplier_city = values.getAsString(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_CITY);
//        if (Supplier_city == null) {
//            throw new IllegalArgumentException("Supplier requires a city");
//        }


        // Gets writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(SupplierEntry.SUPPLIER_TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the product content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update products in the database with the given content values.
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the {@link ProductEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_UNIT_PRICE} key is present,
        // check that the unit price value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_UNIT_PRICE)) {
            // Check that the unit price is valid
            Integer unitPrice = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_UNIT_PRICE);
            if (unitPrice == null) {
                throw new IllegalArgumentException("Product requires a valid unit price");
            }
        }

        // If the {@link ProductEntry#COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            // Check that the quantity is valid
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }

//        // If the {@link ProductEntry#COLUMN_PRODUCT_SUPPLIER_NAME} key is present,
//        // check that the supplier's name value is not null.
//        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
//            String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
//            if (supplierName == null) {
//                throw new IllegalArgumentException("Product requires a supplier's name");
//            }
//        }
//
//        // If the {@link ProductEntry#COLUMN_PRODUCT_SUPPLIER_EMAIL} key is present,
//        // check that the supplier's email value is not null.
//        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL)) {
//            String supplierEmail = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
//            if (supplierEmail == null) {
//                throw new IllegalArgumentException("Product requires a supplier's email address");
//            }
//        }

        // If there are no values to update, then don't try to update the database.
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected.
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated,
        // then notify all listeners that the data at the given URI has changed.
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database.
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted.
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args.
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted,
        // then notify all listeners that the data at the given URI has changed.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted.
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return CategoryEntry.CONTENT_CATEGORY_LIST_TYPE;
            case CATEGORY_ID:
                return CategoryEntry.CONTENT_CATEGORY_ITEM_TYPE;
            case SUPPLIER:
                return SupplierEntry.CONTENT_SUPPLIER_LIST_TYPE;
            case SUPPLIER_ID:
                return SupplierEntry.CONTENT_SUPPLIER_ITEM_TYPE;

            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
