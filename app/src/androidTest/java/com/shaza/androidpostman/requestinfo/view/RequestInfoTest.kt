package com.shaza.androidpostman.requestinfo.view

import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shaza.androidpostman.R
import com.shaza.androidpostman.home.model.RequestType
import com.shaza.androidpostman.requestInfo.view.RequestInfoFragment
import com.shaza.androidpostman.requestInfo.viewmodel.RequestInfoViewModel
import com.shaza.androidpostman.shared.model.NetworkResponse
import com.shaza.androidpostman.shared.model.NetworkResponseInterface
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

@RunWith(AndroidJUnit4::class)

class RequestInfoTest {

    private lateinit var fragmentScenario: FragmentScenario<RequestInfoFragment>

    private lateinit var mockViewModel: RequestInfoViewModel

    private lateinit var mockNetworkResponse : NetworkResponse

    @Before
    fun setUp() {
        mockViewModel = mock(RequestInfoViewModel::class.java)
        val mockNetworkResponseLiveData = MutableLiveData<NetworkResponse>()
        val viewModelFactory = mock(ViewModelProvider.Factory::class.java)
        `when`(viewModelFactory.create(RequestInfoViewModel::class.java)).thenReturn(mockViewModel)
        mockNetworkResponse = mock()

        `when`(mockViewModel.networkResponse).thenReturn(mockNetworkResponseLiveData)

        `when`(mockNetworkResponse.url).thenReturn("https://example.com")
        `when`(mockNetworkResponse.requestType).thenReturn(RequestType.GET)
        `when`(mockNetworkResponse.queryParameters).thenReturn(mapOf("key" to "value"))
        `when`(mockNetworkResponse.requestHeaders).thenReturn(mapOf("Authorization" to "Bearer token"))
        `when`(mockNetworkResponse.responseHeaders).thenReturn(mapOf("Content-Type" to "application/json"))
        `when`(mockNetworkResponse.body).thenReturn("{ \"key\": \"value\" }")
        `when`(mockNetworkResponse.responseCode).thenReturn(200)
        `when`(mockNetworkResponse.elapsedTime).thenReturn(123L)
        `when`(mockNetworkResponse.response).thenReturn("Response")
        `when`(mockNetworkResponse.error).thenReturn(null)
        `when`(mockViewModel.extractData(any())).then {
            mockNetworkResponseLiveData.postValue(mockNetworkResponse)
        }


        fragmentScenario = FragmentScenario.launchInContainer(
            RequestInfoFragment::class.java,
            fragmentArgs = Bundle().apply {
                putParcelable("networkResponse", mockNetworkResponse)
            },
            themeResId = R.style.Theme_AndroidPostman, initialState = Lifecycle.State.RESUMED)
    }

    @Test
    fun testOnPostButtonPressed() {
        Thread.sleep(3000)

        onView(withId(R.id.request_type)).check(matches(isDisplayed()))
    }

    @Test
    fun testOnWriteUrlInEditText() {

    }
}