package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Retrofit.MyService
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    ////FacebookLogin
    private var mFacebookCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        setContentView(R.layout.activity_login)

        var loginButton = findViewById(R.id.login_button) as LoginButton
        loginButton.setReadPermissions("email")
        // If using in a fragment

        mFacebookCallbackManager = CallbackManager.Factory.create()

        // Callback registration
        loginButton.registerCallback(mFacebookCallbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) { // App code
            }

            override fun onCancel() { // App code
            }

            override fun onError(exception: FacebookException) { // App code
            }
        })

        var userName: String = ""
        var userId: String = ""


        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) { // App code

                    // 로그인해서 이메일, 이름 받아오기
                    val request = GraphRequest.newMeRequest(
                        loginResult!!.accessToken
                    ) { `object`, response ->
                        try {
                            userName = response.jsonObject.getString("name").toString()
                            userId = response.jsonObject.getString("id").toString()
                            Log.d("Result", userId+"  "+userName)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id,name")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Toast.makeText(this as Context, "페이스북 로그인을 취소하셨습니다.", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this as Context, exception.message, Toast.LENGTH_LONG).show()
                }
            })

        //val accessToken = AccessToken.getCurrentAccessToken()
        //val isLoggedIn = accessToken != null && !accessToken.isExpired

        var retrofit = Retrofit.Builder()
            .baseUrl("http://192.249.19.251:9080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var myService: MyService = retrofit.create(MyService::class.java)


        // register
        var registerBtn: Button = findViewById(R.id.registerBtn)
        registerBtn.setOnClickListener {
            if(userId!="" && userName!="") {
                // 서버로 register 전송
                myService.registerUser(userId, userName).enqueue(object: Callback<String>{
                    override fun onFailure(call: Call<String>, t: Throwable){
                        Log.e("register",t.message)
                        Toast.makeText(applicationContext, "register fail", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<String>, response: Response<String>){
                        Log.d("register",response.body())
                    }
                })

                Log.d("registerBtn","send id and name to server")
            }
            else {
                Toast.makeText(applicationContext, "login in Facebook first", Toast.LENGTH_SHORT).show()
            }
        }

        // login
        var loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            if(userId!=""){
                // 서버로 login 전송
                myService.loginUser(userId).enqueue(object: Callback<ContactData>{
                    override fun onFailure(call: Call<ContactData>, t: Throwable){
                        Log.e("login",t.message)
                        Toast.makeText(applicationContext, "login fail", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<ContactData>, response: Response<ContactData>){
                        Log.d("login",response.body()!!.facebookId)

                        // 메인으로 돌아가기
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.putExtra("id", userId)
                        startActivity(intent)
                    }
                })
                Log.d("loginBtn","send id to server")
            }
            else {
                Toast.makeText(applicationContext, "login in Facebook first", Toast.LENGTH_SHORT).show()
            }
        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

    }
    /****************************************************************************************************************
     * Facebook
     */
    /**
     * Facebook Login And Get Profile
     * Login시  Friends List, Email,User Profile 권한을 얻는다
     * 위 3개의 권한이상을 가지려면 심사에 통과해야한다
     * @author binaries
     * @date   16.07.20
     */

    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_button -> LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("public_profile")
            )
        }
    }
}