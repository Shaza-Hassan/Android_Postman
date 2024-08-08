package com.shaza.androidpostman.network

import android.net.Uri
import com.shaza.androidpostman.shared.netowrk.UrlUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

/**
 * Created by Shaza Hassan on 2024/Aug/08.
 */

class UrlUtilsTest {

    @Test
    fun `extractQueryParams returns correct map for single parameter`() {
        // Mock Uri
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(uri.queryParameterNames).thenReturn(setOf("param1"))
        Mockito.`when`(uri.getQueryParameter("param1")).thenReturn("value1")

        // Mock Uri.parse using Mockito
        Mockito.mockStatic(Uri::class.java).use { mockedStatic ->
            mockedStatic.`when`<Uri> { Uri.parse("https://example.com?param1=value1") }.thenReturn(uri)

            val url = "https://example.com?param1=value1"
            val expected = mapOf("param1" to "value1")

            val result = UrlUtils.extractQueryParams(url)

            assertEquals(expected, result)
        }
    }

    @Test
    fun `extractQueryParams returns correct map for multiple parameters`() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(uri.queryParameterNames).thenReturn(setOf("param1", "param2", "param3"))
        Mockito.`when`(uri.getQueryParameter("param1")).thenReturn("value1")
        Mockito.`when`(uri.getQueryParameter("param2")).thenReturn("value2")
        Mockito.`when`(uri.getQueryParameter("param3")).thenReturn("value3")

        Mockito.mockStatic(Uri::class.java).use { mockedStatic ->
            mockedStatic.`when`<Uri> { Uri.parse("https://example.com?param1=value1&param2=value2&param3=value3") }.thenReturn(uri)

            val url = "https://example.com?param1=value1&param2=value2&param3=value3"
            val expected = mapOf(
                "param1" to "value1",
                "param2" to "value2",
                "param3" to "value3"
            )

            val result = UrlUtils.extractQueryParams(url)

            assertEquals(expected, result)
        }
    }

    @Test
    fun `extractQueryParams returns empty map for URL without query parameters`() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(uri.queryParameterNames).thenReturn(emptySet())

        Mockito.mockStatic(Uri::class.java).use { mockedStatic ->
            mockedStatic.`when`<Uri> { Uri.parse("https://example.com") }.thenReturn(uri)

            val url = "https://example.com"
            val expected = emptyMap<String, String>()

            val result = UrlUtils.extractQueryParams(url)

            assertEquals(expected, result)
        }
    }

    @Test
    fun `extractQueryParams returns empty string for parameters with no value`() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(uri.queryParameterNames).thenReturn(setOf("param1"))
        Mockito.`when`(uri.getQueryParameter("param1")).thenReturn("")

        Mockito.mockStatic(Uri::class.java).use { mockedStatic ->
            mockedStatic.`when`<Uri> { Uri.parse("https://example.com?param1=") }.thenReturn(uri)

            val url = "https://example.com?param1="
            val expected = mapOf("param1" to "")

            val result = UrlUtils.extractQueryParams(url)

            assertEquals(expected, result)
        }
    }

    @Test
    fun `extractQueryParams handles URL with encoded characters`() {
        val uri = Mockito.mock(Uri::class.java)
        Mockito.`when`(uri.queryParameterNames).thenReturn(setOf("param1", "param2"))
        Mockito.`when`(uri.getQueryParameter("param1")).thenReturn("value 1")
        Mockito.`when`(uri.getQueryParameter("param2")).thenReturn("value/2")

        Mockito.mockStatic(Uri::class.java).use { mockedStatic ->
            mockedStatic.`when`<Uri> { Uri.parse("https://example.com?param1=value%201&param2=value%2F2") }.thenReturn(uri)

            val url = "https://example.com?param1=value%201&param2=value%2F2"
            val expected = mapOf(
                "param1" to "value 1",
                "param2" to "value/2"
            )

            val result = UrlUtils.extractQueryParams(url)

            assertEquals(expected, result)
        }
    }
}

