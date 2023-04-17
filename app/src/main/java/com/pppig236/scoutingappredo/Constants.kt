package com.pppig236.scoutingappredo

import android.os.Environment
import java.io.File

class Constants {
    val file = Environment.getExternalStorageDirectory().path + "/data.csv"
    val fileClass = File(file)

    var matchCnt = 0
}