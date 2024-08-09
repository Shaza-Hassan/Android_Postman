package com.shaza.androidpostman.network

import android.net.Uri
import com.shaza.androidpostman.shared.netowrk.UrlUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

class UrlUtilsTest {

    @Before
    fun setUp() {
        // Mock static methods in Uri
        mockkStatic(Uri::class)
    }

    @After
    fun tearDown() {
        // Unmock static methods
        unmockkStatic(Uri::class)
    }

    @Test
    fun `extractQueryParams returns correct map for single parameter`() {
        // Mock Uri
        val uri = mockk<Uri>()
        every { uri.queryParameterNames } returns setOf("param1")
        every { uri.getQueryParameter("param1") } returns "value1"

        every { Uri.parse("https://example.com?param1=value1") } returns uri


        val url = "https://example.com?param1=value1"
        val expected = mapOf("param1" to "value1")
        val result = UrlUtils.extractQueryParams(url)
        assertEquals(expected, result)
    }

    @Test
    fun `extractQueryParams returns correct map for multiple parameters`() {
        val uri = mockk<Uri>()

        every { uri.queryParameterNames } returns setOf("param1", "param2", "param3")
        every { uri.getQueryParameter("param1") } returns "value1"
        every { uri.getQueryParameter("param2") } returns "value2"
        every { uri.getQueryParameter("param3") } returns "value3"

        every { Uri.parse("https://example.com?param1=value1&param2=value2&param3=value3") } returns uri

        // URL and expected result
        val url = "https://example.com?param1=value1&param2=value2&param3=value3"
        val expected = mapOf(
            "param1" to "value1",
            "param2" to "value2",
            "param3" to "value3"
        )

        // Method under test
        val result = UrlUtils.extractQueryParams(url)

        // Assert results
        assertEquals(expected, result)
    }

    @Test
    fun `extractQueryParams returns empty map for URL without query parameters`() {
        val uri = mockk<Uri>()

        every { uri.queryParameterNames } returns setOf()
        every { Uri.parse("https://example.com") } returns uri


        val url = "https://example.com"
        val expected = emptyMap<String, String>()

        val result = UrlUtils.extractQueryParams(url)

        assertEquals(expected, result)

    }

    @Test
    fun `extractQueryParams returns empty string for parameters with no value`() {
        // Prepare mock Uri
        val uri = mockk<Uri>()
        every { uri.queryParameterNames } returns setOf("param1")
        every { uri.getQueryParameter("param1") } returns ""

        // Mock Uri.parse to return the mocked Uri
        every { Uri.parse("https://example.com?param1=") } returns uri

        // URL and expected result
        val url = "https://example.com?param1="
        val expected = mapOf("param1" to "")

        // Method under test
        val result = UrlUtils.extractQueryParams(url)

        // Assert results
        assertEquals(expected, result)
    }

    @Test
    fun `extractQueryParams handles URL with encoded characters`() {
        // Prepare mock Uri
        val uri = mockk<Uri>()
        every { uri.queryParameterNames } returns setOf("param1", "param2")
        every { uri.getQueryParameter("param1") } returns "value 1"
        every { uri.getQueryParameter("param2") } returns "value/2"

        // Mock Uri.parse to return the mocked Uri
        every { Uri.parse("https://example.com?param1=value%201&param2=value%2F2") } returns uri

        // URL and expected result
        val url = "https://example.com?param1=value%201&param2=value%2F2"
        val expected = mapOf(
            "param1" to "value 1",
            "param2" to "value/2"
        )

        // Method under test
        val result = UrlUtils.extractQueryParams(url)

        // Assert results
        assertEquals(expected, result)
    }
}

