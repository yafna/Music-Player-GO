package com.iven.musicplayergo.extensions

import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.provider.MediaStore
import com.iven.musicplayergo.R
import com.iven.musicplayergo.models.Music
import com.iven.musicplayergo.models.SavedMusic
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun Long.toContentUri(): Uri = ContentUris.withAppendedId(
    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    this
)

fun Uri.toBitrate(context: Context): Pair<Int, Int>? {
    val mediaExtractor = MediaExtractor()
    return try {

        mediaExtractor.setDataSource(context, this, null)

        val mediaFormat = mediaExtractor.getTrackFormat(0)

        val sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        //get bitrate in bps, divide by 1000 to get Kbps
        val bitrate = mediaFormat.getInteger(MediaFormat.KEY_BIT_RATE) / 1000
        Pair(sampleRate, bitrate)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun IntRange.getRandom() = Random.nextInt(start, endInclusive + 1)

fun Long.toFormattedDuration(isAlbum: Boolean) = try {

    val defaultFormat = if (isAlbum) "%02dm:%02ds" else "%02d:%02d"

    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this)

    if (minutes < 60) String.format(
        Locale.getDefault(), defaultFormat,
        minutes,
        seconds - TimeUnit.MINUTES.toSeconds(minutes)
    ) else
    //https://stackoverflow.com/a/9027379
        String.format(
            "%02dh:%02dm",
            hours,
            minutes - TimeUnit.HOURS.toMinutes(hours), // The change is in this line
            seconds - TimeUnit.MINUTES.toSeconds(minutes)
        )

} catch (e: Exception) {
    e.printStackTrace()
    ""
}

fun Int.toFormattedTrack() = try {
    if (this >= 1000) this % 1000 else this
} catch (e: Exception) {
    e.printStackTrace()
    0
}

fun Int.toFormattedYear(resources: Resources) =
    if (this != 0) toString() else resources.getString(R.string.unknown_year)

fun Music.toSavedMusic(playerPosition: Int, isPlayingFromFolder: Boolean) =
    SavedMusic(
        artist,
        title,
        displayName,
        year,
        playerPosition,
        duration,
        album,
        isPlayingFromFolder
    )
