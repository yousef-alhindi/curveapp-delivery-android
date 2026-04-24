package com.curve.delivery.Localization

import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import android.util.Log
import java.util.Locale

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    fun onCreate(context: Context) {
        val lang: String? = if (getLanguage(context)!!.isEmpty()) {
            getPersistedData(context, Locale.getDefault().language)
        } else {
            getLanguage(context)
        }
        setLocale(context, lang)
    }

    fun onCreate(context: Context, defaultLanguage: String) {
        val lang = getPersistedData(context, defaultLanguage)
        setLocale(context, lang)
    }

    private fun getLanguage(context: Context): String? {
        return getPersistedData(context, Locale.getDefault().language)
    }

    fun setLocale(context: Context, language: String?) {
        persist(context, language)
        Const.lang = "" + language
        changeLang(context, language)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            updateResources(context, language)
        } else {
            updateResourcesLegacy(context, language)
        }
    }

    private fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    private fun persist(context: Context, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    private fun updateResourcess(context: Context, language: String) {
        val locale = Locale(language)
        val resources = context.resources
        val configuration = resources.configuration
        resources.updateConfiguration(configuration, context.resources.displayMetrics)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            configuration.setLocale(locale)
            resources.updateConfiguration(configuration, resources.displayMetrics)
        } else {
            configuration.locale = locale
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    fun updateResources(context: Context, language: String?): Context {
        val locale = Locale(language)
        Log.d("LocaleHelper", "language above 24: $language")
        Locale.setDefault(locale)
        val resources = context.resources
        val localeList = LocaleList(locale)
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLocales(localeList)
        configuration.setLayoutDirection(locale)
        context.createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    fun updateResourcesLegacy(context: Context, language: String?): Context {
        val locale = Locale(language)
        Log.d("LocaleHelper", "language below 24: $language")
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    fun changeLang(context: Context, lang_code: String?): ContextWrapper {
        var context = context
        val sysLocale: Locale
        val rs = context.resources
        val config = rs.configuration
        sysLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            config.locales[0]
        } else {
            config.locale
        }
        if (lang_code != "" && sysLocale.language != lang_code) {
            val locale = Locale(lang_code)
            Locale.setDefault(locale)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                config.setLocale(locale)
            } else {
                config.locale = locale
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(config)
            } else {
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
        }
        return ContextWrapper(context)
    }
}