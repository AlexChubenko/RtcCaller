package com.rtccaller.contacts

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ProcessLifecycleOwner
import com.anuntis.rtccaller.R
import com.rtccaller.call.CallActivity
import com.rtccaller.utils.ContactsLifecycleDelegate
import com.rtccaller.utils.ContactsLifecycleDelegate.Companion.getRoomConnectionIntent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList
import javax.inject.Inject

class ContactsActivity : AppCompatActivity(), HasSupportFragmentInjector, ContactsLifecycleDelegate.PreferencesReader {

    //todo inject with Dagger
    private lateinit var contactsLifecycleDelegate: ContactsLifecycleDelegate
    private lateinit var connectButton: ImageButton
    private lateinit var addFavoriteButton: ImageButton
    private lateinit var roomEditText: EditText
    private lateinit var roomListView: ListView
    private lateinit var roomList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPref: SharedPreferences
    private var keyprefRoom: String? = null
    private var keyprefRoomList: String? = null

    @set:Inject
    internal var fragmentAndroidInjector: DispatchingAndroidInjector<Fragment>? = null

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactsLifecycleDelegate =
            ContactsLifecycleDelegate(this)
        setContentView(R.layout.activity_contacts)
        ProcessLifecycleOwner.get().lifecycle.addObserver(contactsLifecycleDelegate)
        initViews()
    }

    private fun initViews(){
        roomEditText = findViewById<View>(R.id.room_edittext) as EditText
        roomEditText.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                addFavoriteButton.performClick()
                return@OnEditorActionListener true
            }
            false
        })
        roomEditText.requestFocus()
        roomListView = findViewById<View>(R.id.room_listview) as ListView
        roomListView.setEmptyView(findViewById<View>(android.R.id.empty))
        roomListView.setOnItemClickListener { adapterView, view, i, l ->
            val roomId = (view as TextView).getText().toString()
            connectToRoom(roomId, false, false, false,
                0)
        }
        registerForContextMenu(roomListView)
        connectButton = findViewById<View>(R.id.connect_button) as ImageButton
        connectButton.setOnClickListener { connectToRoom(roomEditText.getText().toString(),false, false, false,
            0) }
        addFavoriteButton = findViewById<View>(R.id.add_favorite_button) as ImageButton
        addFavoriteButton.setOnClickListener {
            val newRoom = roomEditText.getText().toString()
            if (newRoom.length > 0 && !roomList.contains(newRoom)) {
                adapter.add(newRoom)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun connectToRoom(roomIdP: String?, commandLineRun: Boolean, loopback: Boolean,
                              useValuesFromIntent: Boolean, runTimeMs: Int){
        getRoomConnectionIntent(roomIdP, commandLineRun, loopback, useValuesFromIntent,
                runTimeMs, this)?.let{

                startActivityForResult(it, CONNECTION_REQUEST)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.connect_menu, menu)
        return true
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo
    ) {
        if (v.id == R.id.room_listview) {
            val info = menuInfo as AdapterView.AdapterContextMenuInfo
            menu.setHeaderTitle(roomList[info.position])
            val menuItems = resources.getStringArray(R.array.roomListContextMenu)
            for (i in menuItems.indices) {
                menu.add(Menu.NONE, i, i, menuItems[i])
            }
        } else {
            super.onCreateContextMenu(menu, v, menuInfo)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == REMOVE_FAVORITE_INDEX) {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            roomList.removeAt(info.position)
            adapter.notifyDataSetChanged()
            return true
        }

        return super.onContextItemSelected(item)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle presses on the action bar items.
//        if (item.itemId == R.id.action_settings) {
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
//            return true
//        } else if (item.itemId == R.id.action_loopback) {
        Log.d(TAG, "onOptionsItemSelected()")
            connectToRoom(null, false, true, false, 0)
            return true
//        } else {
//            return super.onOptionsItemSelected(item)
//        }

    }
    public override fun onPause() {
        super.onPause()
        val room = roomEditText.getText().toString()
        val roomListJson = JSONArray(roomList).toString()
        val editor = sharedPref.edit()
        editor.putString(keyprefRoom, room)
        editor.putString(keyprefRoomList, roomListJson)
        editor.commit()
    }

    public override fun onResume() {
        super.onResume()
        val room = sharedPref.getString(keyprefRoom, "")
        roomEditText.setText(room)
        roomList = ArrayList()
        val roomListJson = sharedPref.getString(keyprefRoomList, null)
        if (roomListJson != null) {
            try {
                val jsonArray = JSONArray(roomListJson)
                for (i in 0 until jsonArray.length()) {
                    roomList.add(jsonArray.get(i).toString())
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Failed to load room list: $e")
            }

        }
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, roomList)
        roomListView.adapter = adapter
        if (adapter.count > 0) {
            roomListView.requestFocus()
            roomListView.setItemChecked(0, true)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: $resultCode")
            setResult(resultCode)
            commandLineRun = false
            finish()
        }
    }

    companion object{
        private val TAG = ContactsActivity.javaClass.simpleName
        private const val CONNECTION_REQUEST = 1
        private val REMOVE_FAVORITE_INDEX = 0
        private var commandLineRun = false
    }
}
