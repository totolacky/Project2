package com.example.myapplication.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Config
import com.example.myapplication.ContactData
import com.example.myapplication.R
import com.example.myapplication.Retrofit.MyService
import com.example.myapplication.Util
import kotlinx.android.synthetic.main.fragment_contact.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

/**
 * The address fragment.
 */
class ContactFragment : Fragment() {

    val id = "5e136121b4733f33b0697ea3"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onStart() {
        super.onStart()
        refreshContact()
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
        fun newInstance(id: String): ContactFragment {
            var newCF = ContactFragment()
            newCF.userId = id
            return newCF
        }
    }

    fun refreshContact(){
        val addrList = getContactList()
        // onClick 설정
        val mAdapter = ContactAdapter(requireContext(), addrList) { prof ->
            Toast.makeText(context,"clicked: "+prof.name,Toast.LENGTH_LONG).show()
            // view가 click되었을 때 실행할 것들
        }

        mRecyclerView.adapter = mAdapter

        val lm = LinearLayoutManager(requireContext())
        mRecyclerView.layoutManager = lm
        mRecyclerView.setHasFixedSize(true)
    }

    fun getContactList(): ArrayList<ContactData?>? {
        // Fetch contact list from server
        var idList: ArrayList<String>? = ArrayList()
        var resList: ArrayList<ContactData?> = ArrayList()
        var tmpThread: Thread

        tmpThread = thread(start = true){
            var retrofit = Retrofit.Builder()
                .baseUrl(Config.serverUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            var myService: MyService = retrofit.create(MyService::class.java)

            idList = myService.getFriends(id).execute().body()
            Log.d("ContactFragment","idList is $idList")
        }

        tmpThread.join()

        for (elem_id in idList!!) {
            tmpThread = thread(start = true){
                Log.d("ContactFragment","get contactdata - id is $elem_id")
                var retrofit = Retrofit.Builder()
                    .baseUrl(Config.serverUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                var myService: MyService = retrofit.create(MyService::class.java)

                var body = myService.getContactSimple(elem_id).execute().body()
                Log.d("ContactFragment","get contactdata - body is $body")
                resList.add(Util.getContactDataFramSimpleJson(body!!))
            }
            tmpThread.join()
        }

        return resList
    }

}