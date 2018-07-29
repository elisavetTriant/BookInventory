package com.example.android.bookinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.bookinventory.data.BookContract.BookEntry;
import com.example.android.bookinventory.data.InventoryDbHelper;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertBook();
                displayDatabaseInfo();
            }
        });

        mDbHelper = new InventoryDbHelper(this);
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the inventory database. For debugging purposes only.
     */
    private void displayDatabaseInfo() {

        // TODO: Read all rows from the database and display it in a text view
        
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        TextView displayView = (TextView) findViewById(R.id.database_info);


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

        Cursor cursor = db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);

        try {

            // Create a header in the Text View that looks like this:
            //
            // The books table contains <number of rows in Cursor> books.
            String countMessage = "The " + BookEntry.TABLE_NAME + " table contains " + cursor.getCount() + " rows.\n\n";

            displayView.setText(countMessage);

            if (cursor.getCount()>0) {
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
        // TODO: Insert a single book into the database

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Against the MACHINE - Lee Siegel");
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, "4.99");
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 3);
        values.put(BookEntry.COLUMN_PRODUCT_GENRE, 13);
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Public S.A.");
        //Note: Comment out the following line and the insert will fail as the field is marked as NOT NULL
        values.put(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "+30 2108181333");

        // Insert the new row
        long newRowId = db.insert(BookEntry.TABLE_NAME, null, values);

        //Show the result
        if (newRowId == -1){
            Toast.makeText(this, "Error inserting row in the database.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to delete all records from the database. For debugging purposes only.
     * https://developer.android.com/training/data-storage/sqlite#DeleteDbRow
     *
     * Setting whereClause and whereArgs to null eliminates the where clause from the delete query
     * and essentially deletes all book records from the database.
     * So the raw SQL query would be delete * from books;
     */
    private void deleteAll(){
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int deletedRows = db.delete(BookEntry.TABLE_NAME, null, null);

        Toast.makeText(this, "Deleted " + deletedRows + " rows from the database!", Toast.LENGTH_SHORT).show();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    // Typically, it is optimal to close the database in the onDestroy() of the calling Activity.
    // https://developer.android.com/training/data-storage/sqlite#PersistingDbConnection
    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}
