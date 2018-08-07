package com.example.android.bookinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract;
import com.example.android.bookinventory.data.BookContract.BookEntry;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by Eli on 06-Aug-18.
 * https://github.com/codepath/android_guides/wiki/Populating-a-ListView-with-a-CursorAdapter
 */

public class BookCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the product name for the current book can be set on the name bookNameTextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final Context currentContext = context;

        // Find fields to populate in inflated template
        TextView bookNameTextView = view.findViewById(R.id.product_name);
        TextView bookPriceTextView = view.findViewById(R.id.product_price);
        TextView bookQuantityTextView = view.findViewById(R.id.product_quantity);
        Button saleButton = view.findViewById(R.id.sale_button);

        // Now extract properties from cursor

        // Step a) Figure out the index of each column
        int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);

        // step b) Use that index to extract the String or Int value of the book
        // at the current row the cursor is on.
        final int currentID = cursor.getInt(idColumnIndex);
        String currentName = cursor.getString(nameColumnIndex);
        String currentPrice = cursor.getString(priceColumnIndex);
        final int currentQuantity = cursor.getInt(quantityColumnIndex);

        //https://stackoverflow.com/questions/9187586/storing-currency-in-sqlite-android-database
        BigDecimal price = new BigDecimal(currentPrice);
        String currentFormattedPrice = NumberFormat.getCurrencyInstance().format(price);

        // Populate fields with extracted properties
        bookNameTextView.setText(currentName);
        bookPriceTextView.setText(currentFormattedPrice);
        bookQuantityTextView.setText(Integer.toString(currentQuantity));

        //Set Click Listener and perform Sale button functionality on Click
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = currentQuantity;

                if (quantity > 0) {
                    quantity--;
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, currentID);
                    int rowsAffected = currentContext.getContentResolver().update(currentBookUri, values, null, null);
                    if (rowsAffected == 1) {
                        Toast.makeText(currentContext, currentContext.getString(R.string.quantity_update_succeessful),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(currentContext, currentContext.getString(R.string.quantity_update_unsuccessful),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(currentContext, currentContext.getString(R.string.no_books_message),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}