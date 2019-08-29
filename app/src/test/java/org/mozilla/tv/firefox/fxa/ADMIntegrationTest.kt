/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.tv.firefox.fxa

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.reactivex.observers.TestObserver
import mozilla.components.concept.sync.Device
import mozilla.components.concept.sync.TabData
import mozilla.components.service.fxa.manager.FxaAccountManager
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mozilla.tv.firefox.fxa.ADMIntegration.ReceivedTabs
import org.mozilla.tv.firefox.helpers.FirefoxRobolectricTestRunner

@RunWith(FirefoxRobolectricTestRunner::class)
class ADMIntegrationTest {

    private lateinit var admIntegration: ADMIntegration
    private lateinit var receivedTabsRawTestObs: TestObserver<ReceivedTabs>

    @MockK private lateinit var accountManager: FxaAccountManager
    private var capturedOnTabsReceived: OnTabsReceivedCallback? = null

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        val sendTabsFeatureProvider: SendTabFeatureProvider = { _, _, onTabsReceived ->
            capturedOnTabsReceived = onTabsReceived
            mockk()
        }

        admIntegration = ADMIntegration(mockk(relaxed = true), sendTabsFeatureProvider).also {
            receivedTabsRawTestObs = it.receivedTabsRaw.test()
        }
    }

    @Test
    fun `GIVEN createSendTabFeature is called WHEN the onTabsReceived callback is called with non-null values THEN receivedTabsRaw displays those values`() {
        givenCreateSendTabFeatureIsCalled()

        val expectedDevice = mockk<Device>()
        val expectedTabData = listOf(mockk<TabData>())
        val expectedReceivedTabs = ReceivedTabs(
            device = expectedDevice,
            tabData = expectedTabData
        )

        capturedOnTabsReceived!!.invoke(expectedDevice, expectedTabData)

        receivedTabsRawTestObs.assertValues(expectedReceivedTabs)
    }

    @Test
    fun `GIVEN createSendTabFeature is called WHEN the onTabsReceived callback is called with a null device THEN receivedTabsRaw displays those values`() {
        givenCreateSendTabFeatureIsCalled()

        val expectedTabData = listOf(mockk<TabData>())
        val expectedReceivedTabs = ReceivedTabs(
            device = null,
            tabData = expectedTabData
        )

        capturedOnTabsReceived!!.invoke(null, expectedTabData)

        receivedTabsRawTestObs.assertValues(expectedReceivedTabs)
    }

    @Test
    fun `GIVEN createSendTabFeature is called WHEN the onTabsReceived callback is called with an empty tabData list THEN receivedTabsRaw displays those values`() {
        givenCreateSendTabFeatureIsCalled()

        val expectedDevice = mockk<Device>()
        val expectedReceivedTabs = ReceivedTabs(
            device = expectedDevice,
            tabData = emptyList()
        )

        capturedOnTabsReceived!!.invoke(expectedDevice, emptyList())

        receivedTabsRawTestObs.assertValues(expectedReceivedTabs)
    }

    private fun givenCreateSendTabFeatureIsCalled() {
        admIntegration.createSendTabFeature(accountManager, mockk())
        assertNotNull(capturedOnTabsReceived) // sanity check.
    }
}
