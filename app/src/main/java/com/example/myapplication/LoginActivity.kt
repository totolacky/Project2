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
import java.security.AccessController.getContext
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
        var userEmail: String = ""


        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) { // App code

                    // 로그인해서 이메일, 이름 받아오기
                    val request = GraphRequest.newMeRequest(
                        loginResult!!.accessToken
                    ) { `object`, response ->
                        try {
                            userName = response.jsonObject.getString("name").toString()
                            userEmail = response.jsonObject.getString("email").toString()
                            Log.d("Result", userEmail+"  "+userName)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "email,name")
                    request.parameters = parameters
                    request.executeAsync()


                    // 받아온 거 서버로 전송

                    ///// retrofit으로 했는데 망한 거 같음
                    /*var compositeDisposable: CompositeDisposable = CompositeDisposable()
                    var retrofitClient: Retrofit? = RetrofitClient.getinstance()
                    var iMyService: IMyService = retrofitClient!!.create(IMyService::class.java)

                    compositeDisposable.add(iMyService.loginUser(email)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer<String>(){
                            fun accept(response: String) {
                                Toast.makeText(this as Context, ""+response, Toast.LENGTH_SHORT).show()
                            }
                        })
                    )*/

                    ////// volley로 재도전한거도망한거같음
                    /*lateinit var requestQueue: RequestQueue
                    requestQueue = Volley.newRequestQueue(applicationContext)
                    lateinit var stringRequest: StringRequest
                    val url = "https://localhost:80/"

                    stringRequest = StringRequest(Request.Method.GET, url, Response.Listener<String> {
                                response -> Log.d("response","ok")
                        }, Response.ErrorListener {
                                error ->  i(applicationContext.packageName, error.toString())
                        })

                        // Set tag for cancel
                        stringRequest.tag = applicationContext.packageName
                        // Request
                        requestQueue.add(stringRequest)
                        */

                }

                override fun onCancel() {
                    Toast.makeText(this as Context, "페이스북 로그인을 취소하셨습니다.", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this as Context, exception.message, Toast.LENGTH_LONG).show()
                }
            })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        var retrofit = Retrofit.Builder()
            .baseUrl("http://192.249.19.251:9080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var myService: MyService = retrofit.create(MyService::class.java)


        var registerBtn: Button = findViewById(R.id.registerBtn)
        registerBtn.setOnClickListener {
            if(userEmail!="" && userName!="") {
                // 서버로 register 전송
                myService.registerUser(userEmail, userName).enqueue(object: Callback<String>{
                    override fun onFailure(call: Call<String>, t: Throwable){
                        Log.e("register",t.message)
                        Toast.makeText(applicationContext, "register fail", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<String>, response: Response<String>){
                        Log.d("register","????????")
                    }
                })

                Log.d("registerBtn","send email and name to server")
            }
            else {
                Toast.makeText(applicationContext, "login in Facebook first", Toast.LENGTH_SHORT).show()
            }
        }

        var loginBtn: Button = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            if(userEmail!=""){
                // 서버로 login 전송
                myService.loginUser(userEmail).enqueue(object: Callback<String>{
                    override fun onFailure(call: Call<String>, t: Throwable){
                        Log.e("login",t.message)
                        Toast.makeText(applicationContext, "login fail", Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<String>, response: Response<String>){
                        Log.d("login","?????????????")
                    }
                })
                Log.d("loginBtn","send email to server")
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