package com.ezyro.uba_inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.ezyro.uba_inventory.data.ProductContract.SupplierEntry;

import es.dmoral.toasty.Toasty;

public class SupplierActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private String supplierName;
    private String supplierPhone;
    private String supplierEmail;
    private String supplierCity;

    /** Tag for the log messages */
    public static final String LOG_TAG = SupplierActivity.class.getSimpleName();

    /** Unique identifier for the loader */
    private static final int SUPPLIER_LOADER = 0;

    /** Instance of CursorAdapter */
    SupplierCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Supplier List");
        DrawerUtil.getDrawer(this,toolbar); // call DDrawerUtil class that implement navigationdrawer library

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDiloge();
            }
        });

        // Find the ListView which will be populated with the product data.
        ListView supplierListView = (ListView) findViewById(R.id.supplier_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.supplier_empty_view);
        supplierListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of product data in the Cursor.
        // There is not product data yet (until the loader finishes), so we pass in null for the Cursor.
        mCursorAdapter = new SupplierCursorAdapter(this, null);
        supplierListView.setAdapter(mCursorAdapter);

        // Kick-off the loader.
        getSupportLoaderManager().initLoader(SUPPLIER_LOADER, null, this);
        // getLoaderManager().initLoader(CATEGORY_LOADER, null, (android.app.LoaderManager.LoaderCallbacks<Object>) this);
    }

    public void showAddDiloge(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.supplier_prompts,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(promptsView);
        final EditText PsupplierName =(EditText) promptsView.findViewById(R.id.edit_product_supplier_name);
        final EditText PsupplierPhone =(EditText) promptsView.findViewById(R.id.product_supplier_phone);
        final EditText PsupplierEmail =(EditText) promptsView.findViewById(R.id.edit_product_supplier_email);
        final EditText PsupplierCity =(EditText) promptsView.findViewById(R.id.product_supplier_city);

        builder.setCancelable(false);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        supplierName  = PsupplierName.getText().toString().trim();
                        supplierPhone = PsupplierPhone.getText().toString().trim();
                        supplierEmail = PsupplierEmail.getText().toString().trim();
                        supplierCity  = PsupplierCity.getText().toString().trim();


                        insertSupplier();

                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }) ;

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * Helper method to insert hardcoded product data into the database.
     * Left as an example for users > always deletable.
     */
    private void insertSupplier() {

        // Create a ContentValues objecURI
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);
        values.put(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_CITY, supplierCity);
        values.put(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmail);

        Uri newUri = getContentResolver().insert(SupplierEntry.CONTENT_SUPPLIER_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toasty.error(this, getString(R.string.supplier_insert_supplier_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toasty.success(this, getString(R.string.supplier_insert_supplier_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                SupplierEntry._ID,
                SupplierEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                SupplierEntry.COLUMN_PRODUCT_SUPPLIER_PHONE,
                SupplierEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                SupplierEntry.COLUMN_PRODUCT_SUPPLIER_CITY
        };

        CursorLoader loader = new CursorLoader(
                this,
                SupplierEntry.CONTENT_SUPPLIER_URI,
                projection,
                null,
                null,
                null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
     mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
     mCursorAdapter.swapCursor(null);
    }


}
