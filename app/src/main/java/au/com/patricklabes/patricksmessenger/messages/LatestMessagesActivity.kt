package au.com.patricklabes.patricksmessenger.messages

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import au.com.patricklabes.patricksmessenger.R
import au.com.patricklabes.patricksmessenger.models.ChatMessage
import au.com.patricklabes.patricksmessenger.models.User
import au.com.patricklabes.patricksmessenger.registrationAndLogin.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_latest_messages.*
import kotlinx.android.synthetic.main.latest_message_activity_rom.view.*

class LatestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }

    var TAG = "latestMessages"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messages)

        verifyUserIsLoggedIn()

        fetchCurrentUser()

        supportActionBar?.title = "Messages"

        recyclerView_latest_messages.adapter = adapter

        listenForLatestMessages()

    }

    val  latestMessageMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        
        latestMessageMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }


    class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.latest_message_activity_rom
        }

        override fun bind(viewHolder: ViewHolder, position: Int) {

            val dbuser = chatMessage.toId

            val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("/users/$dbuser")



            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {

                    val user = p0.getValue(User::class.java)?:return

                    viewHolder.itemView.title_name_textView_latest_activity_row.text = user.username
                    viewHolder.itemView.lattest_message_textView_latest_activity_row.text = chatMessage.text

                    Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.profile_image_latest_message_activity_row)

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })




        }
    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessageMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
        })
    }

    val adapter = GroupAdapter<ViewHolder>()




    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid

        Log.d(TAG,"user uid $uid")

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")



        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("latestMessages","current user ${currentUser}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn() {

        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {


        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)

            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


}



