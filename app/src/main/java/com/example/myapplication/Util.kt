package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.myapplication.Retrofit.MyService
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.concurrent.thread


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

    /*
     * This functions converts Bitmap picture to a string which can be
     * JSONified.
     * */
    fun getStringFromBitmap(bitmapPicture: Bitmap): String? {
        val COMPRESSION_QUALITY = 100
        val encodedImage: String
        val byteArrayBitmapStream = ByteArrayOutputStream()
        bitmapPicture.compress(
            Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
            byteArrayBitmapStream
        )
        val b: ByteArray = byteArrayBitmapStream.toByteArray()
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
        return encodedImage
    }

    /*
    * This Function converts the String back to Bitmap
    * */
    fun getBitmapFromString(stringPicture: String): Bitmap? {
        val decodedString =
            Base64.decode(stringPicture, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    // Returns ContactData created from simple json string containing _id, name, profile_photo, status, country_cde
    fun getContactDataFromSimpleJson(json: String): ContactData {
        val jsonObject = JSONObject(json)
        val contactData = ContactData()

        Log.d("Util",json.toString())

        contactData._id = jsonObject.getString("_id")
        contactData.name = jsonObject.getString("name")
        contactData.country_code = jsonObject.getInt("country_code")
        contactData.profile_photo = jsonObject.getString("profile_photo")
        contactData.status = jsonObject.getString("status")
        if (contactData.status.equals("null"))
            contactData.status = ""

        return contactData
    }


    // Returns ChatroomData created from json string
    fun getChatroomDataFramJson(json: String): ChatroomData {
        val jsonObject = JSONObject(json)
        val chatroomData = ChatroomData()

        Log.d("Util",json)

        chatroomData.chatroom_name = jsonObject.getString("chatroom_name")
        chatroomData.last_chat = jsonObject.getString("last_chat")
        chatroomData.chatroom_image = jsonObject.getString("chatroom_image")
        val ja = JSONArray(jsonObject.getString("people"))

        Log.d("Util","ja = "+ja)

        for (i in 0 until ja.length())
            chatroomData.people.add(ja.getString(i))

        Log.d("Util","people = "+chatroomData.people)

        return chatroomData
    }

    // Returns ChatData created from json string
    fun getChatDataFramJson(json: String): ChatData {
        Log.d("Util",json)
        return getChatDataFramJson(JSONObject(json))
    }

    fun getChatDataFramJson(jsonObject: JSONObject): ChatData {
        val chatData = ChatData()

        chatData.date_time = jsonObject.getString("date_time")
        chatData.id = jsonObject.getString("id")
        chatData.script = jsonObject.getString("script")

        return chatData
    }

    fun <T> ArrayList<T>.shuffle(): ArrayList<T> {
        val rng = Random()

        for (index in 0..this.size - 1) {
            val randomIndex = rng.nextInt(index)

            // Swap with the random position
            val temp = this[index]
            this[index] = this[randomIndex]
            this[randomIndex] = temp
        }

        return this
    }

    fun getNameFromId(id: String): String {
        var name = ""
        thread(start = true){
            var retrofit = Retrofit.Builder()
                .baseUrl(Config.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            name = getContactDataFromSimpleJson(myService.getContactSimple(id).execute().body()!!).name
        }.join()
        return name
    }

    fun getProfileImageFromId(id: String): Bitmap? {
        var p_image: Bitmap? = null
        thread(start = true){
            var retrofit = Retrofit.Builder()
                .baseUrl(Config.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            p_image = getBitmapFromString(getContactDataFromSimpleJson(myService.getContactSimple(id).execute().body()!!).profile_photo)
        }.join()
        return p_image
    }

    fun getDefPhoto() : Bitmap {
        return getBitmapFromString("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")!!
    }
}
