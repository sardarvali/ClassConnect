package com.syed.classconnect.ui.attendance

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.syed.classconnect.databinding.FragmentAttendanceBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.DateUtils.todayIsoString
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AttendanceViewModel by viewModels()
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var classId: String
    private var countDownTimer: CountDownTimer? = null

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) openQrScanner() else showSnackbar("Camera permission required for QR scan")
    }

    companion object {
        fun newInstance(classId: String) = AttendanceFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        val uid = auth.currentUser?.uid ?: return

        viewModel.loadUserRole(uid)

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
            if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                binding.layoutTeacher.show()
                binding.layoutStudent.hide()
                setupTeacherView(uid)
            } else {
                binding.layoutTeacher.hide()
                binding.layoutStudent.show()
                setupStudentView(uid)
            }
        }

        viewModel.attendanceRecord.observe(viewLifecycleOwner) { record ->
            record ?: return@observe
            val qrData = JSONObject().apply {
                put("classId", classId)
                put("token", record.qrToken)
                put("date", record.date)
            }.toString()
            binding.ivQrCode.setImageBitmap(generateQr(qrData))
            binding.tvPresentCount.text = "Present: ${record.present.size}"
        }

        viewModel.markResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            if (result.isSuccess) showSnackbar(getString(com.syed.classconnect.R.string.attendance_marked))
            else showSnackbar(result.exceptionOrNull()?.message ?: "Failed")
        }

        // Attendance History
        setupHistory()
    }

    private fun setupHistory() {
        val historyAdapter = AttendanceHistoryAdapter()
        binding.rvHistory.adapter = historyAdapter

        viewModel.loadHistory(classId)

        viewModel.history.observe(viewLifecycleOwner) { records ->
            if (records.isEmpty()) {
                binding.layoutHistoryEmpty.show()
                binding.rvHistory.hide()
            } else {
                binding.layoutHistoryEmpty.hide()
                binding.rvHistory.show()
                historyAdapter.submitList(records)
            }
        }
    }

    private fun setupTeacherView(uid: String) {
        binding.btnStartSession.setOnClickListener {
            viewModel.startSession(classId, uid)
            binding.btnStartSession.hide()
            binding.layoutQr.show()
            startExpiryCountdown()
        }
        binding.btnEndSession.setOnClickListener {
            countDownTimer?.cancel()
            viewModel.endSession(classId, todayIsoString(), emptyList())
            binding.btnStartSession.show()
            binding.layoutQr.hide()
        }
    }

    private fun setupStudentView(uid: String) {
        binding.btnScanQr.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openQrScanner()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openQrScanner() {
        qrScanLauncher.launch(
            com.journeyapps.barcodescanner.ScanOptions().apply {
                setDesiredBarcodeFormats(com.journeyapps.barcodescanner.ScanOptions.QR_CODE)
                setPrompt(getString(com.syed.classconnect.R.string.scan_qr_instruction))
                setBeepEnabled(true)
                setOrientationLocked(true)
                setCameraId(0)
            }
        )
    }

    private val qrScanLauncher = registerForActivityResult(com.journeyapps.barcodescanner.ScanContract()) { result ->
        if (result.contents != null) {
            try {
                val json = JSONObject(result.contents)
                val scannedClassId = json.getString("classId")
                val token = json.getString("token")
                val date = json.getString("date")
                val uid = auth.currentUser?.uid ?: return@registerForActivityResult
                if (scannedClassId == classId) {
                    viewModel.markPresent(classId, date, uid)
                } else {
                    showSnackbar("Invalid QR code for this class")
                }
            } catch (e: Exception) {
                showSnackbar("Invalid QR code")
            }
        }
    }

    private fun startExpiryCountdown() {
        val expiryMs = Constants.QR_EXPIRY_MINUTES * 60 * 1000
        countDownTimer = object : CountDownTimer(expiryMs, 1000) {
            override fun onTick(ms: Long) {
                binding.tvExpiry.text = getString(com.syed.classconnect.R.string.qr_expires_in, "${ms / 1000}s")
            }
            override fun onFinish() {
                binding.tvExpiry.text = "QR Expired"
                binding.ivQrCode.alpha = 0.3f
            }
        }.start()
    }

    private fun generateQr(content: String): Bitmap {
        val writer = QRCodeWriter()
        val matrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                val isBlack: Boolean = matrix.get(x, y)
                bmp.setPixel(x, y, if (isBlack) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    override fun onDestroyView() { countDownTimer?.cancel(); super.onDestroyView(); _binding = null }
}

