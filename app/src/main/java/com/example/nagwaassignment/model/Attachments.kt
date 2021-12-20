package com.example.nagwaassignment.model

import java.io.Serializable

data class  Attachments(val name: String, val id: Int, val url: String, val type: String) : Serializable{
}