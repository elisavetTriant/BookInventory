package com.example.android.bookinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookinventory.data.BookContract.BookEntry;

/**
 * Created by Eli on 29-Jul-18.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the books table
        String CREATE_BOOKS_TABLE_SQL = "CREATE TABLE " + BookEntry.TABLE_NAME + "(" +
                BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                BookEntry.COLUMN_PRODUCT_PRICE + " TEXT NOT NULL, " +
                BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, " +
                BookEntry.COLUMN_PRODUCT_GENRE + " INTEGER NOT NULL DEFAULT 0, " +
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, " +
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT NOT NULL);";

        db.execSQL(CREATE_BOOKS_TABLE_SQL);

    }

    /*
     * This is called when the database needs to be upgraded.
    */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
