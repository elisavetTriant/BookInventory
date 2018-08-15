package com.example.android.bookinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract.BookEntry;


import java.math.BigDecimal;
import java.text.NumberFormat;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri mCurrentBookUri;

    private TextView productNameTextView;

    private TextView productPriceTextView;

    private TextView productQuantityTextView;

    private TextView productGenreTextView;

    private TextView productSupplierNameTextView;

    private TextView productSupplierPhoneTextView;

    private Button callSupplierButton;

    private Button minusQuantityButton;

    private Button addQuantityButton;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the current book's URI from the intent.
        // It should look like "content://com.example.android.bookinventory/books/2"
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        productNameTextView = findViewById(R.id.product_name);
        productPriceTextView = findViewById(R.id.product_price);
        productQuantityTextView = findViewById(R.id.product_quantity);
        productGenreTextView = findViewById(R.id.product_genre);
        productSupplierNameTextView = findViewById(R.id.product_supplier_name);
        productSupplierPhoneTextView = findViewById(R.id.product_supplier_phone);
        callSupplierButton = findViewById(R.id.call_supplier_button);
        minusQuantityButton = findViewById(R.id.minus_one_button);
        addQuantityButton = findViewById(R.id.add_one_button);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mCurrentBookUri != null) {
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_current_record) {
            showDeleteConfirmationDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // Since the detail screen shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_PRODUCT_GENRE,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {

            // Now extract properties from cursor

            // Step a) Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_GENRE);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

            // step b) Use that index to extract the String or Int value of the book
            // at the current row the cursor is on.
            final long currentId = cursor.getInt(idColumnIndex);
            String currentName = cursor.getString(nameColumnIndex);
            String currentPrice = cursor.getString(priceColumnIndex);
            final int currentQuantity = cursor.getInt(quantityColumnIndex);
            int currentGenre = cursor.getInt(genreColumnIndex);
            String currentSupplierName = cursor.getString(supplierNameColumnIndex);
            final String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String genre = getProductGenre(currentGenre);


            //https://stackoverflow.com/questions/9187586/storing-currency-in-sqlite-android-database
            BigDecimal price = new BigDecimal(currentPrice);
            String currentFormattedPrice = NumberFormat.getCurrencyInstance().format(price);

            //Now set the values from the cursor to the UI elements
            productNameTextView.setText(currentName);
            productPriceTextView.setText(currentFormattedPrice);
            productQuantityTextView.setText(Integer.toString(currentQuantity));
            productGenreTextView.setText(genre);
            productSupplierNameTextView.setText(currentSupplierName);
            productSupplierPhoneTextView.setText(currentSupplierPhone);

            minusQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = currentQuantity;

                    if (quantity > 0) {
                        quantity--;
                        updateBookQuantity(quantity);
                    } else {
                        Toast.makeText(DetailActivity.this, getString(R.string.no_books_message),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            addQuantityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int quantity = currentQuantity;
                    quantity++;
                    updateBookQuantity(quantity);

                }
            });

            callSupplierButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialPhoneNumber(currentSupplierPhone);
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Create new intent to go to {@link EditorActivity}
                    Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                    Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentId);
                    // Set the URI on the data field of the intent
                    intent.setData(currentBookUri);
                    // Launch the {@link EditorActivity} to edit this book
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the text fields.
        productNameTextView.setText("");
        productPriceTextView.setText("");
        productQuantityTextView.setText("");
        productGenreTextView.setText("");
        productSupplierNameTextView.setText("");
        productSupplierPhoneTextView.setText("");
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_alert_message);
        builder.setPositiveButton(R.string.delete_positive_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.delete_negative_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.deletion_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.deletion_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    /**
     * Perform the quantity of the book in the database
     */
    private void updateBookQuantity(int quantity) {

        // Only perform the update if this is an existing book.
        if (mCurrentBookUri != null) {
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);

            //Update. The mCurrentBookUri already points to the current book
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            //Show messages depending on the result
            if (rowsAffected == 1) {
                Toast.makeText(this, getString(R.string.quantity_update_successful),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.quantity_update_unsuccessful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Helper method. Takes as a parameter the genre and returns the product genre in a human readable format.
     */
    private String getProductGenre(int genre) {

        String readableGenre;

        switch (genre) {
            case BookEntry.GENRE_TRAGEDY:
                readableGenre = getString(R.string.genre_tragedy);
                break;
            case BookEntry.GENRE_TRAGIC_COMEDY:
                readableGenre = getString(R.string.genre_tragic_comedy);
                break;
            case BookEntry.GENRE_FANTASY:
                readableGenre = getString(R.string.genre_fantasy);
                break;
            case BookEntry.GENRE_MYTHOLOGY:
                readableGenre = getString(R.string.genre_mythology);
                break;
            case BookEntry.GENRE_ADVENTURE:
                readableGenre = getString(R.string.genre_adventure);
                break;
            case BookEntry.GENRE_MYSTERY:
                readableGenre = getString(R.string.genre_mystery);
                break;
            case BookEntry.GENRE_SCIENCE_FICTION:
                readableGenre = getString(R.string.genre_science_fiction);
                break;
            case BookEntry.GENRE_DRAMA:
                readableGenre = getString(R.string.genre_drama);
                break;
            case BookEntry.GENRE_ROMANCE:
                readableGenre = getString(R.string.genre_romance);
                break;
            case BookEntry.GENRE_ACTION_ADVENTURE:
                readableGenre = getString(R.string.genre_action_adventure);
                break;
            case BookEntry.GENRE_SATIRE:
                readableGenre = getString(R.string.genre_satire);
                break;
            case BookEntry.GENRE_HORROR:
                readableGenre = getString(R.string.genre_horror);
                break;
            case BookEntry.GENRE_NON_FICTION:
                readableGenre = getString(R.string.genre_non_fiction);
                break;
            case BookEntry.GENRE_FICTION:
                readableGenre = getString(R.string.genre_fiction);
                break;
            default:
                readableGenre = getString(R.string.genre_unknown);
                break;
        }

        return readableGenre;
    }

    //Source: https://developer.android.com/guide/components/intents-common#DialPhone
    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}