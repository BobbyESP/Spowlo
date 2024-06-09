package com.bobbyesp.utilities.mediastore.advanced

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//Thanks to https://proandroiddev.com/kotlin-flow-contentresolver-and-mediastore-the-key-to-effortless-media-access-in-android-fad56db16fdd
/**
 * An advanced of [ContentResolver.query]
 * @see ContentResolver.query
 * @param order valid column to use for orderBy.
 */
suspend fun ContentResolver.advancedQuery(
    uri: Uri,
    projection: Array<String>? = null,
    selection: String,
    args: Array<String>? = null,
    order: String = MediaStore.MediaColumns._ID,
    ascending: Boolean = true,
    offset: Int = 0,
    limit: Int = Int.MAX_VALUE
): Cursor? {
    return withContext(Dispatchers.Default) {
        // use only above android 10
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // compose the args
            val args2 = Bundle().apply {
                // Limit & Offset
                putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
                putInt(ContentResolver.QUERY_ARG_OFFSET, offset)

                // order
                putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(order))
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    if (ascending) ContentResolver.QUERY_SORT_DIRECTION_ASCENDING else ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
                // Selection and groupBy
                if (args != null) putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, args)
                // add selection.
                // TODO: Consider adding group by.
                // currently I experienced errors in android 10 for groupBy and arg groupBy is supported
                // above android 10.
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            }
            query(uri, projection, args2, null)
        }
        // below android 0
        else {
            //language=SQL
            val order2 =
                order + (if (ascending) " ASC" else " DESC") + " LIMIT $limit OFFSET $offset"
            // compose the selection.
            query(uri, projection, selection, args, order2)
        }
    }
}