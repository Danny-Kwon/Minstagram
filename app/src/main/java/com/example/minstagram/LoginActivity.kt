package com.example.minstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.minstagram.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    private var GOOGLE_LOGIN_CODE = 9001
    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        with(binding){
            emailSignUpButton.setOnClickListener {
                signUpInEmail(emailEdittext.text.toString().trim(), passwordEdittext.text.toString().trim())
            }
            emailLoginButton.setOnClickListener {
                signInEmail(emailEdittext.text.toString().trim(), passwordEdittext.text.toString().trim())
            }
            googleLoginButton.setOnClickListener {
                googleLogin()
            }
        }
    }

    private fun googleLogin() {
        val signInClient = googleSignInClient?.signInIntent
        startActivityForResult(signInClient, GOOGLE_LOGIN_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result!!.isSuccess){
                val account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if(task.isSuccessful){
                moveToNext(task.result?.user)
            } else {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpInEmail(id: String, password: String){
        auth?.createUserWithEmailAndPassword(id, password)
            ?.addOnCompleteListener{ task ->
                task.addOnSuccessListener {
                    binding.emailEdittext.text = null
                    binding.passwordEdittext.text = null
                    Toast.makeText(applicationContext, "Successfully signed up by email", Toast.LENGTH_SHORT).show()
                }
                task.addOnFailureListener {
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun signInEmail(id: String, password: String){
        auth?.signInWithEmailAndPassword(id, password)
            ?.addOnCompleteListener { task ->
                task.addOnSuccessListener {
                    moveToNext(task.result.user)
                }
                task.addOnFailureListener{
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun moveToNext(user: FirebaseUser?){
        if(user != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }else{
            Toast.makeText(this, "화면 이동 실패", Toast.LENGTH_SHORT).show()
        }
    }
}