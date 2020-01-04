package com.example.myapplication.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.ContactData
import com.example.myapplication.R
import kotlinx.android.synthetic.main.fragment_contact.*
import java.time.Duration

/**
 * The address fragment.
 */
class ContactFragment : Fragment() {

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
        fun newInstance(): ContactFragment {
            return ContactFragment()
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

        // Default arraylist
        val arrayList = ArrayList<ContactData?>()
        arrayList.add(ContactData(0,"Tom",null,0,null,"tom@example.com",null,null,null))
        arrayList.add(ContactData(1,"Mike","feel like Mike",0,null,"mike@example.com",null,null,null))
        arrayList.add(ContactData(2,"Henry","feel like Henry",0,null,"henry@example.com",null,null,null))
        arrayList.add(ContactData(3,"Alice","feel sooooo Alice",0,null,"alice@example.com",null,null,null))
        arrayList.add(ContactData(4,"Julia","hehe",0,null,"julia@example.com",null,null,null))
        arrayList.add(ContactData(5,"Daniel",null,0,null,"daniel@example.com",null,null,null))
        arrayList.add(ContactData(6,"Steve",null,0,null,"steve@example.com",null,null,null))
        arrayList.add(ContactData(7,"Sophie","hi",0,null,"sophie@example.com",null,null,null))
        arrayList.add(ContactData(8,"Timothy","heyy",0,null,"timothy@example.com",null,null,null))
        arrayList.add(ContactData(9,"Julien","yo",0,null,"julien@example.com",null,null,null))
        arrayList.add(ContactData(10,"Kevin","hehehehehe",0,null,"kevin@example.com",null,null,null))
        arrayList.add(ContactData(11,"Jake",null,0,null,"jake@example.com",null,null,null))
        arrayList.add(ContactData(12,"Jane","null?",0,null,"jane@example.com",null,null,null))

        return arrayList
    }

}