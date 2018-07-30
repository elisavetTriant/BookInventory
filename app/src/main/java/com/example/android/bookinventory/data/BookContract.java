package com.example.android.bookinventory.data;

import android.provider.BaseColumns;

/**
 * Created by Eli on 28-Jul-18.
 */

public final class BookContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty private constructor.
    private BookContract() {
    }

    public static abstract class BookEntry implements BaseColumns {

        //Name of the table
        public static final String TABLE_NAME = "books";

        //Names of the columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_GENRE = "genre";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone";

        //A list of predefined genres will show up in a spinner
        //For now we need only one for debugging purposes
        public static final int GENRE_NON_FICTION = 13;

    }
}
