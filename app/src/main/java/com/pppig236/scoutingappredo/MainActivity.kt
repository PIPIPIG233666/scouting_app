package com.pppig236.scoutingappredo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    private lateinit var csvOperations: CSVOperations
    private lateinit var constants: Constants

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermissions()
        createCsv()

        val buttonScanner = findViewById<Button>(R.id.scanner_view)
        val buttonHome = findViewById<Button>(R.id.home_view)
        val fragment = ScannerFragment()

        buttonScanner.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
            showHide(buttonScanner)
            showHide(buttonHome)
        }
        buttonHome.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            csvOperations.readCsv(constants.file)
            if (csvOperations.teamDataList.size % 6 == 0){

                builder.setMessage("All six teams' results from match#" + csvOperations.teamDataList.size/6/2+" are collected, tap again to continue")

                    supportFragmentManager.beginTransaction().remove(fragment)
                        .commitAllowingStateLoss()
                    csvOperations.teamDataList.clear()
                    showHide(buttonHome)
                    showHide(buttonScanner)
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    private fun initPermissions() {
        // Initialize a list of required permissions to request runtime
        val list = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this, list, permissionsRequestCode)

        managePermissions.checkPermissions()
        if (Environment.isExternalStorageManager()) {
            // If you don't have access, launch a new activity to show the user the system's dialog
            // to allow access to the external storage
        } else {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            val uri: Uri = Uri.fromParts("package", this.packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun createCsv() {
        constants = Constants()
        csvOperations = CSVOperations()

        val createdFile = constants.fileClass.exists()

        // read the csv from the saved file
        // and add it to the userList
        if (!createdFile) {
            csvOperations.createCsv(constants.file)
        }
    }
}