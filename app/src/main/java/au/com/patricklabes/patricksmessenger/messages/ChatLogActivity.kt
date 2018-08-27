package au.com.patricklabes.patricksmessenger.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import au.com.patricklabes.patricksmessenger.R
import au.com.patricklabes.patricksmessenger.models.ChatMessage
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
        val TAG = "ChatLogActivity1"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recylerview_chatlog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.User_Key)

        supportActionBar?.title = toUser?.username

        //setupDummyData()

        listenForMessages()

        button_send_chatlog.setOnClickListener {
            Log.d(TAG,"Chat log button pressed")
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

                if(chatMessage != null) {

                    //Log.d(TAG, chatMessage.text + "to ${chatMessage.toId} from ${chatMessage.fromId}")

                    Log.d(TAG,  "FUCK ${chatMessage.fromId} to ${FirebaseAuth.getInstance().uid}")

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){

                        Log.d(TAG,"THE TRUTH")

//                        val currentUser = LatestMessagesActivity.currentUser ?: return
//
//                        Log.d(TAG,"from message user ${currentUser.username}")

                        adapter.add(ChatFromItem(chatMessage.text))

                    }else{
                        adapter.add(ChatToItem(chatMessage.text))

//                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

                }

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
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

}

class ChatToItem(val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_log_to.text = text
        //load image
//        val uri = user.profileImageUrl
//        val targetImageView = viewHolder.itemView.chatlog_image_to
//
//        Picasso.get().load(uri).into(targetImageView)
    }
}

class ChatFromItem(val text: String) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_chat_log_from.text = text

//        val uri = user.profileImageUrl
//        val targetImageView = viewHolder.itemView.chatlog_image_from
//
//        Picasso.get().load(uri).into(targetImageView)
    }
}