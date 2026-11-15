
package com.example.roulette

import android.app.Application
import com.example.roulette.di.AppContainer

class RouletteApplication : Application() {

    val container: AppContainer by lazy {
        AppContainer(this)
    }
}
