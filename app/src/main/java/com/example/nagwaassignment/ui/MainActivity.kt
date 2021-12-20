package com.example.nagwaassignment.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nagwaassignment.model.Attachments
import com.example.nagwaassignment.adapter.AttachmentsAdapter
import com.example.nagwaassignment.R
import com.example.nagwaassignment.app.MyApplication
import com.example.nagwaassignment.utils.AppConstants
import com.example.nagwaassignment.utils.PermissionListener
import com.example.nagwaassignment.utils.PermissionUtils
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

import java.nio.charset.Charset


class MainActivity() : AppCompatActivity(), PermissionListener{

    private val TAG = MainActivity::class.java.canonicalName

//    @Inject
//    lateinit var viewModelFactory: ViewModelFactory
//    @Inject
//     lateinit var viewModel: FilesLoadListViewModel

    lateinit var permissionUtils: PermissionUtils

    var customAdapter : AttachmentsAdapter? = null

    val permissions = arrayOf(
        PermissionUtils.WRITE_EXTERNAL_STORAGE,
        PermissionUtils.READ_EXTERNAL_STORAGE

    )

    var attachmentList: ArrayList<Attachments> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as MyApplication).getAppComponent().doInjectInApp(this)
//        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FilesLoadListViewModel::class.java)
        init()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val linearLayoutManager = LinearLayoutManager(applicationContext)


        recyclerView.layoutManager = linearLayoutManager

        try {
            val array = JSONArray(loadJSONFromAsset())

            for (i in 0 until array.length()) {
                val attachmentJSON = array.getJSONObject(i)
                val attachment = Attachments(attachmentJSON.getString("name"),attachmentJSON.getInt("id"), attachmentJSON.getString("url"),attachmentJSON.getString("type"))
                attachmentList.add(attachment)
            }
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        customAdapter = AttachmentsAdapter( attachmentList)

        recyclerView.adapter = customAdapter
    }


    private fun init() {

        if (!AppConstants.checkInternetConnection(this)) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }

    }


    private fun loadJSONFromAsset(): String {
        val json: String?
        try {
            val inputStream = assets.open("getListOfFilesResponse.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            val charset: Charset = Charsets.UTF_8
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, charset)
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }

    override fun onStart() {
        super.onStart()

        permissionUtils = PermissionUtils().with(this@MainActivity).setListener(this)
        val intentFilter = IntentFilter()
        intentFilter.addAction(AppConstants.Action)
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onResume() {
        super.onResume()
            if (Build.VERSION.SDK_INT >= 23) {
                for (permission in permissions) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionUtils.requestPermission(
                            arrayOf(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        )
                    } else {
                        if (AppConstants.checkInternetConnection(this@MainActivity))
                        else
                            Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                if (AppConstants.checkInternetConnection(this@MainActivity))
                    Toast.makeText(this@MainActivity, getString(R.string.must), Toast.LENGTH_SHORT).show()

                else
                    Toast.makeText(this@MainActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

            }

    }

    override fun allPermissionGranted() {

    }

    override fun onNeverAskAgainPermission(string: Array<String>) {

        permissionUtils.requestPermission(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    override fun onDenied(string: Array<String>) {

        permissionUtils.requestPermission(
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val porg = intent.getIntExtra(AppConstants.Progress,0)
            val complete = intent.getStringExtra(AppConstants.Complete)
            val itemPos =  intent.getIntExtra(AppConstants.ItemPostion,0)


            customAdapter?.setProgress_(porg,itemPos)
            customAdapter?.setProgress(listOf(porg))

            if(!complete.isNullOrEmpty()){
                customAdapter?.setValue(complete,itemPos,porg )
            }

        }
    }


    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }


}