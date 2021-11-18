package com.catalystmedia.sourcecatalyst
import android.Manifest
import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import com.downloader.*
import kotlinx.android.synthetic.main.activity_documents.*
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.io.File
import java.lang.Error
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class DocumentsActivity : AppCompatActivity() {
    var PDFlink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documents)
        PRDownloader.initialize(applicationContext);
        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)
        getLink()
        checkFiles()
        cv_download.setOnClickListener {
            downloadPDF()
        }
        iv_back_docs.setOnClickListener {
            openFile()
        }
        iv_clear.setOnClickListener {
            clearFiles()
        }
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@DocumentsActivity, "Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onPermissionDenied(deniedPermissions: List<String?>) {
                Toast.makeText(
                    this@DocumentsActivity,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            )
            .check();

    }

    private fun checkFiles() {
        var dirPath = filesDir
        var filePath = "$dirPath/1.pdf"

        if (File(filePath).exists()) { tv_status_down.text = "Not Downloaded"
            tv_status_down.setTextColor(Color.parseColor("#A0A0A0"))
            frame1.visibility = View.GONE
        }
        else{
            frame1.visibility = View.VISIBLE
            tv_status_down.text = "Downloaded"
            tv_status_down.setTextColor(Color.parseColor("#609A1C"))
        }
    }

    private fun clearFiles() {
        var dirPath = filesDir
        var filePath = "$dirPath/1.pdf"

        if (File(filePath).exists()) {
          File(filePath).delete()
            tv_status_down.text = "Not Downloaded"
            frame1.visibility = View.GONE
            tv_status_down.setTextColor(Color.parseColor("#A0A0A0"))
        }
    }


    private fun openFile() {
        var dirPath = filesDir
        var filePath = "$dirPath/1.pdf"
        val intent = Intent(this@DocumentsActivity, PDFActivity::class.java)
        intent.putExtra("uri", filePath)
        startActivity(intent)
    }


    private fun getLink() {
        //get link from database
        PDFlink = "https://drive.google.com/uc?export=download&id=1oUsaJx8WQPeWC7dHVYvdgQbmM4le1swL"

    }


    private fun downloadPDF() {
        circularProgressBar.visibility = View.GONE
        val url = PDFlink
        var dirPath = filesDir
        var filePath = "$dirPath/1.pdf"

        if (File(filePath).exists()) {
            frame1.visibility = View.GONE
            openFile()
        } else {
            frame1.visibility = View.VISIBLE
            PRDownloader.download(url, dirPath.toString(), "1.pdf")
                .build()
                .setOnStartOrResumeListener {
                }
                .setOnPauseListener { }
                .setOnCancelListener {
                }
                .setOnProgressListener { progress ->
                    icon_cloud.visibility = View.GONE
                    circularProgressBar.visibility = View.VISIBLE
                    tv_num_prog_1.visibility = View.VISIBLE
                    var currentVal = progress.currentBytes * 100 / progress.totalBytes
                    var numProg = currentVal.toFloat()
                    circularProgressBar.progress = numProg
                    var numString = numProg.toInt().toString()
                    tv_num_prog_1.text = "$numString%"
                    if (numProg >= 100F) {
                        frame1.visibility  = View.GONE
                        tv_status_down.text = "Downloaded"
                        tv_status_down.setTextColor(Color.parseColor("#609A1C"))
                    }
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        Toast.makeText(
                            this@DocumentsActivity,
                            "Download Complete",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(error: com.downloader.Error?) {
                        Toast.makeText(
                            this@DocumentsActivity,
                            "Download $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }

    }
}
