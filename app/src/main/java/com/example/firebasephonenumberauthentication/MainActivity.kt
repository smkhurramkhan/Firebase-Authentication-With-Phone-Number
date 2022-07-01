package com.example.firebasephonenumberauthentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasephonenumberauthentication.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import io.github.rupinderjeet.kprogresshud.KProgressHUD
import timber.log.Timber
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var mainActivityBinding: ActivityMainBinding

    // variable for FirebaseAuth class
    private var mAuth: FirebaseAuth? = null
    private var verificationId: String? = null

    private var kHudProgress: KProgressHUD? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivityBinding.root)

        FirebaseAuth.getInstance().firebaseAuthSettings
            .setAppVerificationDisabledForTesting(false)

        mAuth = FirebaseAuth.getInstance()

        mainActivityBinding.idBtnGetOtp.setOnClickListener {
            if (TextUtils.isEmpty(mainActivityBinding.idEdtPhoneNumber.text.toString())) {
                Toast.makeText(
                    this@MainActivity,
                    "Please enter a valid phone number.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                showDialog()
                Timber.d("else called")

                val phone = "+92" + mainActivityBinding.idEdtPhoneNumber.text.toString()
                sendVerificationCode(phone)
            }
        }


        mainActivityBinding.idBtnVerify.setOnClickListener {
            if (TextUtils.isEmpty(mainActivityBinding.idEdtOtp.text.toString())) {
                Toast.makeText(
                    this,
                    "Please enter OTP",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showDialog()
                verifyCode(mainActivityBinding.idEdtOtp.text.toString())
            }
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    kHudProgress?.dismiss()
                    val i = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        task.exception?.message,
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
    }

    private fun showDialog() {
        kHudProgress = KProgressHUD.create(this@MainActivity)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel(getString(R.string.please_wait))
            .setCancellable(false)
            .setAnimationSpeed(1)
            .setDimAmount(0.5f)
            .show()
    }


    private fun sendVerificationCode(number: String) {
        Timber.d("sendVerificationCode called")
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // callback method is called on Phone auth provider.
    private val
            mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                Timber.d("onCodeSent string is $s")
                Timber.d("onCodeSent forceResendingToken is $forceResendingToken")
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                Timber.d("Verification Code Send")
                Toast.makeText(
                    this@MainActivity,
                    "Verification Code Send",
                    Toast.LENGTH_SHORT
                ).show()

                kHudProgress?.dismiss()

            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                Timber.d("onVerificationCompleted code is $code")
                if (code != null) {
                    mainActivityBinding.idEdtOtp.setText(code)
                    verifyCode(code)
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                Timber.d("onVerificationFailed exception is ${e.message}")
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                kHudProgress?.dismiss()
            }
        }

    // below method is use to verify code from Firebase.
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }

}