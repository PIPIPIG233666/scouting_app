package com.pppig236.scoutingappredo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val permissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    private lateinit var tableRecyclerView: RecyclerView
    private var userList = ArrayList<User>()
    private lateinit var csvOperations: CSVOperations
    private lateinit var constants: Constants

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermissions()
        createCsv()

        val buttonScanner = findViewById<Button>(R.id.scanner_view)
        val tableView = findViewById<Button>(R.id.table_view)
        val fragment = ScannerFragment()
        val buttonDelete = findViewById<Button>(R.id.delete)

        showHide(buttonDelete)
        showHide(tableView)

        if (csvOperations.teamDataList.size / 3 != 0)
            buttonDelete.visibility = View.VISIBLE

        buttonScanner.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, fragment).commit()
            showHide(buttonScanner)
            showHide(tableRecyclerView)
            showHide(tableView)
            if (csvOperations.teamDataList.size / 3 != 0)
                buttonDelete.visibility = View.VISIBLE
        }

        tableView.setOnClickListener {
            supportFragmentManager.beginTransaction().remove(fragment)
                .commitAllowingStateLoss()
            showHide(buttonScanner)
            showHide(tableRecyclerView)
            showHide(tableView)
            userList.clear() // make sure there are no duplicates
            if (csvOperations.teamDataList.size / 3 != 0)
                buttonDelete.visibility = View.VISIBLE
        }

        buttonDelete.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Deleting data is permanent\nAre you sure?")
            builder.setPositiveButton("Yes") { _, _ ->
                // handle yes click
                // perform action if user selects "Yes"
                csvOperations.deleteCsv(constants.file)
                createCsv()
                userList.clear() // make sure there are no leftovers
                showHide(buttonDelete)
            }
            builder.setNegativeButton("No") { _, _ ->
                // handle no click
                // perform action if user selects "No"
            }
            val dialog = builder.create()
            dialog.show()
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

    private fun readCsvToList() {
        constants = Constants()
        csvOperations = CSVOperations()
        csvOperations.readCsv(constants.file)
        // add a while loop
        var i = 0
        var cnt = 0
        val lim = csvOperations.teamDataList.size / 3

        if (lim != 0) {
            do {
                userList.add(
                    User(
                        // Update this
                        csvOperations.teamDataList[i],
                        csvOperations.teamDataList[i + 1],
                        csvOperations.teamDataList[i + 2],
                    )
                )
                i += 3
                cnt++
            } while (cnt < lim)
        }
    }

    private fun createCsv() {
        constants = Constants()
        csvOperations = CSVOperations()
        tableRecyclerView = findViewById(R.id.table_recycler_view)

        val createdFile = constants.fileClass.exists()

        // read the csv from the saved file
        // and add it to the userList
        if (!createdFile) {
            csvOperations.createCsv(constants.file)
        }
    }
}