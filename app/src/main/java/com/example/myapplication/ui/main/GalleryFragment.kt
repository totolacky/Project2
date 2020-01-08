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
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.*
import com.example.myapplication.*
import com.example.myapplication.Config.serverUrl
import com.example.myapplication.Retrofit.MyService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread


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
        return gallery_holder.get(position) //[position]
    }
}

object Global {
    var myGalleryHolder = GalleryHolder()
    var myGalleryList: ArrayList<GalleryItem> = ArrayList()

    var getNew: Boolean = true
}

class GalleryFragment : Fragment(), GalleryRecyclerAdapter.OnListItemSelectedInterface {

    lateinit var GalleryRecyclerView : RecyclerView

    lateinit var rootView : View

    var myId = ""

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(id: String): GalleryFragment {
            var newGF = GalleryFragment()
            newGF.myId = id
            return newGF
        }
    }

    var isFabOpen: Boolean = false
    lateinit var fab_open: Animation
    lateinit var fab_close: Animation
    lateinit var main_fab: FloatingActionButton
    lateinit var uploadButton: FloatingActionButton
    lateinit var refreshButton: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if(Global.getNew){
            init()
            Global.getNew = false
        }

        rootView = inflater.inflate(R.layout.fragment_gallery, container, false)

        // 이미지는 recycler view로 구현
        GalleryRecyclerView = rootView.findViewById(R.id.recyclerView)as RecyclerView
        GalleryRecyclerView.adapter = GalleryRecyclerAdapter(requireContext(), this, Global.myGalleryList)


        // floating action button
        fab_open = AnimationUtils.loadAnimation(getContext(), R.anim.fab_open)
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close)

        main_fab = rootView.findViewById(R.id.mainfab)
        uploadButton = rootView.findViewById(R.id.uploadbutton)
        refreshButton = rootView.findViewById(R.id.refreshbutton)

        // 메인 버튼 - 애니메이션
        main_fab.setOnClickListener{
            toggleFab()
        }

        // 업로드 버튼 - 클릭 시 사진 추가 가능
        uploadButton.setOnClickListener {
            toggleFab()
            Toast.makeText(getContext(), "select the image for introducing yourself", Toast.LENGTH_SHORT).show()
            uploadImage()
        }

        // 리프레시 버튼 - 클릭 시 새 갤러리 불러오기
        refreshButton.setOnClickListener{
            toggleFab()
            Toast.makeText(getContext(), "reload gallery", Toast.LENGTH_SHORT).show()
            loadNewGallery()
        }

        return rootView
    }

    fun toggleFab(){
        if(isFabOpen){
            main_fab.setImageResource(R.drawable.ic_add);
            uploadButton.startAnimation(fab_close);
            refreshButton.startAnimation(fab_close);
            uploadButton.setClickable(false);
            refreshButton.setClickable(false);
            isFabOpen = false;
        }
        else{
            main_fab.setImageResource(R.drawable.ic_close);
            uploadButton.startAnimation(fab_open);
            refreshButton.startAnimation(fab_open);
            uploadButton.setClickable(true);
            refreshButton.setClickable(true);
            isFabOpen = true;
        }

    }


    // recycler view 가 업데이트가 안 되면 걍 이걸로 싹 밀어버려
    fun refreshView(){
        activity?.also{
            var viewAdapter = GalleryRecyclerAdapter(requireContext(), this, Global.myGalleryList)
            GalleryRecyclerView = it.findViewById<RecyclerView>(R.id.recyclerView).apply {
                setHasFixedSize(true)
                adapter = viewAdapter
            }
            activity?.findViewById<RelativeLayout>(R.id.fragment_gallery)?.invalidate()
        }
    }

    // recycler view 안에 있는 사진 선택 시 액티비티 시작 (사진, 사진 user, user와의 chat)
    override fun onItemSelected(view: View, position: Int){
        //Toast.makeText(getContext(), "Clicked Image ${position+1}", Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, GalleryItemInformation::class.java)
        intent.putExtra("POS", position)
        intent.putExtra("MYID", myId)
        startActivity(intent)
    }


    var totalUserNum: Int = 0

    // 내 갤러리에는 남들의 사진들이 뜸
    fun init(){
        Global.myGalleryList.clear()

        // 서버 요청 1 : 총 유저 수
        var tmpThread1 = thread(start = true) {
            var retrofit = Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            var response = myService.getUserNumber(myId).execute()
            if (response.body() == null) Log.d("init getUserNumber", "response body is null")
            else {
                totalUserNum = response.body()!!
                Log.d("init : totalUserNum", totalUserNum.toString())

                // 서버 요청 2 : 사진, 정보 갖고오기 (총인원수-1 번 탐색 (나빼고니까))
                for(idx in 0..totalUserNum-2){
                    var tmpThread2 = thread(start = true) {
                        var retrofit = Retrofit.Builder()
                            .baseUrl(serverUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        var myService: MyService = retrofit.create(MyService::class.java)

                        var response = myService.getGalleryItem(myId, idx).execute()
                        if (response.body() == null) Log.d("init galleryItem", "response body is null")
                        else {
                            if (response.body()!!.selectedPhoto == null) {
                                Log.d("init galleryItem","selected photo is null")
                            }
                            else {
                                Global.myGalleryList.add(
                                    GalleryItem(
                                        Util.getBitmapFromString(response.body()!!.selectedPhoto),
                                        response.body()!!.userContactData
                                    )
                                )
                            }
                        }
                    }
                    tmpThread2.join()
                }
                Global.myGalleryHolder.setDataList(Global.myGalleryList)
                Log.d("init gallery", "other users' photos and ContactDatas")
            }
        }
        tmpThread1.join()
    }


    // 새로고침하면 갤러리 다시 가져오기
    fun loadNewGallery(){
        Global.myGalleryList.clear()

        // 서버 요청 1 : 총 유저 수
        var tmpThread1 = thread(start = true) {
            var retrofit = Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            var response = myService.getUserNumber(myId).execute()
            if (response.body() == null) Log.d("nes getUserNumber", "response body is null")
            else {
                totalUserNum = response.body()!!
                Log.d("new : totalUserNum", totalUserNum.toString())

                // 서버 요청 2 : 사진, 정보 갖고오기 (총인원수-1 번 탐색 (나빼고니까))
                for(idx in 0..totalUserNum-2){
                    var tmpThread2 = thread(start = true) {
                        var retrofit = Retrofit.Builder()
                            .baseUrl(serverUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        var myService: MyService = retrofit.create(MyService::class.java)

                        var response = myService.getGalleryItem(myId, idx).execute()
                        if (response.body() == null) Log.d("new galleryItem", "response body is null")
                        else {
                            if (response.body()!!.selectedPhoto == null) {
                                Log.d("new galleryItem","selected photo is null")
                            }
                            else {
                                Global.myGalleryList.add(
                                    GalleryItem(
                                        Util.getBitmapFromString(response.body()!!.selectedPhoto),
                                        response.body()!!.userContactData
                                    )
                                )
                            }
                        }
                    }
                    tmpThread2.join()
                }
                Global.myGalleryHolder.setDataList(Global.myGalleryList)
                Log.d("new gallery", "other users' photos and ContactDatas")
            }
        }
        tmpThread1.join()

        // 셔플
        Global.myGalleryList.shuffle()
        Global.myGalleryHolder.setDataList(Global.myGalleryList)

        // 리사이클러뷰 다시 설정
        refreshView()
    }


    // 갤러리에서 사진 선택시 내 DB에 photos에 추가됨 (다른 사람들의 갤러리 탭에 뜸)
    val Gallery = 0

    fun uploadImage(){
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

                    var tmpThread = thread(start = true){
                        var retrofit = Retrofit.Builder()
                            .baseUrl(Config.serverUrl)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        var myService: MyService = retrofit.create(MyService::class.java)

                        var response = myService.uploadPhoto(myId, bitmapStr!!).execute()
                        Log.d("upload", response.body())

                    }

                    Toast.makeText(getContext(), "upload success", Toast.LENGTH_SHORT).show()
                    tmpThread.join()

                }catch (e:Exception){
                    //Toast.makeText(getContext(), "$e", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}