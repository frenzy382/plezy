package com.edde746.plezy.exoplayer

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TrueHdOverrideTest {
  @After
  fun reset() = TrueHdOverride.setMode(TrueHdOverride.AUTO)

  @Test
  fun autoLeavesTheProbedListAlone() {
    assertEquals("ac3,eac3", withTrueHdOverride("ac3,eac3", forced = false))
    assertEquals("", withTrueHdOverride("", forced = false))
  }

  @Test
  fun forcedAddsTrueHdOnce() {
    assertEquals("ac3,eac3,truehd", withTrueHdOverride("ac3,eac3", forced = true))
    assertEquals("truehd", withTrueHdOverride("", forced = true))
    assertEquals("ac3,truehd,dts", withTrueHdOverride("ac3,truehd,dts", forced = true))
  }

  @Test
  fun unknownModesFallBackToAuto() {
    TrueHdOverride.setMode(7)
    assertEquals(TrueHdOverride.AUTO, TrueHdOverride.mode)
    assertFalse(TrueHdOverride.forced)
    TrueHdOverride.setMode(TrueHdOverride.RAW)
    assertTrue(TrueHdOverride.forced)
    assertEquals("forced-raw", TrueHdOverride.describe())
  }
}
