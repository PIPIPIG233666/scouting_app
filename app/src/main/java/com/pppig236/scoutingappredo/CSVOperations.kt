package com.pppig236.scoutingappredo

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

class CSVOperations {

    var teamDataList = ArrayList<String>()

    fun createCsv(fileName: String) {
        val file = File(fileName)

        // create a new file with headers
        // Update this
        file.writeText("Match Number,Alliance,Team Number,Starting Location,Auto High Cone,Auto Mid Cone,Auto Low Cone,Auto High Cube,Auto Mid Cube,Auto Low Cube,Auto Charge Station,Mobility,Tele High Cone,Tele Mid Cone,Tele Low Cone,Tele High Cube,Tele Mid Cube,Tele Low Cube,Links,Tele Charge Station,Penalties,Robot Breakdown,Robot Tipped,Defense,Comment")
    }

    fun appendCsv(fileName: String, text: String) {
        val file = File(fileName)

        file.appendText(text)
    }

    fun readCsv(fileName: String) {
        val file = File(fileName)
        if (file.exists()) {
            val rows: List<Map<String, String>> = csvReader().readAllWithHeader(file)
            for (column in rows) {
                column["Match Number"]?.let { teamDataList.add(it) }
                column["Team Number"]?.let { teamDataList.add(it) }
            }
        }
    }
}