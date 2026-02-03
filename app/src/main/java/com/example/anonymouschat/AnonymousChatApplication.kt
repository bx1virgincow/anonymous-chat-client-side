package com.example.anonymouschat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Application class for the app
 * this is required for Hilt to work
 */

@HiltAndroidApp
class AnonymousChatApplication : Application()