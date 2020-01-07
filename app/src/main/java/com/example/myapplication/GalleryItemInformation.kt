package com.example.myapplication


import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.main.myGalleryHolder

class GalleryItemInformation : AppCompatActivity() {

    private val TAG = "GalleryItemInformation"
    private var thisItem = GalleryItem(null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        var pos = intent.getIntExtra("POS", 0)
        thisItem = myGalleryHolder.getDataById(pos)

        var userPhoto : ImageView = findViewById(R.id.thumbnail)
        userPhoto.setImageBitmap(thisItem.image)

        var userName: TextView = findViewById(R.id.user_name)
        userName.text = thisItem.userInfo!!.name
        var userStatus: TextView = findViewById(R.id.user_status)
        userStatus.text = thisItem.userInfo!!.status
        var userProfile: ImageView = findViewById(R.id.user_image)
        userProfile.setImageBitmap(Util.getBitmapFromString(thisItem.userInfo!!.profile_photo))
        var userFlag: ImageView = findViewById(R.id.user_flag)
        val flagId = "flag_"+thisItem.userInfo!!.country_code
        val resId = applicationContext.resources.getIdentifier(flagId,"drawable",applicationContext.packageName)
        userFlag.setImageBitmap(BitmapFactory.decodeResource(applicationContext.resources,resId))

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
