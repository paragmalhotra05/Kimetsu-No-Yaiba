package kimetsu.no.yaiba

import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kimetsu.no.yaiba.data.VolumeManager
import kimetsu.no.yaiba.databinding.ActivityReaderBinding
import kimetsu.no.yaiba.models.ReadingMode
import kimetsu.no.yaiba.models.Volume
import com.rajat.pdfviewer.PdfRendererView
import java.io.File
import java.io.FileOutputStream

class ReaderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReaderBinding
    private lateinit var volumeManager: VolumeManager
    private var currentVolume: Volume? = null
    private var isControlsVisible = true
    private var currentReadingMode = ReadingMode.DAY
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent screenshots and screen recording for content protection
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        volumeManager = VolumeManager(this)
        val volumeId = intent.getIntExtra("volume_id", -1)
        currentVolume = volumeManager.getAllVolumes().find { it.id == volumeId }

        if (currentVolume == null) {
            finish()
            return
        }

        currentReadingMode = ReadingMode.valueOf(volumeManager.getReadingMode())

        setupGestureDetector()
        setupUI()
        loadPDF()
        applyReadingMode()
    }

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                toggleControls()
                return true
            }
        })

        // Intercept touches on the overlay or view
        binding.controlsOverlay.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            // Return false to allow touches to pass through if controls are hidden?
            // Actually, if visible, we want to handle them.
            isControlsVisible
        }

        // We can't easily set it on PdfRendererView because it has its own touch handling
        // But we can try to wrap it or use the statusListener's onTap if it existed
    }

    private fun setupUI() {
        binding.readerToolbar.title = "Volume ${currentVolume?.volumeNumber}"
        binding.readerToolbar.setNavigationOnClickListener { finish() }

        binding.pageSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.pdfView.jumpToPage(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.modeButton.setOnClickListener {
            cycleReadingMode()
        }

        binding.bookmarkButton.setOnClickListener {
            addBookmark()
        }

        binding.pdfView.statusListener = object : PdfRendererView.StatusCallBack {
            override fun onPageChanged(currentPage: Int, totalPage: Int) {
                // Library returns 1-based page number in onPageChanged (confirmed via source)
                this@ReaderActivity.onPageChanged(currentPage - 1, totalPage)
            }
            override fun onError(error: Throwable) {
                Toast.makeText(this@ReaderActivity, "Error loading content", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPDF() {
        val volume = currentVolume ?: return

        try {
            val file = File(cacheDir, volume.pdfFileName)
            if (!file.exists()) {
                assets.open(volume.pdfFileName).use { inputStream ->
                    FileOutputStream(file).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            }

            binding.pdfView.initWithFile(file)

            val lastPage = volumeManager.getProgress(volume.id)
            if (lastPage > 0) {
                binding.pdfView.postDelayed({
                    binding.pdfView.jumpToPage(lastPage)
                }, 500)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading manga content", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onPageChanged(page: Int, pageCount: Int) {
        binding.pageNumberText.text = "${page + 1} / $pageCount"
        binding.pageSeekBar.max = pageCount - 1
        binding.pageSeekBar.progress = page

        currentVolume?.let {
            volumeManager.saveProgress(it.id, page)
        }
    }

    private fun cycleReadingMode() {
        currentReadingMode = when (currentReadingMode) {
            ReadingMode.DAY -> ReadingMode.NIGHT
            ReadingMode.NIGHT -> ReadingMode.SEPIA
            ReadingMode.SEPIA -> ReadingMode.DAY
        }
        volumeManager.saveReadingMode(currentReadingMode.name)
        applyReadingMode()
    }

    private fun applyReadingMode() {
        when (currentReadingMode) {
            ReadingMode.DAY -> {
                binding.readerRoot.setBackgroundColor(Color.WHITE)
                binding.pdfView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
            ReadingMode.NIGHT -> {
                binding.readerRoot.setBackgroundColor(Color.BLACK)
                // Apply color inversion for Night Mode
                val matrix = ColorMatrix(floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                ))
                val filter = ColorMatrixColorFilter(matrix)
                val paint = Paint().apply { colorFilter = filter }
                binding.pdfView.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
            }
            ReadingMode.SEPIA -> {
                binding.readerRoot.setBackgroundColor(Color.parseColor("#F4ECD8"))
                // Apply sepia-like filter
                val matrix = ColorMatrix()
                matrix.setScale(1f, 0.95f, 0.82f, 1.0f)
                val filter = ColorMatrixColorFilter(matrix)
                val paint = Paint().apply { colorFilter = filter }
                binding.pdfView.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
            }
        }
    }

    private fun addBookmark() {
        Toast.makeText(this, "Bookmarked current page", Toast.LENGTH_SHORT).show()
    }

    private fun toggleControls() {
        isControlsVisible = !isControlsVisible
        binding.controlsOverlay.visibility = if (isControlsVisible) View.VISIBLE else View.GONE
        if (isControlsVisible) {
            showSystemUI()
        } else {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}
