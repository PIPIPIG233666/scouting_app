package com.pppig236.scoutingappredo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback


class ScannerFragment : Fragment() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var csvOperations: CSVOperations
    private lateinit var constants: Constants

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, scannerView)
        csvOperations = CSVOperations()
        constants = Constants()

        var last = ""
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                val cur = it.text
                Toast.makeText(activity, cur, Toast.LENGTH_SHORT).show()
                val createdFile = constants.fileClass.exists()
                if (!createdFile) {
                    csvOperations.createCsv(constants.file)
                }
                if (last != cur) {
                    csvOperations.appendCsv(constants.file, "\n" + cur)
                    last = cur
                }
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}