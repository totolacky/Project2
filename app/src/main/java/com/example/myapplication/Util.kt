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

    fun getCountryFromId(id: String): Int? {
        var res = -1
        thread(start = true){
            var retrofit = Retrofit.Builder()
                .baseUrl(Config.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            res = getContactDataFromSimpleJson(myService.getContactSimple(id).execute().body()!!).country_code
        }.join()
        return res
    }
}
