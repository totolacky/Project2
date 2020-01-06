package com.example.myapplication.ui.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.*
import com.example.myapplication.*
import com.example.myapplication.Config.serverUrl
import com.example.myapplication.Retrofit.MyService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GalleryHolder{
    var gallery_holder: ArrayList<GalleryItem> = ArrayList()

    fun getDataList() : ArrayList<GalleryItem> {
        return gallery_holder
    }

    fun setDataList(setlist : ArrayList<GalleryItem>) {
        gallery_holder = setlist
        // 여기에 셔플할 수 있는 걸 추가해야하나...??
    }

    fun getDataById(position: Int) : GalleryItem {
        return gallery_holder[position]
    }
}

var myGalleryHolder = GalleryHolder()
var myGalleryList: ArrayList<GalleryItem> = ArrayList()

lateinit var myId: String


class GalleryFragment : Fragment(), GalleryRecyclerAdapter.OnListItemSelectedInterface {

    lateinit var GalleryRecyclerView : RecyclerView

    lateinit var rootView : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // main에서 id받아오기
        // myId = ~~~????????

        init()

        rootView = inflater.inflate(R.layout.fragment_gallery, container, false)

        // 이미지는 recycler view로 구현
        GalleryRecyclerView = rootView.findViewById(R.id.recyclerView)as RecyclerView
        GalleryRecyclerView.adapter = GalleryRecyclerAdapter(requireContext(), this, myGalleryList)

        // 업로드 버튼 - 클릭 시 사진 추가 가능
        var uploadButton: FloatingActionButton = rootView.findViewById(R.id.uploadbutton)
        uploadButton.setOnClickListener {
            Toast.makeText(getContext(), "select the image for introducing yourself", Toast.LENGTH_SHORT).show()
            loadImage()
        }


        return rootView
    }

    /*
    // recycler view 가 업데이트가 안 되면 걍 이걸로 싹 밀어버려
    fun refresh(){
        activity?.also{
            var viewAdapter = GalleryRecyclerAdapter(requireContext(), this, myGalleryList)
            GalleryRecyclerView = it.findViewById<RecyclerView>(R.id.recyclerView).apply {
                setHasFixedSize(true)
                adapter = viewAdapter
            }
            activity?.findViewById<LinearLayout>(R.id.fragment_gallery)?.invalidate()
        }
    }*/

    // recycler view 안에 있는 사진 선택 시 액티비티 시작 (사진, 사진 user, user와의 chat)
    override fun onItemSelected(view: View, position: Int){
        Toast.makeText(getContext(), "Clicked Image ${position+1}", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, GalleryItemInformation::class.java)
        intent.putExtra("POS", position)
        startActivity(intent)
    }

    var retrofit = Retrofit.Builder()
        .baseUrl(serverUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var myService: MyService = retrofit.create(MyService::class.java)


    var initNum: Int = 5

    // 내 갤러리에는 남들의 사진들이 뜸
    fun init(){
        // 서버한테 요청
        for(i in 1..initNum) {
            myService.getGallery(myId)
                .enqueue(object : Callback<Pair<String, ContactData>> {
                    override fun onFailure(
                        call: Call<Pair<String, ContactData>>, t: Throwable
                    ) {
                        Log.e("gallery", t.message)
                        Toast.makeText(getContext(), "get gallery fail", Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<Pair<String, ContactData>>, response: Response<Pair<String, ContactData>>
                    ) {
                        Log.d("gallery", "init response ok")
                        myGalleryList.add(GalleryItem(Util.getBitmapFromString(response.body()!!.first), response.body()!!.second))
                    }
                })
        }
        myGalleryHolder.setDataList(myGalleryList)
        Log.d("init gallery","other users' photos and ContactDatas")
    }



    // 갤러리에서 사진 선택시 내 DB에 photos에 추가됨 (다른 사람들의 갤러리 탭에 뜸)
    val Gallery = 0

    fun loadImage(){
        var intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Load Picture"), Gallery)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == Gallery){
            if(resultCode == RESULT_OK){

                var dataUri : Uri? = data?.data

                // 갤러리에서 사진 불러오기
                try{
                    var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(getActivity()!!.getContentResolver(), dataUri)
                    var bitmapStr: String? = Util.getStringFromBitmap(bitmap)

                    myService.uploadPhoto(myId, bitmapStr!!).enqueue(object : Callback<String> {
                        override fun onFailure(
                            call: Call<String>, t: Throwable
                        ) {
                            Log.e("upload", t.message)
                            Toast.makeText(getContext(), "upload fail", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(
                            call: Call<String>, response: Response<String>
                        ) {
                            Log.d("upload", response.body())
                            Toast.makeText(getContext(), "upload success", Toast.LENGTH_SHORT).show()
                        }
                    })
                    // 내 contact data에 올렸으니까 이거 db로 다시 올려야하는데 어떻게 보내지??
                }catch (e:Exception){
                    Toast.makeText(getContext(), "$e", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}