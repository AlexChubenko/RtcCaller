package com.rtccaller.displays.contacts

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ProcessLifecycleOwner
import com.rtccaller.R
import com.rtccaller.displays.call.CallActivity2
import com.rtccaller.displays.contacts.ContactsLifecycleDelegate.Companion.getRoomConnectionIntent
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class ContactsActivity : AppCompatActivity(), ContactsLifecycleDelegate.PreferencesReader {

    //todo inject with Dagger
    private lateinit var contactsLifecycleDelegate: ContactsLifecycleDelegate
    private lateinit var connectButton: ImageButton
    private lateinit var addFavoriteButton: ImageButton
    private lateinit var roomEditText: EditText
    private lateinit var roomListView: ListView
    private lateinit var roomList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var sharedPref: SharedPreferences
    private lateinit var keyprefRoom: String
    private lateinit var keyprefRoomList: String

//    @set:Inject
//    internal var fragmentAndroidInjector: DispatchingAndroidInjector<Fragment>? = null
//
//    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
//        return fragmentAndroidInjector
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keyprefRoom = getString(R.string.pref_room_key)
        keyprefRoomList= getString(R.string.pref_room_list_key)

        contactsLifecycleDelegate =
            ContactsLifecycleDelegate(this)
        setContentView(R.layout.activity_contacts)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(contactsLifecycleDelegate)
        initViews()
    }

    private fun initViews(){
        roomEditText = findViewById<View>(R.id.room_edittext) as EditText
        roomEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                addFavoriteButton.performClick()
                return@OnEditorActionListener true
            }
            false
        })
        roomEditText.requestFocus()

        roomListView = findViewById<View>(R.id.room_listview) as ListView
        roomListView.emptyView = findViewById<View>(android.R.id.empty)
        roomListView.setOnItemClickListener { _, view, i, l ->
            val roomId = (view as TextView).text.toString()
            connectToRoom(roomId,
                commandLineRun = false,
                loopback = false,
                useValuesFromIntent = false,
                runTimeMs = 0
            )
        }
        registerForContextMenu(roomListView)
        connectButton = findViewById<View>(R.id.connect_button) as ImageButton
        connectButton.setOnClickListener {
            Log.d(TAG, "aChube roomEditText.text.toString(): ${roomEditText.text.toString()}")
            connectToRoom(roomEditText.text.toString(),false, false, false,
            0)
        }

        addFavoriteButton = findViewById<View>(R.id.add_favorite_button) as ImageButton
        addFavoriteButton.setOnClickListener {
            val newRoom = roomEditText.text.toString()
            if (newRoom.isNotEmpty() && !roomList.contains(newRoom)) {
                adapter.add(newRoom)
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun connectToRoom(roomIdP: String?, commandLineRun: Boolean, loopback: Boolean,
                              useValuesFromIntent: Boolean, runTimeMs: Int
    ){
        connectionIntent = getRoomConnectionIntent(roomIdP, commandLineRun, loopback, useValuesFromIntent,
                runTimeMs, this)
        handlePermissions()
    }

    private var connectionIntent: Intent? = null

    private fun startConnection(){
        connectionIntent?.let{startActivityForResult(it, CONNECTION_REQUEST)}
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

    private fun handlePermissions() {
        val canAccessCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        val canRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!canAccessCamera || !canRecordAudio) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                CAMERA_AUDIO_PERMISSION_REQUEST
            )
        } else {
            startConnection()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.w(CallActivity2.TAG, "onRequestPermissionsResult: $requestCode $permissions $grantResults")
        when (requestCode) {
            CAMERA_AUDIO_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
//                    arePermissionsGranted = true
//                    startVideoSession()
                    startConnection()
                } else {
                    finish()
                }
                return
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        //todo check this
        val room = roomEditText.text.toString()
        val roomListJson = JSONArray(roomList).toString()
        val editor = sharedPref.edit()
        Log.d(TAG, "aChub onPause() keyprefRoom: $keyprefRoom sroomEditText.text.toString(): ${roomEditText.text.toString()}")

        editor.putString(keyprefRoom, room)
        editor.putString(keyprefRoomList, roomListJson)
        editor.commit()
    }

    public override fun onResume() {
        super.onResume()
        val room = sharedPref.getString(keyprefRoom, "")
        Log.d(TAG, "aChub onResume() keyprefRoom $keyprefRoom sharedPref.getString(keyprefRoom, \"\"): ${sharedPref.getString(keyprefRoom, "")}")
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

        private const val CAMERA_AUDIO_PERMISSION_REQUEST = 11
        private const val CONNECTION_REQUEST = 1
        private val REMOVE_FAVORITE_INDEX = 0
        private var commandLineRun = false
    }
}
