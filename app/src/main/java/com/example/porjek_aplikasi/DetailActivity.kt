package com.example.porjek_aplikasi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar

class DetailActivity : AppCompatActivity() {

    private var vehicle: Vehicle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        vehicle = intent.getParcelableExtra("VEHICLE")

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        vehicle?.let {
            collapsingToolbar.title = it.name
            findViewById<ImageView>(R.id.iv_vehicle_detail).setImageResource(it.image)
            findViewById<TextView>(R.id.tv_vehicle_name_detail).text = it.name
            findViewById<TextView>(R.id.tv_vehicle_description_detail).text = it.description

            val specsLayout = findViewById<LinearLayout>(R.id.ll_specs)
            for ((key, value) in it.specs) {
                val specView = layoutInflater.inflate(R.layout.item_spec, specsLayout, false)
                specView.findViewById<TextView>(R.id.tv_spec_key).text = key
                specView.findViewById<TextView>(R.id.tv_spec_value).text = value
                specsLayout.addView(specView)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            R.id.action_share -> {
                vehicle?.let {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Lihat kendaraan ini: ${it.name}\n\n${it.description}")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(shareIntent, "Bagikan melalui"))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}