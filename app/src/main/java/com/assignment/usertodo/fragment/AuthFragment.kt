package com.assignment.usertodo.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.assignment.usertodo.activity.HomeActivity
import com.assignment.usertodo.databinding.FragmentAuthBinding
import com.assignment.usertodo.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit

class AuthFragment : Fragment() {

    private lateinit var binding: FragmentAuthBinding

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = ""
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private lateinit var mDateSetListener: OnDateSetListener

    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)


        auth = Firebase.auth

        progressDialog = ProgressDialog(activity!!)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)


        initViews()
        firebaseCallback()
        return binding.root
    }

    private fun initViews() {

        binding.apply {

            btnNext.setOnClickListener {
                if (isValidate()) {
                    progressDialog.show()
                    sendOtp(etNumber.text.toString())
                }
            }

            btnVerify.setOnClickListener {
                val code: String = etVerficationCode.text.toString().trim()

                if (code.isEmpty() || code.length < 6) {
                    etVerficationCode.error = "Enter code..."
                    etVerficationCode.requestFocus()
                    return@setOnClickListener
                }
                progressDialog.show()
                verifyCode(code)
            }

            txtDateOfBirth.setOnClickListener {
                selectDate()
            }

            etFullName.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    selectDate()
                    return@OnEditorActionListener true
                }
                false
            })
        }
    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun selectDate() {

        //date select listener
        mDateSetListener =
            OnDateSetListener { _, year, month, day ->
                var monthOfYear = month
                monthOfYear += 1
                val date = "$day/$monthOfYear/$year"
                binding.apply {
                    txtDateOfBirth.text = date

                    etEmail.requestFocus()
                    val imm: InputMethodManager =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(etEmail, InputMethodManager.SHOW_IMPLICIT)
                }
            }


        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        //date dialog
        val dialog = DatePickerDialog(
            activity!!,
            mDateSetListener,
            year, month, day
        )
        dialog.show()
    }

    private fun showToast(s: String) = Toast.makeText(activity, s, Toast.LENGTH_LONG).show()

    //check all validation
    private fun isValidate(): Boolean {
        binding.apply {
            if (etFullName.text.toString().isEmpty()) {
                etFullName.error = "Please Enter Your Name"
                etFullName.requestFocus()
            } else if (txtDateOfBirth.text.toString().isEmpty()) {
                txtDateOfBirth.error = "Please Select DOB"
                txtDateOfBirth.requestFocus()
            } else if (etEmail.text.toString().isEmpty()
                || !Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()
            ) {
                etEmail.error = "Please Enter Valid email id"
                etEmail.requestFocus()
            } else if (etNumber.text.toString().isEmpty()
                || etNumber.text.toString().length < 10
            ) {
                etNumber.error = "Please Enter Number"
                etNumber.requestFocus()
            } else {
                return true
            }
        }
        return false
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun sendOtp(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity!!)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun firebaseCallback() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progressDialog.hide()
                showToast("onVerificationCompleted")
                //   signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                showToast("Something is wrong!!!")
            }

            override fun onCodeSent(
                vId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = vId
                binding.apply {
                    llForm.visibility = View.GONE
                    llVerification.visibility = View.VISIBLE
                }
                progressDialog.hide()
                showToast("Verification Code sent")
            }
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity!!) { task ->
                if (task.isSuccessful) {
                    saveData()
                } else {
                    showToast("Please enter valid code")
                }
            }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun saveData() {
        binding.apply {
            val name = etFullName.text.toString()
            val dob = txtDateOfBirth.text.toString()
            val email = etEmail.text.toString()
            val number = etNumber.text.toString()

            val database = Firebase.database
            val myRef = database.getReference("+91$number").child("MyData")
            myRef.setValue(User(name,dob,email,number))
                .addOnCompleteListener { task ->
                    progressDialog.dismiss()
                    if (task.isSuccessful) {
                        val sharedPreferences: SharedPreferences = activity!!.getSharedPreferences("MySharedPreference",Context.MODE_PRIVATE)
                        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                        editor.putString("number","+91$number")
                        editor.apply()
                        showToast("Login Successful")
                        startActivity(Intent(context, HomeActivity::class.java))
                        activity?.finish()
                    } else {
                        showToast("Something is wrong")
                    }
                }
        }
    }
}