package au.com.patricklabes.patricksmessenger.models

import au.com.patricklabes.patricksmessenger.R
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_activity_rom.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){

    var chatPartnerUser: User? = null

    override fun getLayout(): Int {
        return R.layout.latest_message_activity_rom
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val dbuser = chatMessage.toId

        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("/users/$dbuser")



        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                chatPartnerUser = p0.getValue(User::class.java)?:return

                viewHolder.itemView.title_name_textView_latest_activity_row.text = chatPartnerUser?.username
                viewHolder.itemView.lattest_message_textView_latest_activity_row.text = chatMessage.text

                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(viewHolder.itemView.profile_image_latest_message_activity_row)

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })




    }
}