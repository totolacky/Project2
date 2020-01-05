package com.example.myapplication.ui.main


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.GalleryItem
import com.example.myapplication.R

class GalleryItemInformation : AppCompatActivity() {

    private val TAG = "GalleryItemInformation"
    private var thisItem = GalleryItem(null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        var pos = intent.getIntExtra("POS", 0)
        thisItem = myGalleryHolder.getDataById(pos)

        var imageInfo : ImageView = findViewById(R.id.thumbnail)
        imageInfo.setImageBitmap(thisItem.image)

        var userInfo : TextView = findViewById(R.id.username)
        userInfo.text = "This is "+ thisItem.userInfo!!.name + "'s image"

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
