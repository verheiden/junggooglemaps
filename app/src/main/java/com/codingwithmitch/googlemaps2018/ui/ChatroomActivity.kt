package apps.com.codingwithmitch.googlemaps2018.ui

import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.codingwithmitch.googlemaps2018.adapters.ChatMessageRecyclerAdapter
import com.codingwithmitch.googlemaps2018.models.ChatMessage
import com.codingwithmitch.googlemaps2018.models.Chatroom
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.ArrayList
import java.util.HashSet

class ChatroomActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "ChatroomActivity"

    //widgets
    private var mChatroom: Chatroom? = null
    private var mMessage: EditText? = null

    //vars
    private var mChatMessageEventListener: ListenerRegistration? = null
    private var mUserListEventListener:ListenerRegistration? = null
    private var mChatMessageRecyclerView: RecyclerView? = null
    private var mChatMessageRecyclerAdapter: ChatMessageRecyclerAdapter? = null
    private var mDb: FirebaseFirestore? = null
    private val mMessages = ArrayList<ChatMessage>()
    private val mMessageIds = HashSet<String>()
    private var mUserList = ArrayList<User>()
    private val mUserListFragment: UserListFragment? = null
    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}