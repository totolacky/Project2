package com.example.myapplication

import java.io.Serializable

data class GalleryData(var selectedPhoto: String,
                       var userContactData: ContactData) : Serializable
