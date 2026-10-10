import 'dart:io';

import 'package:flutter/services.dart';

import '../utils/app_logger.dart';

/// Fork-only: how Dolby TrueHD reaches the audio output on Android.
///
/// Stock Plezy bitstreams TrueHD only when the route probes say the HDMI
/// output accepts it, and otherwise decodes it to PCM. Some TVs and
/// projectors answer "no" while other apps bitstream TrueHD to the same
/// receiver or soundbar fine, so TrueHD plays as stereo/PCM in Plezy only.
///
/// Mirrors `TrueHdOverride` on the Kotlin side; the int values must match.
enum TrueHdOverrideMode {
  /// Stock behaviour: the route probes decide.
  auto(0, 'Automatic', 'Plezy checks whether the HDMI output accepts TrueHD'),

  /// Always pack TrueHD into the 192kHz/7.1 IEC carrier (Kodi's method).
  carrier(1, 'Force bitstream (carrier)', 'Kodi-style MAT carrier. Try this first'),

  /// Hand the raw TrueHD stream to Android, as most other players do.
  raw(2, 'Force bitstream (raw)', 'Raw TrueHD track. Try if carrier gives silence');

  const TrueHdOverrideMode(this.nativeValue, this.label, this.description);

  final int nativeValue;
  final String label;
  final String description;

  static const _channel = MethodChannel('com.plezy/fork_audio');

  /// Pushes [mode] to the native players. Call before playback configures
  /// audio, so both the ExoPlayer sink and mpv's audio-spdif list see it.
  static Future<void> apply(TrueHdOverrideMode mode) async {
    if (!Platform.isAndroid) return;
    try {
      await _channel.invokeMethod<String>('setTrueHdOverride', {'mode': mode.nativeValue});
    } catch (error, stackTrace) {
      appLogger.w('Fork: failed to apply TrueHD override', error: error, stackTrace: stackTrace);
    }
  }
}
