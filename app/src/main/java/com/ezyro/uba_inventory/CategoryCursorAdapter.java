package com.ezyro.uba_inventory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ezyro.uba_inventory.data.ProductContract.CategoryEntry;

public class CategoryCursorAdapter extends CursorAdapter {

    public CategoryCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_category,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView categorytextView = (TextView) view.findViewById(R.id.cate_list_name);

        // Find the columns of category attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(CategoryEntry.COLUMN_CATEGORY_NAME);

        // Read the product attributes from the Cursor for the current product
        String categoryName = cursor.getString(nameColumnIndex);


        // Update the TextViews with the attributes for the current category
        categorytextView.setText(categoryName);

    }
}
