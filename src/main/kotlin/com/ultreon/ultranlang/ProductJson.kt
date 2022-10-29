package com.ultreon.ultranlang

import com.google.gson.annotations.SerializedName

/**
 * Product info.
 *
 * @author Qboi123
 */
@Suppress("unused")
class ProductJson internal constructor() {
    var version: String = ""
        private set

    @field:SerializedName("build-date")
    var buildDate: String = ""
        private set
}
