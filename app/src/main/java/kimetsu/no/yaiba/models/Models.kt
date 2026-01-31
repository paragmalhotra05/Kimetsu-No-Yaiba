package kimetsu.no.yaiba.models

import java.io.Serializable

data class Volume(
    val id: Int,
    val volumeNumber: Int,
    val title: String,
    val description: String,
    val coverImageUrl: String,
    val totalPages: Int,
    val pdfFileName: String,
    val releaseDate: String
) : Serializable

data class Bookmark(
    val volumeId: Int,
    val pageIndex: Int,
    val timestamp: Long,
    val note: String = ""
)

enum class ReadingMode {
    DAY,
    NIGHT,
    SEPIA
}
