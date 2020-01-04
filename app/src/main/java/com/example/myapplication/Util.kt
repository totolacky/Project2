package com.example.myapplication

import android.graphics.Bitmap

object Util {
    /* Bitmap을 정사각형으로 자르는 메소드 */
    fun squareBitmap(oBitmap: Bitmap?): Bitmap? {
        if (oBitmap == null) return null
        val width = oBitmap.width
        val height = oBitmap.height
        var resx = 0
        var resy = 0
        var reswidth = width
        var resheight = height
        if (width > height) {
            reswidth = height
            resx = (width - height) / 2
        } else if (width < height) {
            resheight = width
            resy = (height - width) / 2
        }
        return Bitmap.createBitmap(oBitmap, resx, resy, reswidth, resheight)
    }

    /* Bitmap을 resizing하는 메소드 */
    fun resizingBitmap(oBitmap: Bitmap?, size: Int): Bitmap? {
        if (oBitmap == null) return null
        val width = oBitmap.width
        val height = oBitmap.height
        val rBitmap: Bitmap
        rBitmap =
            Bitmap.createScaledBitmap(oBitmap, size, size, true)
        return rBitmap
    }

    /* Bitmap을 xCount X yCount로 자르는 메소드 */
    fun splitBitmap(
        bitmap: Bitmap,
        xCount: Int,
        yCount: Int
    ): Array<Array<Bitmap?>> { // Allocate a two dimensional array to hold the individual images.
        val bitmaps =
            Array(xCount) { arrayOfNulls<Bitmap>(yCount) }
        val width: Int
        val height: Int
        // Divide the original bitmap width by the desired vertical column count
        width = bitmap.width / xCount
        // Divide the original bitmap height by the desired horizontal row count
        height = bitmap.height / yCount
        // Loop the array and create bitmaps for each coordinate
        for (x in 0 until xCount) {
            for (y in 0 until yCount) { // Create the sliced bitmap
                bitmaps[x][y] =
                    Bitmap.createBitmap(bitmap, x * width, y * height, width, height)
            }
        }
        // Return the array
        return bitmaps
    }
}
