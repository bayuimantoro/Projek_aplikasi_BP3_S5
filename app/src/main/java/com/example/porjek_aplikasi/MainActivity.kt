package com.example.porjek_aplikasi

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.ChipGroup
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var vehicleAdapter: VehicleAdapter
    private val vehicleList = ArrayList<Vehicle>()
    private var currentFilter: VehicleType? = null
    private var username: String = "Guest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val topAppBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        // Get username from Login/Register
        username = intent.getStringExtra("USERNAME") ?: "Guest"
        
        // Display username in welcome header
        val tvUsernameDisplay = findViewById<TextView>(R.id.tv_username_display)
        tvUsernameDisplay.text = "Halo, $username! 👋"

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.rv_vehicles)
        recyclerView.layoutManager = LinearLayoutManager(this)

        prepareVehicleData()

        vehicleAdapter = VehicleAdapter(vehicleList) {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("VEHICLE", it)
            startActivity(intent)
        }
        recyclerView.adapter = vehicleAdapter

        // Setup ChipGroup filter
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupFilter)
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipMobil) -> {
                    currentFilter = VehicleType.MOBIL
                    vehicleAdapter.filterByType(VehicleType.MOBIL)
                }
                checkedIds.contains(R.id.chipMotor) -> {
                    currentFilter = VehicleType.MOTOR
                    vehicleAdapter.filterByType(VehicleType.MOTOR)
                }
                else -> {
                    currentFilter = null
                    vehicleAdapter.filterByType(null)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                vehicleAdapter.filter.filter(newText)
                return false
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            R.id.action_logout -> {
                // Go back to login screen
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun prepareVehicleData() {
        vehicleList.add(
            Vehicle(
                "McLaren 720S",
                "McLaren 720S adalah mobil sport Inggris yang dirancang dan diproduksi oleh McLaren Automotive.",
                R.drawable.img_mclaren_720s,
                mapOf(
                    "Mesin" to "4.0L M840T V8 twin-turbocharged",
                    "Tenaga" to "710 bhp",
                    "0-100 km/j" to "2.8 detik"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Lamborghini Aventador SVJ",
                "Mobil sport bermesin tengah yang diproduksi oleh pabrikan otomotif Italia, Lamborghini.",
                R.drawable.img_lamborghini_aventador_svj,
                mapOf(
                    "Mesin" to "6.5L L539 V12",
                    "Tenaga" to "759 bhp",
                    "0-100 km/j" to "2.8 detik"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Ferrari SF90 Stradale",
                "Mobil sport hybrid plug-in bermesin tengah yang diproduksi oleh pabrikan mobil Italia, Ferrari.",
                R.drawable.img_ferrari_sf90,
                mapOf(
                    "Mesin" to "4.0L F154CD V8 twin-turbocharged",
                    "Tenaga" to "986 bhp",
                    "0-100 km/j" to "2.5 detik"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Porsche 911 GT3 RS",
                "Varian performa tinggi dari mobil sport Porsche 911.",
                R.drawable.img_porsche_911_gt3rs, // Placeholder
                mapOf(
                    "Mesin" to "4.0L Flat-6 Naturally Aspirated",
                    "Tenaga" to "518 bhp",
                    "0-100 km/j" to "3.0 detik"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Bugatti Chiron",
                "Mobil sport dua tempat duduk bermesin tengah yang dikembangkan dan diproduksi di Molsheim, Prancis oleh Bugatti Automobiles S.A.S.",
                R.drawable.img_bugatti_chiron, // Placeholder
                mapOf(
                    "Mesin" to "8.0L W16 quad-turbocharged",
                    "Tenaga" to "1,479 bhp",
                    "0-100 km/j" to "2.4 detik"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Koenigsegg Jesko",
                "Mobil sport bermesin tengah produksi terbatas yang diproduksi oleh produsen mobil Swedia, Koenigsegg.",
                R.drawable.img_koenigsegg_jesko, // Placeholder
                mapOf(
                    "Mesin" to "5.0L V8 twin-turbocharged",
                    "Tenaga" to "1,280 bhp (bensin)",
                    "Kecepatan Tertinggi" to "483+ km/j"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Pagani Huayra",
                "Mobil sport bermesin tengah yang diproduksi oleh produsen mobil sport Italia, Pagani.",
                R.drawable.img_pagani_huayra, // Placeholder
                mapOf(
                    "Mesin" to "6.0L V12 twin-turbocharged",
                    "Tenaga" to "720 bhp",
                    "0-100 km/j" to "3.0 detik"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Aston Martin Valkyrie",
                "Mobil sport hybrid produksi terbatas yang dibuat bekerja sama oleh pabrikan mobil Inggris Aston Martin, Red Bull Racing, dan lainnya.",
                R.drawable.img_aston_martin_valkyrie, // Placeholder
                mapOf(
                    "Mesin" to "6.5L V12 N/A + Motor Listrik",
                    "Tenaga" to "1,160 bhp",
                    "0-100 km/j" to "<2.5 detik"
                ),
                VehicleType.MOBIL
            )
        )
        vehicleList.add(
            Vehicle(
                "Ducati Panigale V4 R",
                "V4 R adalah superbike legal jalan raya dan puncak dari sepeda motor performa Ducati.",
                R.drawable.img_ducati_panigale_v4r, // Placeholder
                mapOf(
                    "Mesin" to "998 cc Desmosedici Stradale R V4",
                    "Tenaga" to "218 hp",
                    "Berat Kering" to "172 kg"
                ),
                VehicleType.MOTOR
            )
        )
        vehicleList.add(
            Vehicle(
                "Kawasaki Ninja H2R",
                "Sepeda motor hypersport supercharged, H2R adalah model sirkuit tertutup yang tidak legal untuk penggunaan jalan umum.",
                R.drawable.img_kawasaki_ninja_h2r, // Placeholder
                mapOf(
                    "Mesin" to "998cc inline-four supercharged",
                    "Tenaga" to "310 hp",
                    "Kecepatan Tertinggi" to "~400 km/j"
                ),
                VehicleType.MOTOR
            )
        )
        vehicleList.add(
            Vehicle(
                "Yamaha YZF-R1M",
                "Versi R1 yang berfokus pada lintasan, menampilkan suspensi balap elektronik dan bodywork serat karbon.",
                R.drawable.img_yamaha_yzf_r1m, // Placeholder
                mapOf(
                    "Mesin" to "998cc crossplane-crankshaft inline-four",
                    "Tenaga" to "197 hp",
                    "Berat Isi" to "202 kg"
                ),
                VehicleType.MOTOR
            )
        )
        vehicleList.add(
            Vehicle(
                "BMW M 1000 RR",
                "Model M pertama dari BMW Motorrad, berbasis S 1000 RR, dengan tenaga yang ditingkatkan dan winglet aerodinamis.",
                R.drawable.img_bmw_m1000rr, // Placeholder
                mapOf(
                    "Mesin" to "999cc inline-four dengan ShiftCam",
                    "Tenaga" to "205 hp",
                    "Kecepatan Tertinggi" to "304 km/j"
                ),
                VehicleType.MOTOR
            )
        )
        vehicleList.add(
            Vehicle(
                "Honda CBR1000RR-R Fireblade SP",
                "Superbike yang berfokus pada lintasan dengan teknologi turunan MotoGP.",
                R.drawable.img_honda_cbr1000rr, // Placeholder
                mapOf(
                    "Mesin" to "1000cc inline-four",
                    "Tenaga" to "214 hp",
                    "Berat Isi" to "201 kg"
                ),
                VehicleType.MOTOR
            )
        )
        vehicleList.add(
            Vehicle(
                "Aprilia RSV4 Factory",
                "Superbike andalan yang dikenal dengan mesin V4 dan elektroniknya yang canggih.",
                R.drawable.img_aprilia_rsv4_factory, // Placeholder
                mapOf(
                    "Mesin" to "1,099cc V4",
                    "Tenaga" to "217 hp",
                    "Berat Kering" to "177 kg"
                ),
                VehicleType.MOTOR
            )
        )
        vehicleList.add(
            Vehicle(
                "Suzuki Hayabusa",
                "Sepeda motor sport legendaris yang dikenal dengan tenaga luar biasa dan kecepatan di lintasan lurus.",
                R.drawable.img_suzuki_hayabusa, // Placeholder
                mapOf(
                    "Mesin" to "1,340cc inline-four",
                    "Tenaga" to "188 hp",
                    "Kecepatan Tertinggi" to "299 km/j (dibatasi)"
                ),
                VehicleType.MOTOR
            )
        )
    }
}