package com.ezyro.uba_inventory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezyro.uba_inventory.data.ProductContract.SupplierEntry;

public class SupplierCursorAdapter extends CursorAdapter {
    public SupplierCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_supplier,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView PsupplierName = (TextView) view.findViewById(R.id.supp_name_value);
        TextView PsupplierPhone = (TextView) view.findViewById(R.id.supp_phone_value);
        TextView PsupplierEmail = (TextView) view.findViewById(R.id.supp_email_value);
        TextView PsupplierCity = (TextView) view.findViewById(R.id.supp_city_value);

        // Find the columns of category attributes that we're interested in
        int SupplierNameColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int SupplierPhoneColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
        int SupplierEmailColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
        int SupplierCityColumnIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_PRODUCT_SUPPLIER_CITY);

        // Read the product attributes from the Cursor for the current product
        String supplierName = cursor.getString(SupplierNameColumnIndex);
        String supplierPhone = cursor.getString(SupplierPhoneColumnIndex);
        String supplierEmail = cursor.getString(SupplierEmailColumnIndex);
        String supplierCity = cursor.getString(SupplierCityColumnIndex);


        // Update the TextViews with the attributes for the current category
        PsupplierName.setText(supplierName);
        PsupplierPhone.setText(supplierPhone);
        PsupplierEmail.setText(supplierEmail);
        PsupplierCity.setText(supplierCity);

    }
}
