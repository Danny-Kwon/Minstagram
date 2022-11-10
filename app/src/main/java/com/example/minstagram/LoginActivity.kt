package com.example.minstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.minstagram.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        with(binding){
            emailSignUpButton.setOnClickListener {
                signUpInEmail(emailEdittext.text.toString().trim(), passwordEdittext.text.toString().trim())
            }
            emailLoginButton.setOnClickListener {
                signInEmail(emailEdittext.text.toString().trim(), passwordEdittext.text.toString().trim())
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