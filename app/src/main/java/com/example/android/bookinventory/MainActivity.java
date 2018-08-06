package com.example.android.bookinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.bookinventory.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertBook();
                displayDatabaseInfo();
            }
        });

    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the inventory database. For debugging purposes only.
     */
    private void displayDatabaseInfo() {

        // TODO: Read all rows from the database and display it in a text view

        TextView displayView = findViewById(R.id.database_info);


        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_GENRE,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
        };

        Cursor cursor = getContentResolver().query(BookEntry.CONTENT_URI, projection, null, null, null);

        try {

            // Create a header in the Text View that looks like this:
            //
            // The books table contains <number of rows in Cursor> books.
            String countMessage = getString(R.string.count_message, BookEntry.TABLE_NAME, cursor.getCount()) + "\n\n";

            displayView.setText(countMessage);

            if (cursor.getCount() > 0) {
                // Show columns names in a horizontal list
                //
                displayView.append(BookEntry._ID + " - " +
                        BookEntry.COLUMN_PRODUCT_NAME + " - " +
                        BookEntry.COLUMN_PRODUCT_PRICE + " - " +
                        BookEntry.COLUMN_PRODUCT_QUANTITY + " - " +
                        BookEntry.COLUMN_PRODUCT_GENRE + " - " +
                        BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " - " +
                        BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + "\n"
                );

                // Figure out the index of each column
                int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
                int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_GENRE);
                int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

                // In the while loop below, iterate through the rows of the cursor and display
                // the information from each column in this order.
                // Iterate through all the returned rows in the cursor
                while (cursor.moveToNext()) {
                    // Use that index to extract the String or Int value of the word
                    // at the current row the cursor is on.
                    int currentID = cursor.getInt(idColumnIndex);
                    String currentName = cursor.getString(nameColumnIndex);
                    String currentPrice = cursor.getString(priceColumnIndex);
                    int currentQuantity = cursor.getInt(quantityColumnIndex);
                    int currentGenre = cursor.getInt(genreColumnIndex);
                    String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                    String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                    // Display the values from each column of the current row in the cursor in the TextView
                    displayView.append(("\n" +
                            currentID + " - " +
                            currentName + " - " +
                            currentPrice + " - " +
                            currentQuantity + " - " +
                            currentGenre + " - " +
                            currentSupplierName + " - " +
                            currentSupplierPhone
                    ));
                }
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // TODO: Check the insert method of the BookProvider. Insert a single book into the database.

        try {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            //Note: Comment out the following line and the insert will fail as the field is marked as NOT NULL
            values.put(BookEntry.COLUMN_PRODUCT_NAME, getString(R.string.dummy_product_name));
            //Note: Comment out the following line and the insert will fail as the field is marked as NOT NULL
            values.put(BookEntry.COLUMN_PRODUCT_PRICE, getString(R.string.dummy_product_price));
            //Comment out the following line and the insert will fail with the message Book requires valid quantity and cannot be null.
            values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 1);
            //Comment out the following line and the insert will fail with the message Book requires valid quantity and cannot be null.
            //values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, -9);
            //Comment out the following line and the insert will NOT fail, it will save the value 0 instead (which maps to BookEntry.GENRE_UNKNOWN)
            values.put(BookEntry.COLUMN_PRODUCT_GENRE, BookEntry.GENRE_HORROR);
            //Comment out the following line and the insert will fail with the message Book requires valid genre.
            //values.put(BookEntry.COLUMN_PRODUCT_GENRE, 25);
            //Note: Comment out the following line and the insert will fail as the field is marked as NOT NULL
            values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, getString(R.string.dummy_supplier_name));
            //Note: Comment out the following line and the insert will fail as the field is marked as NOT NULL
            values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, getString(R.string.dummy_supplier_phone));


            //This will fail as the URI is not correct and the UriMatcher in the BookProvider insert method will throw an IllegalArgumentException
            //Uri resultUri = getContentResolver().insert(Uri.withAppendedPath(BookEntry.CONTENT_URI, "test"), values);

            // Insert the new row
            Uri resultUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            if (resultUri != null) {
                String id = resultUri.getLastPathSegment();
                Toast.makeText(this, getString(R.string.insert_ok_message, id), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.insert_error_message), Toast.LENGTH_SHORT).show();
            }

        } catch (IllegalArgumentException e) {
            Log.e(LOG_TAG, e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Helper method to delete all records from the database. For debugging purposes only.
     * Setting whereClause and whereArgs to null eliminates the where clause from the delete query
     * and essentially deletes all book records from the database.
     */
    private void deleteAll() {

        // TODO: Delete all rows from the books table

        int deletedRows = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);

        Toast.makeText(this, getString(R.string.deleted_message, deletedRows), Toast.LENGTH_SHORT).show();
    }

    /**
     * Helper method to update all records from the database. For debugging purposes only.
     * We are going to set all the inventory to 0 quantity
     */
    private void updateAll() {

        // TODO: Update all the rows in the database. Set all to 0 quantity. Also perform at least one sanity check.

        try {
            ContentValues values = new ContentValues();

            values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 0);
            //Uncomment the below statement to test sanity check
            //values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, -10);

            int rowsAffected = getContentResolver().update(BookEntry.CONTENT_URI, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.update_books_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.update_books_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_all) {
            deleteAll();
            displayDatabaseInfo();
            return true;
        } else if (id == R.id.action_update_all) {
            updateAll();
            displayDatabaseInfo();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

}