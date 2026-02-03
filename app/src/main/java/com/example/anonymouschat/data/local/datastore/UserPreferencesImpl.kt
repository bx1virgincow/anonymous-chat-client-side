package com.example.anonymouschat.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import com.example.anonymouschat.util.Constants
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.anonymouschat.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.USER_PREFS_NAME
)

@Singleton
class UserPreferencesImpl @Inject constructor(
    private val context: Context
) : UserPreferences {

    /** preference keys */
    private val KEY_USER_ID = stringPreferencesKey(Constants.KEY_USER_ID)
    private val KEY_DISPLAY_NAME = stringPreferencesKey(Constants.KEY_DISPLAY_NAME)
    private val KEY_SHARE_CODE = stringPreferencesKey(Constants.KEY_SHARE_CODE)
    private val KEY_FULl_SHAREABLE = stringPreferencesKey(Constants.KEY_FULL_SHAREABLE)


    override suspend fun saveUser(user: User) {
        context.userDataStore.edit { preferences ->
            preferences[KEY_USER_ID] = user.userId
            preferences[KEY_DISPLAY_NAME] = user.displayName
            preferences[KEY_SHARE_CODE] = user.shareCode
            preferences[KEY_FULl_SHAREABLE] = user.fullShareable
        }
    }

    override suspend fun getUser(): User? {
        val preferences = context.userDataStore.data.first()

        val userId = preferences[KEY_USER_ID] ?: return null
        val displayName = preferences[KEY_DISPLAY_NAME] ?: return null
        val shareCode = preferences[KEY_SHARE_CODE] ?: return null
        val fullShareable = preferences[KEY_FULl_SHAREABLE] ?: return null

        return User(
            userId = userId,
            displayName = displayName,
            shareCode = shareCode,
            fullShareable = fullShareable,
            isNewUser = false
        )
    }

    override fun observeUser(): Flow<User?> {
        return context.userDataStore.data.map { preferences ->
            val userId = preferences[KEY_USER_ID] ?: return@map null
            val displayName = preferences[KEY_DISPLAY_NAME] ?: return@map null
            val shareCode = preferences[KEY_SHARE_CODE] ?: return@map null
            val fullShareable = preferences[KEY_FULl_SHAREABLE] ?: return@map null

            User(
                userId = userId,
                displayName = displayName,
                shareCode = shareCode,
                fullShareable = fullShareable,
                isNewUser = false
            )
        }
    }

    override suspend fun deleteUser() {
        context.userDataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun hasUser(): Boolean {
        val preferences = context.userDataStore.data.first()
        return preferences[KEY_USER_ID] != null
    }


}