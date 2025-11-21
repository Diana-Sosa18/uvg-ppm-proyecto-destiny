package com.uvg.rachapp.ui.viewModels

import android.content.Context
import androidx.annotation.VisibleForTesting
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColorPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefsName = "color_prefs"
    private val keySelection = "selected_scheme"

    companion object {
        // default selection: 1 -> naranja (original behavior)
        internal const val DEFAULT_SELECTION = 1
    }

    private val sharedPrefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    private val _selection = MutableStateFlow(sharedPrefs.getInt(keySelection, DEFAULT_SELECTION))
    val selection = _selection.asStateFlow()

    fun setSelection(value: Int) {
        sharedPrefs.edit().putInt(keySelection, value).apply()
        _selection.value = value
    }
}
