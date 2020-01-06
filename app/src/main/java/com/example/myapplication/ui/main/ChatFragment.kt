package com.example.myapplication.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.*
import com.example.myapplication.Retrofit.MyService
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_contact.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

/**
 * The chat fragment.
 */
class ChatFragment : Fragment() {

    var id = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onStart() {
        super.onStart()
        refreshChatroom()
    }

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
        fun newInstance(id: String): ChatFragment {
            var newCF = ChatFragment()
            newCF.id = id
            return newCF
        }
    }

    fun refreshChatroom(){
        val addrList = getChatroomList()
        // onClick 설정
        val mAdapter = ChatCorridorAdapter(requireContext(), addrList) { prof ->
            Toast.makeText(context,"clicked: "+prof.chatroom_name, Toast.LENGTH_LONG).show()
            // view가 click되었을 때 실행할 것들
        }

        cRecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(requireContext())
        cRecyclerView.layoutManager = lm
        cRecyclerView.setHasFixedSize(true)
    }

    fun getChatroomList(): ArrayList<ChatroomData?>? {
        // Fetch contact list from server
        var idList: ArrayList<String>? = ArrayList()
        var resList: ArrayList<ChatroomData?> = ArrayList()

        thread(start = true){
            var retrofit = Retrofit.Builder()
                .baseUrl(Config.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            idList = myService.getChatrooms(id).execute().body()
            Log.d("ContactFragment","idList is $idList")
        }.join()

        for (elem_id in idList!!) {
            thread(start = true){
                Log.d("ContactFragment","get contactdata - id is $elem_id")
                var retrofit = Retrofit.Builder()
                    .baseUrl(Config.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myService: MyService = retrofit.create(MyService::class.java)

                var body = myService.getChatroom(elem_id).execute().body()
                Log.d("ContactFragment","get contactdata - body is $body")
                resList.add(Util.getChatroomDataFramJson(body!!))
            }.join()
        }

        return resList
    }

}