package com.ezyro.uba_inventory;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ezyro.uba_inventory.data.ProductContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Tag for the log messages */
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    /** Unique identifier for the loader */
    private static final int PRODUCT_LOADER = 0;

    /** Instance of CursorAdapter */
    ProductCursorAdapter mCursorAdapter;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "com.ezyro.uba_inventory.datasaved";

    //Broadcast receiver to know the sync status
    private final BroadcastReceiver broadcastReceiver = new NetworkStateChecker();

   // private boolean mProductHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//                // Your code
//                finish();
//            }
//        });
        DrawerUtil.getDrawer(this,toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action !!", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data.
        ListView productListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of product data in the Cursor.
        // There is not product data yet (until the loader finishes), so we pass in null for the Cursor.
        mCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        // Kick-off the loader. for gat product list
        getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);

        //the broadcast receiver to update sync status
//        /broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                //loading the product again
//              //  getSupportLoaderManager().initLoader(PRODUCT_LOADER, null, this);
//                restartActivity(CatalogActivity.this);
//            }
//        };
//
//        //registering the broadcast receiver to update sync status
//        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));


    }


    public static void restartActivity(AppCompatActivity activity){
        if (Build.VERSION.SDK_INT >= 16) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver,filter);

        //restartActivity(CatalogActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * Helper method to insert hardcoded product data into the database.
     * Left as an example for users > always deletable.
     */
    private void insertProduct() {

        Uri imageforDummyProductURI = Uri.parse("android.resource://com.ezyro.uba_inventory/drawable/img_audi_a3");


        // Create a ContentValues objecURI
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "ford");
        values.put(ProductEntry.COLUMN_PRODUCT_UNIT_PRICE, 25000);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 1);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE_PATH, String.valueOf(imageforDummyProductURI));
        values.put(ProductEntry.COLUMN_PRODUCT_CATEGORY_NAME,"Car");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID,1);
    //        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Audi");
    //        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, "orders@audi.com");

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            // Respond to a click on the "Insert dummy data" menu option.
            case R.id.action_insert_dummy_data:

                insertProduct();

                return true;
            // Respond to a click on the "Delete all entries" menu option.
            case R.id.action_delete_all_entries:
                Cursor c = getContentResolver().query(ProductEntry.CONTENT_URI, null, null, null, null);
                // If the products table is not empty, try to delete all its entries.
                if(c.getCount() > 0) {
                    // Pop up confirmation dialog for deletion.
                    showDeleteConfirmationDialog();
                }
                // Otherwise, show a toast saying that the products table is empty.
                else { Toast.makeText(this, getString(R.string.catalog_products_table_is_empty),
                        Toast.LENGTH_SHORT).show(); }
                c.close();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_UNIT_PRICE,
                ProductEntry.COLUMN_STATUS,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};
        return new CursorLoader(this,   // Parent's activity context
                ProductEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
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

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners,
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_products_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.catalog_delete_all_products_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.catalog_delete_all_products_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

//    /**
//     * This method is called when the back button is pressed.
//     */
//    @Override
//    public void onBackPressed() {
//        if (!mProductHasChanged) {
//            super.onBackPressed();
//            return;
//        }
//
//    }
}
