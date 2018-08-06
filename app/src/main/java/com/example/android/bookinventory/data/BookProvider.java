package com.example.android.bookinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.bookinventory.InventoryApp;
import com.example.android.bookinventory.R;


/**
 * Created by Eli on 02-Aug-18.
 * Updated on 06-Aug-18 so there is notification to all listeners that the data has changed.
 */

public class BookProvider extends ContentProvider {

     /**
     * URI matcher code for the content URI for the books table (all records)
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }


    /**
     * InventoryDbHelper global var. It will be used to gain access to the inventory database,
     * so we can perform the CRUD functions.
     */
    private InventoryDbHelper mDbHelper;


    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize an InventoryDbHelper object to gain access to the inventory database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.bookinventory/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.cannot_query_unknown_url) + " " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.insert_not_supported_for_uri) + " " + uri);
        }
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     * Returns the number of rows that were successfully updated.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.update_not_supported_for_uri) + " " + uri);
        }
    }


    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // For tracking the number of rows that were deleted
        int rowsDeleted;

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                //Return number of deleted rows
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.delete_not_supported_for_uri) + " "  + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(InventoryApp.getRes().getString(R.string.unknown_uri) + " " + uri + " " + InventoryApp.getRes().getString(R.string.with_match) + " " + match);
        }
    }


    /**
     * Insert a book into the database with the given content values.
     * Return the new content URI for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        /* Do Sanity Checks before you write to the database
         * Consult Schema of the table on InventoryDbHelper class
         */

        // Check that the product name value is not null.
        String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_product_name));
        }


        // check that the product price value is not null.
        String price = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_price));
        }


        // Check that the quantity is greater than or equal to 0
        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_valid_quantity));
        }


        // check that the genre value is valid. If genre == null then it defaults to 0 (INTEGER NOT NULL DEFAULT 0).
        //So we check if genre value is present and the genre is valid in this case
        Integer genre = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_GENRE);
        if (genre != null && !BookContract.BookEntry.isValidGenre(genre)) {
            throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_valid_genre));
        }


        // check that the product supplier name value is not null.
        String supplierName = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_supplier_name));
        }

        // check that the product supplier phone value is not null.
        String supplierPhone = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_supplier_telephone));
        }

        // If we passed all the exceptions we are ready to write to the database - data is correct

        // Gets the data repository in write mode
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new row
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Return null.
        if (id == -1) {
            return null;
        }

        // Notify all listeners that the data has changed for the given content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Returns the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        /* Do Sanity Checks before you write to the database
        *  Consult Schema of the table on InventoryDbHelper class
         */

        // If the {@link BookContract.BookEntry.COLUMN_PRODUCT_NAME} key is present,
        // check that the product name value is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_product_name));
            }
        }

        // If the {@link BookContract.BookEntry.COLUMN_PRODUCT_PRICE} key is present,
        // check that the product price value is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_PRICE)) {
            String price = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_price));
            }
        }


        // If the {@link BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY)) {
            // Check that the quantity is greater than or equal to 0
            Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_valid_quantity));
            }
        }

        // If the {@link BookContract.BookEntry.COLUMN_PRODUCT_GENRE} key is present,
        // check that the genre value is valid. If genre == null then it defaults to 0 (INTEGER NOT NULL DEFAULT 0).
        //So we check if genre value is present and the genre is valid in this case
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_GENRE)) {
            Integer genre = values.getAsInteger(BookContract.BookEntry.COLUMN_PRODUCT_GENRE);
            if (genre != null && !BookContract.BookEntry.isValidGenre(genre)) {
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_valid_genre));
            }
        }

        // If the {@link BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME} key is present,
        // check that the product supplier name value is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_supplier_name));
            }
        }

        // If the {@link BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE} key is present,
        // check that the product supplier phone value is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException(InventoryApp.getRes().getString(R.string.book_requires_supplier_telephone));
            }
        }

        // If we passed all the exceptions we are ready to write to the database - data is correct

        // Get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated  = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }

}
