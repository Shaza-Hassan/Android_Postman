package com.shaza.androidpostman.shared.extensions

/**
 * Created by Shaza Hassan on 2024/Aug/09.
 */

fun String.toMap(): Map<String, String> {
    if (this.isEmpty() || this.removeSurrounding("{", "}").isEmpty()) return emptyMap()
    return this.removeSurrounding("{", "}")
        .split(", ")
        .mapNotNull {
            val parts = it.split("=")
            if (parts.size == 2) {
                parts[0] to parts[1]
            } else {
                null
            }
        }.toMap()
}
