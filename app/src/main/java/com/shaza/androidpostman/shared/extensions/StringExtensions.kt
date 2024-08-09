package com.shaza.androidpostman.shared.extensions

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

fun String.toMap(): Map<String, String> {
    return this.removeSurrounding("{", "}")
        .split(", ")
        .associate {
            val (key, value) = it.split("=")
            key to value
        }
}