package com.example.saveit.model

data class User (
    val id:String?,
    val userId:String?,
    val displayName:String?,
    val avatarUrl:String?,
    val quote:String?,
    val profession:String?
) {
    fun toMap(): MutableMap<String, String?> {
        return mutableMapOf(
            "user_id" to this.userId,
            "display_name" to this.userId,
            "profession" to this.profession,
            "avatar_url" to this.avatarUrl,
            "user_Id" to this.userId,
        )
    }
}