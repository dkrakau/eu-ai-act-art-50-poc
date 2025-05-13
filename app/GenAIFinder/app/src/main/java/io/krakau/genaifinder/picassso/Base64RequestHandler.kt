package io.krakau.genaifinder.picassso

import android.graphics.BitmapFactory
import android.util.Base64
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import java.io.IOException

/**
 * Custom Picasso RequestHandler for Base64 encoded images
 */
class Base64RequestHandler : RequestHandler() {

    companion object {
        const val SCHEME_BASE64 = "base64"
    }

    override fun canHandleRequest(data: Request): Boolean {
        return SCHEME_BASE64 == data.uri.scheme
    }

    @Throws(IOException::class)
    override fun load(request: Request, networkPolicy: Int): Result {
        var path = request.uri.toString()

        // Remove the scheme prefix (base64://)
        path = path.substring(SCHEME_BASE64.length + 3)

        // Handle data URI format (data:image/png;base64,...)
        if (path.startsWith("data:")) {
            val commaIndex = path.indexOf(',')
            if (commaIndex != -1) {
                path = path.substring(commaIndex + 1)
            }
        }

        return try {
            // Decode the Base64 string to a byte array
            val decodedBytes = Base64.decode(path, Base64.DEFAULT)

            // Convert the byte array to a Bitmap
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ?: throw IOException("Failed to decode Base64 string to Bitmap")

            Result(bitmap, Picasso.LoadedFrom.MEMORY)
        } catch (e: IllegalArgumentException) {
            throw IOException("Invalid Base64 string", e)
        }
    }
}