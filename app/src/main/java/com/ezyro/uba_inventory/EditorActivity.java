package com.ezyro.uba_inventory;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ezyro.uba_inventory.data.ProductContract.ProductEntry;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Allows the user to create a new product or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    /** Tag for the log messages */
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /** Constants definition */
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static int PICK_IMAGE_REQUEST = 0;
    private static int PICK_IMAGE_Gallery_REQUEST = 1;
    private static int PICK_IMAGE_Camera_REQUEST = 2;
    private static final String IMAGE_DIRECTORY = "/ubaInventor";


    /** Global variables declaration */
    private Uri selectedImageUri = null;
    private Uri mCurrentProductUri;

    private String productName;
    private String supplierName;
    private String supplierEmail;
    private int productQuantity;

    private ImageView mPictureImageView;
    private EditText mNameEditText;
    private Spinner mProductCategory;
    private EditText mProductItem;
    private EditText mSupplierNameEditText;
    private EditText mSupplierEmailAddressEditText;
    private EditText mUnitPriceEditText;
    private TextView mProductQuantityTextView;

    private boolean mProductHasChanged = false;
    private boolean imageProductHasChanged = false;
    private boolean productDataAreValid = true;

    /**
     * OnTouchListener that listens for any touch on a View.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mPictureImageView = (ImageView) findViewById(R.id.image_product);
        mNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mProductCategory =(Spinner) findViewById(R.id.Spinner_product_category);
        mProductItem =(EditText) findViewById(R.id.edit_product_item);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_product_supplier_name);
        mSupplierEmailAddressEditText = (EditText) findViewById(R.id.edit_product_supplier_email);
        mUnitPriceEditText = (EditText) findViewById(R.id.edit_product_unit_price);
        mProductQuantityTextView = (TextView) findViewById(R.id.product_quantity_text_view);

        ImageView mOrderNowImageView = (ImageView) findViewById(R.id.order_now);
        ImageView mDecrementStock = (ImageView) findViewById(R.id.decrement_stock);
        ImageView mIncrementStock = (ImageView) findViewById(R.id.increment_stock);

        // Examine the intent that was used to launch this activity.
        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
            productQuantity = 0;
            mProductQuantityTextView.setText(String.valueOf(productQuantity));
            mOrderNowImageView.setVisibility(View.GONE);
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getSupportLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Setup OnTouchListeners on all the input fields,
        // to notify the user if he tries to leave the editor without saving.
        mPictureImageView.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailAddressEditText.setOnTouchListener(mTouchListener);
        mUnitPriceEditText.setOnTouchListener(mTouchListener);
        mDecrementStock.setOnTouchListener(mTouchListener);
        mIncrementStock.setOnTouchListener(mTouchListener);

        mPictureImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                showPictureDialog();
            }
        });

        mDecrementStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantity > 0) {
                    productQuantity--;
                    displayProductStockLevel(productQuantity);
                }
            }
        });

        mIncrementStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productQuantity++;
                displayProductStockLevel(productQuantity);
            }
        });

        mOrderNowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {supplierEmail});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject_prefix) + productName);
                intent.putExtra(Intent.EXTRA_TEXT, createOrderEmailMessage());
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });



        // Loading spinner data from database
        loadSpinnerData();




    }

    /**
     * This method creates a summary for the order email.
     */
    public String createOrderEmailMessage() {

        String orderEmailMessage = getString(R.string.order_email_starting_greeting) + supplierName;
        orderEmailMessage += getString(R.string.order_email_comma);
        orderEmailMessage += getString(R.string.order_email_jump_line);
        orderEmailMessage += getString(R.string.order_email_current_stock_statement_part_one) + productName;
        orderEmailMessage += getString(R.string.order_email_current_stock_statement_part_two) + productQuantity;
        orderEmailMessage += getString(R.string.order_email_point);
        orderEmailMessage += getString(R.string.order_email_jump_line);
        orderEmailMessage += getString(R.string.order_email_request_end_greetings);
        return orderEmailMessage;
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, PICK_IMAGE_Gallery_REQUEST);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, PICK_IMAGE_Camera_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == PICK_IMAGE_Gallery_REQUEST) {

            if (resultCode == RESULT_OK) {

                try {
                    selectedImageUri = data.getData();
                    imageProductHasChanged = true;
                    final InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    mPictureImageView.setImageBitmap(selectedImage);
                    String path = saveImage(selectedImage);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(EditorActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }

        } else if (requestCode == PICK_IMAGE_Camera_REQUEST) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            mPictureImageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);

        }else {
               Toast.makeText(EditorActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
            }
    }

    public String saveImage( Bitmap myBitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File ImageDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!ImageDirectory.exists()) {
            if(ImageDirectory.mkdirs()){
                Log.d(LOG_TAG, "Directory successfully created");
            }else {
                Log.e(LOG_TAG, "Directory not created");
            }
        }

        try {
              long file_name = Calendar.getInstance().getTimeInMillis(); //set file name Know time and m/s
            File f = new File(ImageDirectory, file_name + ".jpg");
                 f.createNewFile();

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            //selectedImageUri = Uri.fromFile(f);
            MediaScannerConnection.scanFile(this, new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);

            fo.close();
            Log.d(LOG_TAG, "File Saved::--->" + f.getAbsolutePath());

            convertFileToContentUri(getApplicationContext(),f); // call Function

            return "";
        } catch (IOException e1) {
            e1.printStackTrace();
            Toast.makeText(this, "Failed: " + e1.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * Converts a file to a content uri, by inserting it into the media store.
     * Requires this permission: <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     */
    private Uri convertFileToContentUri(Context context, File file) throws Exception {

        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri
        ContentResolver cr = context.getContentResolver();
        String imagePath = file.getAbsolutePath();
        String imageName = null;
        String imageDescription = null;
        String uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
        selectedImageUri = Uri.parse(uriString);
        imageProductHasChanged = true;
        return null;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        // Check which request we are responding to.
//        if (requestCode == PICK_IMAGE_REQUEST) {
//            // Make sure the request was successful.
//            if (resultCode == RESULT_OK) {
//                try {
//                    selectedImageUri = data.getData();
//                    imageProductHasChanged = true;
//                    final InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
//                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                    //Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                    mPictureImageView.setImageBitmap(selectedImage);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(EditorActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
//                }
//                // The request was not successful.
//            } else {
//                Toast.makeText(EditorActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
//            }
//        }
//    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayProductStockLevel(int currentStock) {
        TextView quantityTextView = (TextView) findViewById(R.id.product_quantity_text_view);
        quantityTextView.setText(String.valueOf(currentStock));
    }

    /**
     * Get user input from editor and save new product into the database or update an existing one.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void saveProduct() {

        String nameString = mNameEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailAddressEditText.getText().toString().trim();
        String unitPriceString = mUnitPriceEditText.getText().toString().trim();

        // Check if this is supposed to be a new product,
        // and check if all the fields in the editor are blank.
        if (mCurrentProductUri == null && !imageProductHasChanged && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierEmailString) && TextUtils.isEmpty(unitPriceString) && productQuantity ==0) {
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues();

        // Check that the product has a name.
        if (!nameString.isEmpty()) {
            values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        }
        else {
            Toast.makeText(this, getString(R.string.editor_product_requires_name),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        }

        // Check that the name of the supplier of the product is provided.
        if (!supplierNameString.isEmpty()) {
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
        }
        else {
            Toast.makeText(this, getString(R.string.editor_product_requires_supplier_name),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        }

        // Check that the email of the supplier of the product has been provided.
        if (!supplierNameString.isEmpty() && supplierEmailString.matches(EMAIL_PATTERN)) {
            values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL, supplierEmailString);
        }
        else if (supplierEmailString.isEmpty()) {
            Toast.makeText(this, getString(R.string.editor_product_requires_supplier_email),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        } else if (!supplierEmailString.matches(EMAIL_PATTERN)) {
            Toast.makeText(this, getString(R.string.editor_product_invalid_supplier_email),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        }

        // Check that the product has a valid unit price.
        if (!unitPriceString.isEmpty() && Integer.parseInt(unitPriceString) > 0) {
            values.put(ProductEntry.COLUMN_PRODUCT_UNIT_PRICE, unitPriceString);
        }
        else if (unitPriceString.length() == 0){
            Toast.makeText(this, getString(R.string.editor_product_requires_price),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        } else if (Integer.parseInt(unitPriceString) == 0) {
            Toast.makeText(this, getString(R.string.editor_product_requires_positive_price),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        }

        // Check that the product has a valid quantity.
        if (productQuantity > 0) {
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        }
        else {
            Toast.makeText(this, getString(R.string.editor_product_requires_positive_stock_level),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        }

        // Check that the product has an image
        if (imageProductHasChanged) {
            String imageString = selectedImageUri.toString();
            values.put(ProductEntry.COLUMN_PRODUCT_IMAGE_PATH, imageString);
        }
        else if (((BitmapDrawable)mPictureImageView.getDrawable()).getBitmap() == ((BitmapDrawable)getDrawable(R.drawable.img_generic)).getBitmap()){
            Toast.makeText(this, getString(R.string.editor_product_requires_image),
                    Toast.LENGTH_SHORT).show();
            productDataAreValid = false;
            return;
        }

        // If all the checks on the input values are passed,
        // then the data for the product are valid.
        productDataAreValid = true;

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                if (productDataAreValid) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_IMAGE_PATH,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_UNIT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,     // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE_PATH);
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL);
            int unitPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_UNIT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

            String productImageURI = cursor.getString(imageColumnIndex);
            productName = cursor.getString(nameColumnIndex);
            supplierName = cursor.getString(supplierNameColumnIndex);
            supplierEmail = cursor.getString(supplierEmailColumnIndex);
            int productUnitPrice = cursor.getInt(unitPriceColumnIndex);
            productQuantity = cursor.getInt(quantityColumnIndex);

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }

            if (productImageURI != null) {
                try {
                    final InputStream imageStream = getContentResolver().openInputStream(Uri.parse(productImageURI));
                    Bitmap productImage = BitmapFactory.decodeStream(imageStream);
                    mPictureImageView.setImageBitmap(productImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                mPictureImageView.setImageDrawable(getDrawable(R.drawable.img_generic));
            }

            mNameEditText.setText(productName);
            mSupplierNameEditText.setText(supplierName);
            mSupplierEmailAddressEditText.setText(supplierEmail);
            mUnitPriceEditText.setText(Integer.toString(productUnitPrice));
            mProductQuantityTextView.setText(Integer.toString(productQuantity));

            // Update the color of the displayed product's quantity according to its stock level
            if (productQuantity == 0) {
                mProductQuantityTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorEmptyStock));
            }
            else {
                mProductQuantityTextView.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorPositiveStock));
            }
            //cursor.close();
        }
      //  cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mPictureImageView.setImageBitmap(null);
        mNameEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierEmailAddressEditText.setText("");
        mUnitPriceEditText.setText("");
        mProductQuantityTextView.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /***************************
     * show dialog for select product Images camera or from gallery
     * ******************* */
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                requestStorageAndCameraPermission();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    /**
     * Getting all category
     * returns list of category
     * */
    public List<String> getAllLabels(){
        List<String> labels = new ArrayList<String>();

        Cursor cursor = managedQuery(ProductEntry.CONTENT_URI,null,null,null,"name");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
       // cursor.close();

        // returning lables
        return labels;
    }


    /**
     * Function to load the spinner data from SQLite database
     * */
    private void loadSpinnerData() {
        // database handler

        // Spinner Drop down elements
        List<String> lables = this.getAllLabels();

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        mProductCategory.setPrompt("Select Product Category");
        mProductCategory.setAdapter(dataAdapter);
        // Spinner click listener
        mProductCategory.setOnItemSelectedListener(this);
    }
    /**
     * 
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    /**
//     * Requesting camera permission
//     * This uses single permission model from dexter
//     * Once the permission granted, opens the camera
//     * On permanent denial opens settings dialog
//     */
//    private void requestCameraPermission() {
//        Dexter.withActivity(this)
//                .withPermission(
//                        Manifest.permission.CAMERA)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        // permission is granted
//                        takePhotoFromCamera();
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//                        // check for permanent denial of permission
//                        if (response.isPermanentlyDenied()) {
//                            showSettingsDialog();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//                }).check();
//    }

    private void requestStorageAndCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                           // Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                            takePhotoFromCamera();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }
    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}

