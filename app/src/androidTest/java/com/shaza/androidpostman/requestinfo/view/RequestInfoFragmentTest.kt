package com.shaza.androidpostman.requestinfo.view

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaza.androidpostman.R
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.requestInfo.view.RequestInfoFragment
import com.shaza.androidpostman.requestInfo.viewmodel.RequestInfoViewModel
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.utils.DataIdlingResource
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

@RunWith(AndroidJUnit4::class)
class RequestInfoFragmentTest {

    private lateinit var fragmentScenario: FragmentScenario<RequestInfoFragment>

    private lateinit var mockViewModel: RequestInfoViewModel

    private lateinit var mockNetworkResponse: NetworkResponse

    @Before
    fun setUp() {
        mockViewModel = mockk<RequestInfoViewModel>()
        val mockNetworkResponseLiveData = MutableLiveData<NetworkResponse>()
        mockNetworkResponse = mockk()
        val bundle = Bundle().apply {
            putParcelable("networkResponse", mockNetworkResponse)
        }
        every { mockViewModel.networkResponse } returns mockNetworkResponseLiveData

        // Set up NetworkResponse properties
        every { mockNetworkResponse.url } returns "https://example.com"
        every { mockNetworkResponse.requestType } returns RequestType.GET
        every { mockNetworkResponse.queryParameters } returns mapOf("key" to "value")
        every { mockNetworkResponse.requestHeaders } returns mapOf("Authorization" to "Bearer token")
        every { mockNetworkResponse.responseHeaders } returns mapOf("Content-Type" to "application/json")
        every { mockNetworkResponse.body } returns "{ \"key\": \"value\" }"
        every { mockNetworkResponse.responseCode } returns 200
        every { mockNetworkResponse.elapsedTime } returns 123L
        every { mockNetworkResponse.response } returns "Response"
        every { mockNetworkResponse.error } returns null

        // Set up ViewModel method behavior
        every { mockViewModel.extractData(any()) } answers {
            mockNetworkResponseLiveData.postValue(mockNetworkResponse)
        }


        fragmentScenario = FragmentScenario.launchInContainer(
            RequestInfoFragment::class.java,
            fragmentArgs = Bundle().apply {
                putParcelable("networkResponse", mockNetworkResponse)
            },
            themeResId = R.style.Theme_AndroidPostman
        )

        Thread.sleep(1000)

    }

    @Test
    fun testFragmentIsVisible() {
        onView(withId(R.id.request_info_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun makeSureTheDataIsShowing() {

        val liveData = MutableLiveData<NetworkResponse>()
        val idlingResource = DataIdlingResource(liveData)

        IdlingRegistry.getInstance().register(idlingResource)
        liveData.postValue(mockNetworkResponse)

        onView(withId(R.id.request_type)).check(matches(withText("GET")))

        IdlingRegistry.getInstance().unregister(idlingResource)
    }

}