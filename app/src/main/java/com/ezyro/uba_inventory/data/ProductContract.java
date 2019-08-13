package com.ezyro.uba_inventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API Contract for the Inventory Manager app.
 */
public final class ProductContract {
    
    // To prevent someone from accidentally instantiating the contract class, an empty constructor is given
    private ProductContract() {}
    
    public static final String CONTENT_AUTHORITY = "com.ezyro.uba_inventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_PRO_CATEGORY = "category";
    public static final String PATH_PRO_SUPPLIER = "supplier";
public static final String WEB_SERVER_URL = "http://192.168.43.130:8080";


    /**
     * Inner class that defines constant values for the products database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /** Name of database table for products */
        public final static String TABLE_NAME = "products";

        /**
         * Unique ID number for the product (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * Unit price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_UNIT_PRICE = "unit_price";

        /**
         * Available quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        /**
         * Image for the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_IMAGE_PATH = "image";

//        /**
//         * Name of the supplier of the product.
//         *
//         * Type: TEXT
//         */
//        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";

        /**
         * Name of the category of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_CATEGORY_NAME = "category_name";

        /**
         * Id of the supplier table of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_ID = "supplier_id";

        /**
         * id of the company .
         *
         * Type: INTEGER
         */
        public final static String COLUMN_COMPANY_ID = "company_id";

        /**
         * status of the product .
         *
         * Type: INTEGER
         */
        public static final String COLUMN_STATUS = "status";

//        /**
//         * Email of the supplier of the product.
//         *
//         * Type: TEXT
//         */
//        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supplier_email";

        /*
         * this is the url to our webservice
         * */
        public static final String  WEB_URL_PRODUCT = WEB_SERVER_URL+"/"+TABLE_NAME;

    }

    /**
     * Inner class that defines constant values for the category database table.
     * Each entry in the table represents a single category.
     */
    public static final class CategoryEntry implements BaseColumns {

        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_CATEGORY_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRO_CATEGORY);

        /**
         * The MIME type of the {@link #CONTENT_CATEGORY_URI} for a list of products.
         */
        public static final String CONTENT_CATEGORY_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRO_CATEGORY;

        /**
         * The MIME type of the {@link #CONTENT_CATEGORY_URI} for a single product.
         */
        public static final String CONTENT_CATEGORY_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRO_CATEGORY;

        /** Name of database table for products */
        public final static String CATEGORY_TABLE_NAME = "category";

        /**
         * Unique ID number for the category (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the category.
         *
         * Type: TEXT
         */
        public final static String COLUMN_CATEGORY_NAME ="name";

        /**
         * state of the category.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_CATEGORY_ACTIVE = "active";

    }

    /**
     * Inner class that defines constant values for the supplier database table.
     * Each entry in the table represents a single supplier.
     */
    public static final class SupplierEntry implements BaseColumns {

        /** The content URI to access the supplier data in the provider */
        public static final Uri CONTENT_SUPPLIER_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRO_SUPPLIER);

        /**
         * The MIME type of the {@link #CONTENT_SUPPLIER_URI} for a list of suppliers.
         */
        public static final String CONTENT_SUPPLIER_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRO_SUPPLIER;

        /**
         * The MIME type of the {@link #CONTENT_SUPPLIER_URI} for a single supplier.
         */
        public static final String CONTENT_SUPPLIER_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRO_SUPPLIER;

        /** Name of database table for supplier */
        public final static String SUPPLIER_TABLE_NAME = "supplier";

        /**
         * Unique ID number for the supplier (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the supplier of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";

        /**
         * Email of the supplier of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_EMAIL = "supplier_email";

        /**
         * Email of the supplier of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone";

        /**
         * city of the supplier of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_SUPPLIER_CITY = "supplier_city";



    }

    /**
     * Inner class that defines constant values for the supplier database table.
     * Each entry in the table represents a single supplier.
     */
    public static final class StatisticEntry implements BaseColumns {


        /** Name of database table for statistic */
        public final static String STATISTICS_TABLE_NAME = "statistic";

        /**
         * Unique ID number for the statistic (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the statistic of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "statistic_name";

        /**
         * Unit price of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_UNIT_PRICE = "unit_price";

        /**
         * Available quantity of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";


        /**
         * city of the statistic of the product.
         *
         * Type: INTEGER
         */
        public final static String COLUMN_PRODUCT_SELL_DATE = "sell_date";

    }
}