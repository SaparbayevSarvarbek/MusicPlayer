package com.example.navigationmenu.models

import java.io.Serializable

data class Audio(
    var data: String,
    var musicName: String,
    var artist: String,
    var duration: String,
    var title: String
): Serializable
