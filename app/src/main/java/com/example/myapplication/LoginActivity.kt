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


class LoginActivity : AppCompatActivity(), View.OnClickListener {

    ////FacebookLogin
    private var mFacebookCallbackManager: CallbackManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        setContentView(R.layout.fragment_main)

        var loginButton = findViewById(R.id.login_button) as LoginButton
        loginButton.setReadPermissions("email")
        // If using in a fragment
        // If using in a fragment
        //loginButton.activity = this as Fragment

        mFacebookCallbackManager = CallbackManager.Factory.create()

        // Callback registration
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
                }

                override fun onCancel() { // App code
                }

                override fun onError(exception: FacebookException) { // App code
                }
            })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired


        //initFacebook()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mFacebookCallbackManager?.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data);

//        super.onActivityResult(requestCode, resultCode, data)
//        mFacebookCallbackManager!!.onActivityResult(requestCode, resultCode, data)
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
    private fun initFacebook() { //FaceBook Init
        FacebookSdk.sdkInitialize(this.getApplicationContext())
        mFacebookCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("Success", loginResult.accessToken.toString())
                    Log.d(
                        "Success",
                        Profile.getCurrentProfile().id.toString()
                    )
                    Log.d(
                        "Success",
                        Profile.getCurrentProfile().name.toString()
                    )
                    Log.d(
                        "Success",
                        Profile.getCurrentProfile().getProfilePictureUri(
                            200,
                            200
                        ).toString()
                    )
                    requestUserProfile(loginResult)
                }

                override fun onCancel() {
                    Toast.makeText(this as Context, "페이스북 로그인을 취소하셨습니다.", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this as Context, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    fun requestUserProfile(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { `object`, response ->
            try {
                val email =
                    response.jsonObject.getString("email").toString()
                Log.d("Result", email)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "email")
        request.parameters = parameters
        request.executeAsync()
    }

    /****************************************************************************************************************
     * Facebook
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