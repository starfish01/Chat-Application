package au.com.patricklabes.patricksmessenger.messages

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import au.com.patricklabes.patricksmessenger.R
import au.com.patricklabes.patricksmessenger.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        //val adapter = GroupAdapter<ViewHolder>()
        //recyclearview_newmessage.adapter = adapter

        fetchUsers()

    }


    companion object {
        val User_Key = "User_key"
    }


    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
                    Log.d("newmessage1", it.toString())

                    val user1 : String = LatestMessagesActivity.currentUser!!.uid

                    Log.d("newmessage1", (it.key == user1).toString())

                    val user = it.getValue(User::class.java)

                    if (user != null && (it.key != user1)) {

                        adapter.add(UserItem(user))

                    }

                    adapter.setOnItemClickListener { item, view ->

                        val userItem = item as UserItem

                        val intent = Intent(view.context, ChatLogActivity::class.java)

                        intent.putExtra(User_Key, userItem.user)

                        startActivity(intent)

                        finish()

                    }

                    recyclearview_newmessage.adapter = adapter

                }
            }

        })
    }
}


class UserItem(val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textView_newmessage.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.image_imageView_newmessage)

    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}
