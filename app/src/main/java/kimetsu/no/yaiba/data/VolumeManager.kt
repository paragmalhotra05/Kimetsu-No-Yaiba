package kimetsu.no.yaiba.data

import android.content.Context
import android.content.SharedPreferences
import kimetsu.no.yaiba.models.Volume

class VolumeManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("manga_reader_prefs", Context.MODE_PRIVATE)

    fun getAllVolumes(): List<Volume> {
        return listOf(
            Volume(
                id = 1,
                volumeNumber = 1,
                title = "Cruelty",
                description = "Tanjiro sets out on a journey to find a cure for his sister Nezuko and avenge his family.",
                coverImageUrl = "volume_1_cover",
                totalPages = 192,
                pdfFileName = "kimetsu_volume_01.pdf",
                releaseDate = "June 3, 2016"
            ),
            Volume(
                id = 2,
                volumeNumber = 2,
                title = "It was you",
                description = "Tanjiro encounters a mysterious man who might know more about the demons.",
                coverImageUrl = "volume_2_cover",
                totalPages = 192,
                pdfFileName = "kimetsu_volume_02.pdf",
                releaseDate = "August 4, 2016"
            ),
            Volume(
                id = 3,
                volumeNumber = 3,
                title = "Believe in yourself",
                description = "Tanjiro and Nezuko face more powerful demons as they continue their journey.",
                coverImageUrl = "volume_3_cover",
                totalPages = 192,
                pdfFileName = "kimetsu_volume_03.pdf",
                releaseDate = "October 4, 2016"
            )
        )
    }

    fun saveProgress(volumeId: Int, pageIndex: Int) {
        prefs.edit().putInt("progress_${volumeId}_page", pageIndex).apply()
        val totalPages = getAllVolumes().find { it.id == volumeId }?.totalPages ?: 1
        val percent = (pageIndex.toFloat() / (totalPages - 1) * 100).toInt()
        prefs.edit().putInt("progress_${volumeId}_percent", percent).apply()
    }

    fun getProgress(volumeId: Int): Int {
        return prefs.getInt("progress_${volumeId}_page", 0)
    }

    fun getProgressPercent(volumeId: Int): Int {
        return prefs.getInt("progress_${volumeId}_percent", 0)
    }

    fun saveReadingMode(mode: String) {
        prefs.edit().putString("reading_mode", mode).apply()
    }

    fun getReadingMode(): String {
        return prefs.getString("reading_mode", "DAY") ?: "DAY"
    }
}
