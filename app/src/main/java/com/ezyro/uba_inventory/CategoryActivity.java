package com.ezyro.uba_inventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.ezyro.uba_inventory.data.ProductContract;

import es.dmoral.toasty.Toasty;

public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

   private String CnameString = " ";

    /** Tag for the log messages */
    public static final String LOG_TAG = CategoryActivity.class.getSimpleName();

    /** Unique identifier for the loader */
    private static final int CATEGORY_LOADER = 0;

    /** Instance of CursorAdapter */
    CategoryCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("List of Category");

        DrawerUtil.getDrawer(this,toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //
                showAddDiloge();
            }
        });
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find the ListView which will be populated with the product data.
        ListView categoryListView = (ListView) findViewById(R.id.category_list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.category_empty_view);
        categoryListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of product data in the Cursor.
        // There is not product data yet (until the loader finishes), so we pass in null for the Cursor.
        mCursorAdapter = new CategoryCursorAdapter(this, null);
        categoryListView.setAdapter(mCursorAdapter);

        // Kick-off the loader.
        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
       // getLoaderManager().initLoader(CATEGORY_LOADER, null, (android.app.LoaderManager.LoaderCallbacks<Object>) this);

    }

    public void showAddDiloge(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.category_prompts,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(promptsView);
        final EditText categoryInput =(EditText) promptsView.findViewById(R.id.editCategoeyDialog);
        builder.setCancelable(false);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                         CnameString = categoryInput.getText().toString().trim();

                        insertProduct();

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
     * Helper method to insert hardcoded category data into the database.
     * Left as an example for users > always deletable.
     */
    private void insertProduct() {

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();

        // Check that the product has a name.
        if (!CnameString.isEmpty()) {
            values.put(ProductContract.CategoryEntry.COLUMN_CATEGORY_NAME, CnameString);
            values.put(ProductContract.CategoryEntry.COLUMN_CATEGORY_ACTIVE, 1);
        }
        else {
            Toasty.error(this, getString(R.string.category_category_requires_name),
                    Toast.LENGTH_SHORT).show();
            return;
        }


        Uri newUri = getContentResolver().insert(ProductContract.CategoryEntry.CONTENT_CATEGORY_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toasty.error(this, getString(R.string.category_insert_category_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
           // Toast.makeText(this, getString(R.string.category_insert_category_successful),
                    //Toast.LENGTH_SHORT).show();
            Toasty.success(getBaseContext(), getString(R.string.category_insert_category_successful), Toast.LENGTH_SHORT, true).show();
            showAddDiloge();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.CategoryEntry._ID,
                ProductContract.CategoryEntry.COLUMN_CATEGORY_NAME };

        CursorLoader loader = new CursorLoader(
                this,
                ProductContract.CategoryEntry.CONTENT_CATEGORY_URI,
                projection,
                null,
                null,
                null);
        return loader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update (@Link ProductCursorAdapter) with the new cursor containing updated products data.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted.
        mCursorAdapter.swapCursor(null);
    }


}
