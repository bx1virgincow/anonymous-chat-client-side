package com.example.anonymouschat.domain.model

data class User(
    val userId: String,
    val displayName: String,
    val shareCode: String,
    val fullShareable: String,
    val isNewUser: Boolean = false
){
    fun isValid():Boolean{
        return userId.isNotBlank()
                && displayName.isNotBlank()
                && shareCode.isNotBlank()
                && fullShareable.isNotBlank()
    }

    /**
     * empty/invalid user
     * useful as a default value of placeholder
     */
    companion object{
        fun empty() = User(
            userId = "",
            displayName = "",
            shareCode = "",
            fullShareable = "",
            isNewUser = false
        )
    }
}