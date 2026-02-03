package com.example.anonymouschat.data.mapper

import com.example.anonymouschat.data.remote.dto.UserIdentityDTO
import com.example.anonymouschat.domain.model.User

/** extension function to convert dto to entity */

fun UserIdentityDTO.toDomainModel(): User{
    return User(
        userId = this.userId,
        displayName = this.displayName,
        shareCode = this.shareCode,
        fullShareable = this.fullShareable,
        isNewUser = this.isNewUser
    )
}

/** domain model to DTO */
fun User.DTO(): UserIdentityDTO{
    return UserIdentityDTO(
        userId = this.userId,
        displayName = this.displayName,
        shareCode = this.shareCode,
        fullShareable = this.fullShareable,
        isNewUser = this.isNewUser

    )
}