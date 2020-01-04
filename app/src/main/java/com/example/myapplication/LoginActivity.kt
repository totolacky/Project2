package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONException
import java.util.*
import com.facebook.Profile.getCurrentProfile
import com.facebook.ProfileTracker
import org.json.JSONObject
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.myapplication.Retrofit.IMyService
import com.example.myapplication.Retrofit.RetrofitClient
import com.facebook.GraphResponse
import com.facebook.GraphRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.create
import java.security.AccessController.getContext


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


        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) { // App code

                    var name: String = ""
                    var email: String = ""

                    val request = GraphRequest.newMeRequest(
                        loginResult!!.accessToken
                    ) { `object`, response ->
                        try {
                            name = response.jsonObject.getString("name").toString()
                            email = response.jsonObject.getString("email").toString()
                            Log.d("Result", email+"  "+name)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "email,name")
                    request.parameters = parameters
                    request.executeAsync()

                    var compositeDisposable: CompositeDisposable = CompositeDisposable()
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
                    )

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