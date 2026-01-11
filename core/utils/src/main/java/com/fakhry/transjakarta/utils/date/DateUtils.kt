package com.fakhry.transjakarta.utils.date

import android.os.Build
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    fun formatUpdatedAt(updatedAt: String): String {
        if (updatedAt.isBlank()) return "Unknown"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formatUpdatedAtOreo(updatedAt)
        } else {
            formatUpdatedAtLegacy(updatedAt)
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun formatUpdatedAtOreo(updatedAt: String): String = try {
        // ISO_OFFSET_DATE_TIME handles '2017-08-14T16:04:44-04:00' and '...Z'
        // Using DateTimeFormatter.ISO_DATE_TIME is safer as it covers offset
        // and no-offset (local)
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val parsed = ZonedDateTime.parse(updatedAt, formatter)

        // Convert to system default zone
        val systemZoneDate = parsed.withZoneSameInstant(ZoneId.systemDefault())

        // Format to readable string in default locale
        val outputFormatter = DateTimeFormatter.ofPattern(
            "MMM dd, HH:mm:ss",
            Locale.getDefault(),
        )
        systemZoneDate.format(outputFormatter)
    } catch (e: Exception) {
        e.printStackTrace()
        updatedAt
    }

    private fun formatUpdatedAtLegacy(updatedAt: String): String {
        // Legacy fallback
        val inputPatterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ssXXX", // ISO 8601 with time zone (e.g., -04:00)
            "yyyy-MM-dd'T'HH:mm:ssX", // ISO 8601 with time zone (e.g., -04)
            "yyyy-MM-dd'T'HH:mm:ss", // No time zone
        )

        for (pattern in inputPatterns) {
            try {
                val parser = SimpleDateFormat(pattern, Locale.getDefault())
                // If the pattern doesn't process timezone (no 'X'), assume UTC if not specified
                // But generally the input should have it.
                val date = parser.parse(updatedAt) ?: continue

                val formatter = SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault())
                formatter.timeZone = TimeZone.getDefault()

                return formatter.format(date)
            } catch (_: ParseException) {
                continue
            }
        }
        return updatedAt
    }
}
