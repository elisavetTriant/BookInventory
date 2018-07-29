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


        //Possible genres
        public static final int GENRE_UNKNOWN = 0;
        public static final int GENRE_TRAGEDY = 1;
        public static final int GENRE_TRAGIC_COMEDY = 2;
        public static final int GENRE_FANTASY = 3;
        public static final int GENRE_MYTHOLOGY = 4;
        public static final int GENRE_ADVENTURE = 5;
        public static final int GENRE_MYSTERY = 6;
        public static final int GENRE_SCIENCE_FICTION = 7;
        public static final int GENRE_DRAMA = 8;
        public static final int GENRE_ROMANCE = 9;
        public static final int GENRE_ACTION_ADVENTURE = 10;
        public static final int GENRE_SATIRE = 11;
        public static final int GENRE_HORROR = 12;
        public static final int GENRE_NON_FICTION = 13;
        public static final int GENRE_FICTION = 14;


        /*
        * Note:
        *
        * The classic major genres of literature are:
        * Tragedy
        * Tragic comedy
        * Fantasy
        * Mythology
        * Adventure
        * Mystery
        *
        * Other major book genres include
        * Science fiction
        * Drama
        * Romance
        * Action / Adventure
        * Satire
        * Horror
        *
        * Source: https://en.wikipedia.org/wiki/List_of_writing_genres
        *
        * */

    }
}
