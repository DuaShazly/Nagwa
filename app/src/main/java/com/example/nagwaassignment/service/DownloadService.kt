package com.example.nagwaassignment.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.nagwaassignment.*
import com.example.nagwaassignment.model.Attachments
import com.example.nagwaassignment.restclient.ApiCallInterface
import com.example.nagwaassignment.utils.AppConstants
import com.example.nagwaassignment.model.Download
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import java.io.*


class DownloadService : Service() {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize: Int = 0
    private var fileDirectory: File? = null
    private var appName = "app_name"
    private val NOTIF_ID = 10
    private var fileType: String = ""
    private val TAG = DownloadService::class.java.canonicalName
    var mAttach: Attachments? = null
    var itemPostion: Int? = null
    val handler = Handler(Looper.getMainLooper())




    override fun onCreate() {
        super.onCreate()
        fileDirectory =
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Nagwa"
            )
        if (!fileDirectory!!.exists()) {
            fileDirectory!!.mkdirs()
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mAttach = intent.getSerializableExtra(AppConstants.attachObj) as Attachments?
        itemPostion = intent.getIntExtra(AppConstants.ItemPostion,0)
        if (null != intent.getStringExtra(AppConstants.URL)) {
            initNotification()
            initDownload(intent.getStringExtra(AppConstants.URL)!!)
        }
        return Service.START_REDELIVER_INTENT
    }

    private fun initNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW)

            notificationChannel.description = "no sound"
            notificationChannel.setSound(null, null)
            notificationChannel.enableLights(false)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(false)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }

        notificationBuilder = NotificationCompat.Builder(this, "id")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(getString(R.string.download))
            .setContentText(getString(R.string.downloading_file))
            .setDefaults(0)
            .setOngoing(true)
            .setAutoCancel(false)

        notificationManager!!.notify(NOTIF_ID, notificationBuilder!!.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun initDownload(url: String) {

        if (url.contains(".pdf")) {
            fileType = ".pdf"
        } else if (url.contains(".mp4")) {
            fileType = ".mp4"
        }

        if (AppConstants.checkInternetConnection(applicationContext)) {
            val api = ApiCallInterface.create()
            api.downloadFileRX(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onNext(responseBody: ResponseBody) {
                        try {
                            downloadFile(responseBody)
                        } catch (e: IOException) {
                            val handler = Handler(Looper.getMainLooper())
                            handler.postDelayed({
                                Toast.makeText(
                                    applicationContext,
                                    e.localizedMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }, 0)

                            e.printStackTrace()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        handler.postDelayed(Runnable {
                            Toast.makeText(
                                this@DownloadService,
                                getString(R.string.please_wait),
                                Toast.LENGTH_SHORT
                            ).show()
                        }, 0)

                    }

                    override fun onError(e: Throwable) {
                        handler.postDelayed(Runnable {
                            Toast.makeText(
                                this@DownloadService,
                                getString(R.string.error_msg),
                                Toast.LENGTH_SHORT
                            ).show()
                        }, 0)

                        sendMyBroadcastReceiver(0,"error",itemPostion!!)

                        stopSelf()
                    }

                    override fun onComplete() {
                        handler.postDelayed( {
                            Toast.makeText(
                                this@DownloadService,
                                getString(R.string.some_thing_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }, 0)

                        sendMyBroadcastReceiver(0,"complete",itemPostion!!)

                        stopSelf()
                    }
                })


        } else {
            Toast.makeText(
                this,
                getString(R.string.network_is_not_available),
                Toast.LENGTH_SHORT
            ).show()
            stopSelf()
        }

    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody) {
        var count: Int
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFolder =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Nagwa")

        var fileName: String? = mAttach?.url?.lastIndexOf('/')?.plus(1)
            ?.let { mAttach?.url?.substring(it) }
        val pos: Int? = fileName?.lastIndexOf(".")
        if (pos != null) {
            if (pos > 0) {
                fileName = fileName?.substring(0, pos)
            }
        }
        val mFile = File(outputFolder.absolutePath + "/" + fileName + System.currentTimeMillis() + fileType)
        val output = FileOutputStream(mFile)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        val download = Download()

        while (true) {
            count = bis.read(data)
            if (count == -1)
                break

            total += count.toLong()
            totalFileSize = (fileSize / Math.pow(1024.0, 2.0)).toInt()
            val current = Math.round(total / Math.pow(1024.0, 2.0)).toDouble()

            val progress = (total * 100 / fileSize).toInt()


            val currentTime = System.currentTimeMillis() - startTime

            download.totalFileSize = totalFileSize

            if (currentTime > 1000 * timeCount) {
                download.currentFileSize = current.toInt()
                download.progress = progress
                sendNotification(download)
                //  sendMyBroadcastReceiver(download.progress,"",itemPostion!!)

                timeCount++
            }
            output.write(data, 0, count)
        }
        onDownloadComplete(mFile)
        output.flush()
        output.close()
        bis.close()
        stopSelf()
    }




    @SuppressLint("DefaultLocale")
    private fun sendNotification(download: Download) {

        sendMyBroadcastReceiver(download.progress,"",itemPostion!!)

        notificationBuilder!!.setProgress(100, download.progress, false)
        notificationBuilder!!.setContentText(
            String.format(
                "Downloaded (%d/%d) MB",
                download.currentFileSize,
                download.totalFileSize
            )
        )
        notificationManager!!.notify(NOTIF_ID, notificationBuilder!!.build())
    }


    private fun onDownloadComplete(s: File) {
        val download = Download()
        download.progress = 100
        notificationManager!!.cancel(0)
        notificationBuilder!!.setProgress(0, 100, false)


        var shareType = "image/png"
        if (s.absolutePath.contains(".pdf"))
            shareType = "application/pdf"

        val intent = getShareGlobal(this, s.absolutePath, shareType, appName)

        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        notificationBuilder = NotificationCompat.Builder(this, "id")
        notificationBuilder!!.setContentTitle(getString(R.string.downloaded_file_msg))
        notificationBuilder!!.setContentText("${mAttach?.name} " +getString(R.string.download_file_msg))
            .setWhen(System.currentTimeMillis())
            .setContentIntent(contentIntent)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setAutoCancel(true)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_file_download)
            .build()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.notify(1, notificationBuilder!!.build())
        sendMyBroadcastReceiver(0,"complete",itemPostion!!)

    }


    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager?.cancel(NOTIF_ID)
    }


    private fun getShareGlobal(
        context: Context,
        path: String,
        shareType: String,
        title: String
    ): Intent {
        val file = File(path)
        val photoUri: Uri
        if (Build.VERSION.SDK_INT >= 24)
            photoUri = FileProvider.getUriForFile(
                context.applicationContext,
                BuildConfig.APPLICATION_ID + ".fileProvider",
                file
            )
        else
            photoUri = Uri.fromFile(file)

        val sharingIntent = Intent(Intent.ACTION_VIEW)
        sharingIntent.setDataAndType(photoUri, shareType)
        sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return sharingIntent
    }


    override fun onDestroy() {
        super.onDestroy()
        notificationManager?.cancel(NOTIF_ID)
        stopSelf()
    }

    fun sendMyBroadcastReceiver(progress: Int, completeString: String,itemPostion : Int) {
        val intent1 = Intent()
        intent1.action = AppConstants.Action
        intent1.putExtra(AppConstants.Progress, progress)
        intent1.putExtra(AppConstants.Complete, completeString)
        intent1.putExtra(AppConstants.ItemPostion,itemPostion)
        sendBroadcast(intent1)
    }

}
