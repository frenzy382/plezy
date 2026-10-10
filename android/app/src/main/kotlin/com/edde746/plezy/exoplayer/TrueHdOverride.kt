package com.edde746.plezy.exoplayer

/**
 * Fork-only user override for Dolby TrueHD routing.
 *
 * Stock Plezy bitstreams TrueHD only when the route probes report the 192kHz/7.1 IEC 61937
 * carrier (and media3's passthrough check) as supported. Some TVs and projectors (seen on an
 * XGIMI Horizon 20 Pro feeding a soundbar over eARC) answer "no" while other apps bitstream
 * TrueHD to the same sink without trouble, so Plezy silently decodes it to PCM instead.
 *
 * - [AUTO]: stock behaviour, route probes decide.
 * - [CARRIER]: skip the probes and always pack TrueHD into the IEC carrier (Kodi's transport).
 * - [RAW]: skip the probes and hand the raw TrueHD stream to the platform sink (media3's
 *   stock transport, what most other Android players do).
 *
 * Neither forced mode overrides the user's own blocks (passthrough off, downmix, loudness
 * normalization) or the recovery path that demotes TrueHD to decoding after an AudioTrack failure.
 *
 * Set from Dart over [CHANNEL] before each playback starts.
 */
object TrueHdOverride {
  const val CHANNEL = "com.plezy/fork_audio"

  const val AUTO = 0
  const val CARRIER = 1
  const val RAW = 2

  @Volatile
  var mode: Int = AUTO
    private set

  val forced: Boolean get() = mode != AUTO

  fun setMode(value: Int) {
    mode = if (value == CARRIER || value == RAW) value else AUTO
  }

  fun describe(): String = when (mode) {
    CARRIER -> "forced-carrier"
    RAW -> "forced-raw"
    else -> "auto"
  }
}
