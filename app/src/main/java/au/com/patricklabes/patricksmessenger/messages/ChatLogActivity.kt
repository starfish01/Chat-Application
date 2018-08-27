package au.com.patricklabes.patricksmessenger.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import au.com.patricklabes.patricksmessenger.R
import au.com.patricklabes.patricksmessenger.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        //val username = intent.getStringExtra(NewMessageActivity.User_Key)

        val user = intent.getParcelableExtra<User>(NewMessageActivity.User_Key)

        supportActionBar?.title = user.username

        //setupDummyData()

        listenForMessages()

        button_send_chatlog.setOnClickListener {
            Log.d("chatlogactivity","Chat log button pressed")
            preformSendMessage();
        }


    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    class ChatMessage(val id: String, val text: String, val fromId: String, val toId:String, val timestap:Long){
        constructor() : this("","","","",-1)
    }


    private fun preformSendMessage(){

        val text = editText_chatlog.text.toString()

        val fromId = FirebaseAuth.getInstance().uid

        if (fromId == null) return

        val user = intent.getParcelableExtra<User>(NewMessageActivity.User_Key)

        val toId = user.uid

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId,toId, System.currentTimeMillis())

        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d("chatlogactivity", "Successfully saved message ${reference.key}")
                }

    }

    fun setupDummyData(){
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem("From message"))
        adapter.add(ChatToItem("To message"))


        recylerview_chatlog.adapter = adapter
    }
}

class ChatToItem(val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_log_to.text = text
    }
}

class ChatFromItem(val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_log_from.text = text
    }
}