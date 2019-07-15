package com.ezyro.uba_inventory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class MainActivityOlde extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainolde);

//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
    }
    public void Addprodact(View view){

        Intent intent = new Intent(MainActivityOlde.this, EditorActivity.class);
        startActivity(intent);
    }
    public void productList(View view){

        Intent intent = new Intent(MainActivityOlde.this, CatalogActivity.class);
        startActivity(intent);
    }
     public void Categoty(View view){

//     Toasty.info(getBaseContext(), "Sorry for know this not function", Toast.LENGTH_SHORT, true).show();
         Intent intent = new Intent(MainActivityOlde.this, CategoryActivity.class);
         startActivity(intent);
    }
    public void Additem(View view){

    // Toasty.info(getBaseContext(), "Sorry for know this not function", Toast.LENGTH_SHORT, true).show();
        Intent intent = new Intent(MainActivityOlde.this, ItemActivity.class);
        startActivity(intent);
    }
    public void location(View view){

     Toasty.info(getBaseContext(), "Sorry for know this not function", Toast.LENGTH_SHORT, true).show();
    }
    public void report(View view){

     Toasty.info(getBaseContext(), "Sorry for know this not function", Toast.LENGTH_SHORT, true).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){

            case R.id.action_settings:

                //Toasty.warning(getBaseContext(), "Sorry for know this not function", Toast.LENGTH_SHORT, true).show();
                Intent sintent = new Intent(this, SettingsActivity.class);
                startActivity(sintent);

                return true;
                case R.id.action_about:

                    Toasty.warning(getBaseContext(), "Sorry for know this not function", Toast.LENGTH_SHORT, true).show();

                return true;
            // Respond to a click on the "Delete all entries" menu option.
            case R.id.action_Logout:
                Intent intent = new Intent(this, LoginActivity.class);
                SharedPreferences pref = getSharedPreferences("user_details",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                startActivity(intent);

                return true;
            case R.id.action_exit:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
