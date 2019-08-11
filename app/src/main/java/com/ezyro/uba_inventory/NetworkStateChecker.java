package com.ezyro.uba_inventory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ezyro.uba_inventory.data.ProductContract.ProductEntry;
import com.ezyro.uba_inventory.data.ProductDbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private ProductDbHelper db;
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new ProductDbHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced product
                Cursor cursor = db.getUnsyncedProduct();
                if (cursor.moveToFirst()) {

                    int productIdColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
                    int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE_PATH);
                    int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
                    int categoryNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_CATEGORY_NAME);
                    int supplierIdColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_ID);
                    int unitPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_UNIT_PRICE);
                    int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

                    do {
                        int productID = cursor.getInt(productIdColumnIndex);
                        String productImageURI = cursor.getString(imageColumnIndex);
                        final String productName = cursor.getString(nameColumnIndex);
                        final String categoryName = cursor.getString(categoryNameColumnIndex);
                        final String supplierId = cursor.getString(supplierIdColumnIndex);
                        final String productUnitPrice = cursor.getString(unitPriceColumnIndex);
                        String productQuantity = cursor.getString(quantityColumnIndex);

                        //calling the method to save the unsynced name to MySQL
                        saveProduct(productID, productName,supplierId, productQuantity, productUnitPrice );
                    } while (cursor.moveToNext());


                }
            }


        }
    }

    /*
     * method taking two arguments
     * name that is to be saved and id of the name from SQLite
     * if the name is successfully sent
     * we will update the status as synced in SQLite
     * */
    private void saveProduct(final int id,
                             final String nameString ,
                             final String supplierIdString,
                             final String productQuantityString,
                             final String unitPriceString
                             ) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, ProductEntry.WEB_URL_PRODUCT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            int success = obj.getInt(TAG_SUCCESS);
                            Log.d("Create Response", obj.toString());

                            if (success == 1) {
                                //updating the status in sqlite
                                db.updateProductStatus(id, EditorActivity.STATUS_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(CatalogActivity.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                   Log.e("Broadcast volley Error",error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", nameString);
                //params.put("catagory_id", categoryNameString);
                params.put("suppliery_id", supplierIdString);
                params.put("price", unitPriceString);
                // params.put("pro_un_code", productQuantityString);
                //params.put("location_id", productQuantityString);
                params.put("quantity", productQuantityString);
//                params.put("item_id", productQuantityString);
//                params.put("camp_id", "2");
//                params.put("image", "images/prodact.jpg");
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }



}
