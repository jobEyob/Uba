package com.ezyro.uba_inventory;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.ezyro.uba_inventory.data.ProductDbHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class StatisticsActivity extends AppCompatActivity {

    private ProductDbHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerUtil.getDrawer(this,toolbar);

        db = new ProductDbHelper(this);

        ArrayList<HashMap<String, String>> userList = db.GetStatistics();
        ListView lv = (ListView) findViewById(R.id.pro_list);
        ListAdapter adapter = new SimpleAdapter(StatisticsActivity.this, userList,
                R.layout.list_statistics,new String[]{"pname","unit_price","quantity","date"},
                new int[]{R.id.sproduct_name, R.id.sproduct_unit_price, R.id.sproduct_qunitity,R.id.sproduct_date});
        lv.setAdapter(adapter);
    }
}
