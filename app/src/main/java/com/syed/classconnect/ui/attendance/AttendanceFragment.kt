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
<<<<<<< HEAD
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
=======
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.syed.classconnect.data.model.AttendanceRecord
import com.syed.classconnect.R
>>>>>>> final
import com.syed.classconnect.databinding.FragmentAttendanceBinding
import com.syed.classconnect.util.Constants
import com.syed.classconnect.util.DateUtils.todayIsoString
import com.syed.classconnect.util.hide
import com.syed.classconnect.util.show
import com.syed.classconnect.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject
<<<<<<< HEAD
=======
import java.util.Locale
import kotlin.math.max
>>>>>>> final

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AttendanceViewModel by viewModels()
<<<<<<< HEAD
    @Inject lateinit var auth: FirebaseAuth
    private lateinit var classId: String
    private var countDownTimer: CountDownTimer? = null

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) openQrScanner() else showSnackbar("Camera permission required for QR scan")
    }
=======
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var classId: String
    private var countDownTimer: CountDownTimer? = null
    private var activeSessionToken: String? = null

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openQrScanner() else showSnackbar("Camera permission required for QR scan")
        }
>>>>>>> final

    companion object {
        fun newInstance(classId: String) = AttendanceFragment().apply {
            arguments = Bundle().apply { putString(Constants.EXTRA_CLASS_ID, classId) }
        }
    }

<<<<<<< HEAD
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
=======
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
>>>>>>> final
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classId = arguments?.getString(Constants.EXTRA_CLASS_ID) ?: return
        val uid = auth.currentUser?.uid ?: return

        viewModel.loadUserRole(uid)

        viewModel.userRole.observe(viewLifecycleOwner) { role ->
<<<<<<< HEAD
            if (role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN) {
                binding.layoutTeacher.show()
                binding.layoutStudent.hide()
                setupTeacherView(uid)
            } else {
                binding.layoutTeacher.hide()
                binding.layoutStudent.show()
                setupStudentView(uid)
=======
            val isTeacher = role == Constants.ROLE_TEACHER || role == Constants.ROLE_ADMIN

            if (isTeacher) {
                binding.teacherPanel.show()
                binding.studentPanel.hide()
                setupTeacherView(uid)
            } else {
                binding.teacherPanel.hide()
                binding.studentPanel.show()
                setupStudentView()
>>>>>>> final
            }
        }

        viewModel.attendanceRecord.observe(viewLifecycleOwner) { record ->
<<<<<<< HEAD
            record ?: return@observe
=======
            if (record == null) {
                activeSessionToken = null
                binding.activeSessionCard.hide()
                binding.btnStartSession.show()
                return@observe
            }

>>>>>>> final
            val qrData = JSONObject().apply {
                put("classId", classId)
                put("token", record.qrToken)
                put("date", record.date)
            }.toString()
<<<<<<< HEAD
            binding.ivQrCode.setImageBitmap(generateQr(qrData))
            binding.tvPresentCount.text = "Present: ${record.present.size}"
=======

            binding.ivQrCode.setImageBitmap(generateQr(qrData))
            binding.tvPresentCount.text = getString(R.string.present_label, record.present.size)
            binding.btnStartSession.hide()
            binding.activeSessionCard.show()

            if (activeSessionToken != record.qrToken) {
                activeSessionToken = record.qrToken
                startExpiryCountdown(record.qrExpiresAt.seconds * 1000)
            }
>>>>>>> final
        }

        viewModel.markResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
<<<<<<< HEAD
            if (result.isSuccess) showSnackbar(getString(com.syed.classconnect.R.string.attendance_marked))
            else showSnackbar(result.exceptionOrNull()?.message ?: "Failed")
        }

        // Attendance History
        setupHistory()
    }

    private fun setupHistory() {
        val historyAdapter = AttendanceHistoryAdapter()
        binding.rvHistory.adapter = historyAdapter
=======
            if (result.isSuccess) showSnackbar(getString(R.string.attendance_marked))
            else showSnackbar(result.exceptionOrNull()?.message ?: "Failed")
        }

        viewModel.attendanceError.observe(viewLifecycleOwner) { message ->
            message?.let {
                showSnackbar(it)
                viewModel.clearAttendanceError()
            }
        }

        viewModel.timingAlertMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                showTimingAlertDialog(it)
                viewModel.clearTimingAlert()
            }
        }

        // Attendance History
        setupHistory(uid)
    }

    private fun setupHistory(uid: String) {
        val historyAdapter = AttendanceHistoryAdapter()
        binding.historyRecyclerView.adapter = historyAdapter
>>>>>>> final

        viewModel.loadHistory(classId)

        viewModel.history.observe(viewLifecycleOwner) { records ->
            if (records.isEmpty()) {
<<<<<<< HEAD
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
=======
                binding.emptyHistoryState.show()
                binding.historyRecyclerView.hide()
            } else {
                binding.emptyHistoryState.hide()
                binding.historyRecyclerView.show()
                historyAdapter.submitList(records)
            }
            updateAttendanceRate(records, uid)
        }
    }

    private fun updateAttendanceRate(records: List<AttendanceRecord>, uid: String) {
        val totalSessions = records.size
        val attendedSessions = records.count { uid in it.present }
        val rate = if (totalSessions > 0) attendedSessions * 100 / totalSessions else 0
        binding.tvAttendanceRate.text = String.format(Locale.getDefault(), "%d%%", rate)
    }

    private fun setupTeacherView(uid: String) {
        binding.btnStartSession.setOnClickListener {
            viewModel.startSession(classId, uid)
        }
        binding.btnEndSession.setOnClickListener {
            countDownTimer?.cancel()
            val date = viewModel.attendanceRecord.value?.date ?: todayIsoString()
            viewModel.endSession(classId, date, emptyList())
            binding.btnStartSession.show()
            binding.activeSessionCard.hide()
            activeSessionToken = null
        }
    }

    private fun setupStudentView() {
        binding.btnScanQr.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
>>>>>>> final
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
<<<<<<< HEAD
                setPrompt(getString(com.syed.classconnect.R.string.scan_qr_instruction))
                setBeepEnabled(true)
                setOrientationLocked(true)
=======
                setPrompt(getString(R.string.scan_qr_instruction))
                setBeepEnabled(true)
                setOrientationLocked(false)
>>>>>>> final
                setCameraId(0)
            }
        )
    }

<<<<<<< HEAD
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
=======
    private val qrScanLauncher =
        registerForActivityResult(com.journeyapps.barcodescanner.ScanContract()) { result ->
            if (result.contents != null) {
                try {
                    val json = JSONObject(result.contents)
                    val scannedClassId = json.getString("classId")
                    json.getString("token")
                    val date = json.getString("date")
                    val uid = auth.currentUser?.uid ?: return@registerForActivityResult
                    if (scannedClassId == classId) {
                        viewModel.markPresent(classId, date, uid)
                    } else {
                        showSnackbar("Invalid QR code for this class")
                    }
                } catch (_: Exception) {
                    showSnackbar("Invalid QR code")
                }
            }
        }

    private fun startExpiryCountdown(expiresAtMillis: Long) {
        countDownTimer?.cancel()
        val remainingMillis = max(0L, expiresAtMillis - System.currentTimeMillis())
        if (remainingMillis == 0L) {
            binding.tvTimer.text = getString(R.string.qr_expired)
            return
        }

        countDownTimer = object : CountDownTimer(remainingMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val mins = millisUntilFinished / 1000 / 60
                val secs = (millisUntilFinished / 1000) % 60
                binding.tvTimer.text = getString(
                    R.string.qr_expires_in,
                    String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
                )
            }

            override fun onFinish() {
                binding.tvTimer.text = getString(R.string.qr_expired)
                val date = viewModel.attendanceRecord.value?.date ?: todayIsoString()
                viewModel.endSession(classId, date, emptyList())
                binding.btnStartSession.show()
                binding.activeSessionCard.hide()
                activeSessionToken = null
>>>>>>> final
            }
        }.start()
    }

    private fun generateQr(content: String): Bitmap {
        val writer = QRCodeWriter()
        val matrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
<<<<<<< HEAD
        val bmp = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                val isBlack: Boolean = matrix.get(x, y)
                bmp.setPixel(x, y, if (isBlack) Color.BLACK else Color.WHITE)
=======
        val bmp = createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                val isBlack = matrix[x, y]
                bmp[x, y] = if (isBlack) Color.BLACK else Color.WHITE
>>>>>>> final
            }
        }
        return bmp
    }

<<<<<<< HEAD
    override fun onDestroyView() { countDownTimer?.cancel(); super.onDestroyView(); _binding = null }
}

=======
    private fun showTimingAlertDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.attendance_time_restriction_title))
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    override fun onDestroyView() {
        countDownTimer?.cancel(); super.onDestroyView(); _binding = null
    }
}
>>>>>>> final
