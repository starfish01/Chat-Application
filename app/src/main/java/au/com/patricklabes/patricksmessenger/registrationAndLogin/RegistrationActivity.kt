package au.com.patricklabes.patricksmessenger.registrationAndLogin

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import au.com.patricklabes.patricksmessenger.R
import au.com.patricklabes.patricksmessenger.messages.LatestMessagesActivity
import au.com.patricklabes.patricksmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


        reg_button_reg_screen.setOnClickListener {
            preformReg()
        }

        already_have_account_textView_reg_screen.setOnClickListener {

            Log.d("RegisterActivity", "textbutton Pressed")


            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        selectphoto_image_registration_screen.setOnClickListener {
            Log.d("RegisterActivity", "try to show photo selector")

            if(checkPermission()){
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }

        }
    }

    fun checkPermission(): Boolean{

        val RECORD_REQUEST_CODE = 101

        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), RECORD_REQUEST_CODE)

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                return false
            }
            return true
        }
        return true
    }




    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("RegisterActivity", "photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            userphoto_imageview_registration.setImageBitmap(bitmap)

            selectphoto_image_registration_screen.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//
//            selectphoto_image_registration_screen.setBackgroundDrawable(bitmapDrawable)

        }

    }


    private fun preformReg() {

        val email = email_edittext_reg_screen.text.toString()
        val password = password_edittext_reg_screen.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in Email/ Password", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Please enter a profile image", Toast.LENGTH_SHORT).show()
            return
        }


        Log.d("RegisterActivity", "Email is " + email)
        Log.d("RegisterActivity", "Password: $password")

        //firebase auth
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful()) return@addOnCompleteListener

                    Toast.makeText(this, "${it.result.user.email} was created", Toast.LENGTH_SHORT).show()
                    // else if successful

                    uploadImageToFirebaseStorage()

                    Log.d("RegisterActivity", "Successfully created user uid: ${it.result.user.uid}")
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to create user ${it.message}")

                    Toast.makeText(this, "Failed to create user ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToFirebaseStorage() {

        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully Uploaded image ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        it.toString()
                        Log.d("RegisterActivity", "file Location: $it")

                        saveUserToDatabase(it.toString())

                    }

                }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to uploadimage ${it.message}")
                }

    }

    private fun saveUserToDatabase(imageurl: String) {

        val uid = FirebaseAuth.getInstance().uid ?: ""

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_reg_screen.text.toString(), imageurl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "we have saved to the db")

                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to uploadimage ${it.message}")
                }

    }


}




