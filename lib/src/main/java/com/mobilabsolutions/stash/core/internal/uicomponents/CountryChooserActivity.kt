/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.core.internal.uicomponents

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobilabsolutions.stash.core.R
import kotlinx.android.synthetic.main.country_chooser_activity.back
import java.util.Locale
import kotlin.collections.ArrayList

class CountryChooserActivity : AppCompatActivity() {

    companion object {
        const val CURRENT_LOCATION_ENABLE_EXTRA = "CURRENT_LOCATION_ENABLE"
        const val CURRENT_LOCATION_CUSTOM_EXTRA = "CURRENT_LOCATION_CUSTOM"
        const val SELECTED_COUNTRY = "SELECTED_COUNTRY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.country_chooser_activity)

        val adapter = prepareCountryGroupAdapter(
            intent.getBooleanExtra(CURRENT_LOCATION_ENABLE_EXTRA, false),
            intent.getStringExtra(CURRENT_LOCATION_CUSTOM_EXTRA)
        )

        val recyclerView = findViewById<RecyclerView>(R.id.countryChooserRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                for (group in adapter.groups) {
                    group.filter(newText)
                }
                adapter.update()
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        back.setOnClickListener {
            if (searchView.hasFocus()) {
                searchView.clearFocus()
            }
            onBackPressed()
        }
    }

    private fun prepareCountryGroupAdapter(showCurrentLocation: Boolean, currentLocation: String?): CountryGroupAdapter {
        val adapter = CountryGroupAdapter {
            setResult(
                RESULT_OK,
                Intent().putExtra(SELECTED_COUNTRY, it)
            )
            finish()
        }

        if (showCurrentLocation) {
            var locale = Locale.getDefault()

            currentLocation?.let {
                // Change to the given Locale, if it's a valid Alpha2Code
                if (currentLocation.length == 2) {
                    locale = Locale("", currentLocation)
                }
            }

            try {
                adapter.addGroup(CountryGroup("Current location", listOf(Country(locale.displayCountry, locale.country, locale.isO3Country))))
            } catch (ignored: Exception) {
            }
        }

        getCountryList()
            .groupByTo(mutableMapOf()) { it.displayName[0] }
            .forEach {
                adapter.addGroup(CountryGroup(it.key.toString(), it.value))
            }

        return adapter
    }

    private fun getCountryList(): List<Country> {
        val countryList = arrayListOf<Country>()

        for (code in Locale.getISOCountries()) {
            val locale = Locale("", code)
            val displayName = locale.displayCountry
            if (displayName.isNotBlank()) {
                try {
                    countryList.add(Country(displayName, locale.country, locale.isO3Country))
                } catch (ignored: Exception) {
                }
            }
        }

        return ArrayList(countryList.sortedWith(compareBy { it.displayName }))
    }
}