package com.example.astrocalculatorzad1

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import org.json.JSONObject
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private var arrayAdapter:ArrayAdapter<String>?=null
    private var refreshTimeTable = arrayOf(
            "10sec",
            "30sec",
            "1min",
            "15min",
            "1h"
    )

    private var refreshTime = 10;

    private var dlugosc_geo = 19.4667
    private var szerokosc_geo = 51.75
    private var miasto_geo = "lodz"

    private var appID = "e3956da9280e71d5b7de157c99ddd954"
    private var weather = "https://api.openweathermap.org/data/2.5/weather"
    private var forecast = "https://api.openweathermap.org/data/2.5/forecast/daily"
    private lateinit var jsonData1:JSONObject
    private lateinit var jsonData2:JSONObject
    private var online = false
    private var temperatureType = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val list: ArrayList<String> = ArrayList()
        //var cv = ContentValues()
        //cv.put("CITYNAME","warszawa")
        var helper = MyDBHelper(this)
        var db = helper.readableDatabase
        //db.insert("CITY",null,cv)
        var rs = db.rawQuery("SELECT * from CITY",null)
        if (rs.moveToFirst()) {
            while (!rs.isAfterLast()) {
                val name: String = rs.getString(rs.getColumnIndex("CITYNAME"))
                list.add(name)
                rs.moveToNext()
            }
        }
        //list.forEach { System.out.println(it) }
        val textSzerokosc = findViewById<TextView>(R.id.textSzerokosc)
        val textDlugosc = findViewById<TextView>(R.id.textDługość)
        textSzerokosc.text = szerokosc_geo.toString();
        textDlugosc.text = dlugosc_geo.toString();

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val toastOnlineNot = Toast.makeText(applicationContext, "BRAK INTERNETU, DANE MOGA BYC STARE", Toast.LENGTH_LONG)
        online = isOnline(this)
        if(!online) toastOnlineNot.show()

        thread(start = true) {
            if(online)
            {
                //System.out.println("jest net")
                val url = "$weather?q=$miasto_geo&units=metric&appid=$appID"
                val text = URL(url).readText()
                jsonData1 = JSONObject(text)
                val fileout = openFileOutput("lastJsonData1.json", Context.MODE_PRIVATE)
                val outputWriter = OutputStreamWriter(fileout)
                outputWriter.write(jsonData1.toString())
                outputWriter.close()
                val url2 = "$forecast?q=$miasto_geo&cnt=5&units=metric&appid=$appID"
                val text2 = URL(url2).readText()
                jsonData2 = JSONObject(text2)
                val fileout2 = openFileOutput("lastJsonData2.json", Context.MODE_PRIVATE)
                val outputWriter2 = OutputStreamWriter(fileout2)
                outputWriter2.write(jsonData2.toString())
                outputWriter2.close()
            }
            else
            {
                //System.out.println("brak neta")

                var fileInputStream: FileInputStream? = null
                fileInputStream = openFileInput("lastJsonData1.json")
                var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder: StringBuilder = StringBuilder()
                var text: String? = null
                while ({ text = bufferedReader.readLine(); text }() != null) {
                    stringBuilder.append(text)
                }

                jsonData1 = JSONObject(stringBuilder.toString())

                var fileInputStream2: FileInputStream? = null
                fileInputStream2 = openFileInput("lastJsonData2.json")
                var inputStreamReader2: InputStreamReader = InputStreamReader(fileInputStream2)
                val bufferedReader2: BufferedReader = BufferedReader(inputStreamReader2)
                val stringBuilder2: StringBuilder = StringBuilder()
                var text2: String? = null
                while ({ text2 = bufferedReader2.readLine(); text2 }() != null) {
                    stringBuilder2.append(text2)
                }

                jsonData2 = JSONObject(stringBuilder2.toString())

            }
        }
        Thread.sleep(2000)
        val switch_button = findViewById<Switch>(R.id.tempType)
        switch_button.setOnCheckedChangeListener { buttonView, isChecked ->
            temperatureType = isChecked
            refresh()
        }
        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        var xInches = width/displayMetrics.xdpi
        var yInches = height/displayMetrics.ydpi

        var diagonalInches = sqrt((xInches*xInches+yInches*yInches).toDouble())
/*
        if(diagonalInches>=6.5)
        {
            System.out.println("%%%%%%%%%%%%%%%%TABLET")
            if(savedInstanceState!=null)
            {
                textSzerokosc.text = savedInstanceState.getString("memorySzerokosc").toString()
                textDlugosc.text = savedInstanceState.getString("memoryDlugosc").toString()
            }

            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.mainFragment,FragmentSun.newInstance(dlugosc_geo,szerokosc_geo)).commit()
            fragmentManager.beginTransaction().replace(R.id.mainFragment2,FragmentMoon.newInstance(dlugosc_geo,szerokosc_geo)).commit()

            val stringCurrentTime = findViewById<TextView>(R.id.obecnyCzas)
            thread(start = true) {
                while (true) {
                    TimeUnit.SECONDS.sleep(1)
                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    val formatted = current.format(formatter)
                    stringCurrentTime.text = formatted
                }
            }
            val toastRefresh = Toast.makeText(applicationContext, "Zmieniono odświeżanie", Toast.LENGTH_SHORT)
            val sp = findViewById<Spinner>(R.id.menu)
            arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,refreshTimeTable)
            sp.adapter = arrayAdapter
            sp.onItemSelectedListener = object: AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var value = sp.selectedItem.toString()
                    if(value=="10sec") refreshTime=10
                    else if(value=="30sec") refreshTime=30
                    else if(value=="1min") refreshTime=60
                    else if(value=="15min") refreshTime=900
                    else if(value=="1h") refreshTime=3600
                    toastRefresh.show()
                }

            }


            val toastDlugosc = Toast.makeText(applicationContext, "Dlugosc musi byc z przedzialu -180 do 180", Toast.LENGTH_SHORT)
            val toastSzerokosc = Toast.makeText(applicationContext, "Szerokosc musi byc z przedzialu -90 do 90", Toast.LENGTH_SHORT)
            val button = findViewById<Button>(R.id.zatwierdz)
            button.setOnClickListener {

                val dl = findViewById<EditText>(R.id.dlugoscInput)
                val sz = findViewById<EditText>(R.id.szerokoscInput)

                if(!dl.text.isEmpty() && !sz.text.isEmpty())
                {
                    val szerokoscCurrent = sz.text.toString()
                    val dlugoscCurrent = dl.text.toString()

                    val dlugoscTemp = dlugoscCurrent.toDouble()
                    val szerokoscTemp = szerokoscCurrent.toDouble()
                    if(dlugoscTemp>180 || dlugoscTemp<-180) toastDlugosc.show()
                    else if(szerokoscTemp>90 || szerokoscTemp<-90) toastSzerokosc.show()
                    else
                    {
                        dlugosc_geo=dlugoscTemp
                        szerokosc_geo=szerokoscTemp
                        textDlugosc.text = dlugosc_geo.toString()
                        textSzerokosc.text = szerokosc_geo.toString()
                        refreshOnTablet()

                    }
                }

            }
            val toastOdswiezenie = Toast.makeText(applicationContext, "Odświeżono", Toast.LENGTH_SHORT)
            val button2 = findViewById<Button>(R.id.zmienCzas)
            button2.setOnClickListener {
                refreshOnTablet()
                toastOdswiezenie.show()
            }

            thread(start = true) {
                while (true) {

                    var i = 0
                    while(i<refreshTime)
                    {
                        TimeUnit.SECONDS.sleep(1)
                        i += 1
                    }

                    this.runOnUiThread(java.lang.Runnable {
                        refreshOnTablet()
                    })
                    toastOdswiezenie.show()

                }
            }
        }
        else
        {*/
            System.out.println("%%%%%%%%%%%%%%%%%TELEFON")
            if(savedInstanceState!=null)
            {
                textSzerokosc.text = savedInstanceState.getString("memorySzerokosc").toString()
                textDlugosc.text = savedInstanceState.getString("memoryDlugosc").toString()
            }
            refresh()//budowanie pierwszej apki//screena

            val stringCurrentTime = findViewById<TextView>(R.id.obecnyCzas)
            thread(start = true) {
                while (true) {
                    TimeUnit.SECONDS.sleep(1)
                    val current = LocalDateTime.now()

                    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                    val formatted = current.format(formatter)
                    stringCurrentTime.text = formatted
                }
            }
            val toastRefresh = Toast.makeText(applicationContext, "Zmieniono odświeżanie", Toast.LENGTH_SHORT)
            val sp = findViewById<Spinner>(R.id.menu)
            arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,refreshTimeTable)
            sp.adapter = arrayAdapter
            sp.onItemSelectedListener = object: AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var value = sp.selectedItem.toString()
                    if(value=="10sec") refreshTime=10
                    else if(value=="30sec") refreshTime=30
                    else if(value=="1min") refreshTime=60
                    else if(value=="15min") refreshTime=900
                    else if(value=="1h") refreshTime=3600
                    toastRefresh.show()
                }

            }
            val sp1 = findViewById<Spinner>(R.id.miastaLista)
            arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,list)
            sp1.adapter = arrayAdapter
            sp1.onItemSelectedListener = object: AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    var value = sp1.selectedItem.toString()
                    miasto_geo = value
                    thread(start = true) {
                        val url = "$weather?q=$miasto_geo&units=metric&appid=$appID"
                        val text = URL(url).readText()
                        jsonData1 = JSONObject(text)
                        //System.out.println(jsonData1?.getString("name"))
                        val url2 = "$forecast?q=$miasto_geo&cnt=5&units=metric&appid=$appID"
                        val text2 = URL(url2).readText()
                        jsonData2 = JSONObject(text2)
                        dlugosc_geo = jsonData1?.getJSONObject("coord")?.getString("lon")?.toDouble()!!
                        szerokosc_geo = jsonData1?.getJSONObject("coord")?.getString("lat")?.toDouble()!!
                        runOnUiThread(java.lang.Runnable {
                            textSzerokosc.text = szerokosc_geo.toString()
                            textDlugosc.text = dlugosc_geo.toString()
                        })
                    }
                    Thread.sleep(1000)
                    refresh()
                }

            }

            val toastDlugosc = Toast.makeText(applicationContext, "Dlugosc musi byc z przedzialu -180 do 180", Toast.LENGTH_SHORT)
            val toastSzerokosc = Toast.makeText(applicationContext, "Szerokosc musi byc z przedzialu -90 do 90", Toast.LENGTH_SHORT)
            val button = findViewById<Button>(R.id.zatwierdz)
            button.setOnClickListener {

                val dl = findViewById<EditText>(R.id.dlugoscInput)
                val sz = findViewById<EditText>(R.id.szerokoscInput)
                val city = findViewById<EditText>(R.id.miasto)

                if(!dl.text.isEmpty() && !sz.text.isEmpty())
                {
                    val szerokoscCurrent = sz.text.toString()
                    val dlugoscCurrent = dl.text.toString()

                    val dlugoscTemp = dlugoscCurrent.toDouble()
                    val szerokoscTemp = szerokoscCurrent.toDouble()
                    if(dlugoscTemp>180 || dlugoscTemp<-180) toastDlugosc.show()
                    else if(szerokoscTemp>90 || szerokoscTemp<-90) toastSzerokosc.show()
                    else
                    {
                        dlugosc_geo=dlugoscTemp
                        szerokosc_geo=szerokoscTemp
                        textDlugosc.text = dlugosc_geo.toString()
                        textSzerokosc.text = szerokosc_geo.toString()
                        thread(start = true) {
                            val url = "$weather?lat=$szerokosc_geo&lon=$dlugosc_geo&units=metric&appid=$appID"
                            val text = URL(url).readText()
                            jsonData1 = JSONObject(text)

                            val url2 = "$forecast?lat=$szerokosc_geo&lon=$dlugosc_geo&units=metric&appid=$appID"
                            val text2 = URL(url2).readText()
                            jsonData2 = JSONObject(text2)
                        }
                        Thread.sleep(1000)
                        refresh()

                    }
                }
                else if(!city.text.isEmpty())
                {
                    miasto_geo = city.text.toString()
                    thread(start = true) {
                        val url = "$weather?q=$miasto_geo&units=metric&appid=$appID"
                        val text = URL(url).readText()
                        jsonData1 = JSONObject(text)
                        //System.out.println(jsonData1?.getString("name"))
                        val url2 = "$forecast?q=$miasto_geo&cnt=5&units=metric&appid=$appID"
                        val text2 = URL(url2).readText()
                        jsonData2 = JSONObject(text2)
                        dlugosc_geo = jsonData1?.getJSONObject("coord")?.getString("lon")?.toDouble()!!
                        szerokosc_geo = jsonData1?.getJSONObject("coord")?.getString("lat")?.toDouble()!!
                        this.runOnUiThread(java.lang.Runnable {
                            textSzerokosc.text = szerokosc_geo.toString()
                            textDlugosc.text = dlugosc_geo.toString()
                        })

                    }

                    Thread.sleep(1000)
                    refresh()
                }
                this.runOnUiThread(java.lang.Runnable {
                    sz.setText("")
                    dl.setText("")
                    city.setText("")
                    sz.clearFocus()
                    dl.clearFocus()
                    city.clearFocus()
                })

            }
            val toastOdswiezenie = Toast.makeText(applicationContext, "Odświeżono", Toast.LENGTH_SHORT)
            val button2 = findViewById<Button>(R.id.zmienCzas)
            button2.setOnClickListener {
                refresh()
                toastOdswiezenie.show()
            }
            val buttonUlu = findViewById<Button>(R.id.dodajDoUlubionych)
            buttonUlu.setOnClickListener {
                if(!list.contains(miasto_geo))
                {
                    var cv = ContentValues()
                    cv.put("CITYNAME",miasto_geo)
                    db.insert("CITY",null,cv)
                    list.add(miasto_geo)
                    arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,list)
                    sp1.adapter = arrayAdapter
                    sp1.setSelection(list.size-1)
                    refresh()
                    Toast.makeText(applicationContext, "Dodano do ulubionych", Toast.LENGTH_SHORT)
                }
            }
            val buttonUlu2 = findViewById<Button>(R.id.dodajDoUlubionych)
            buttonUlu2.setOnLongClickListener {
                if(list.contains(miasto_geo))
                {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Android Alert")
                    builder.setMessage("Czy chcesz usunac $miasto_geo z listy ulubionych?")

                    builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                        var cv = ContentValues()
                        db.delete("CITY","CITYNAME = ?", arrayOf(miasto_geo))
                        list.remove(miasto_geo)
                        arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,list)
                        sp1.adapter = arrayAdapter
                        sp1.setSelection(list.size-1)
                        refresh()
                        Toast.makeText(applicationContext, "Usunieto z ulubionych", Toast.LENGTH_SHORT)
                        System.out.println("Dlugie")
                    }

                    builder.setNegativeButton(android.R.string.no) { dialog, which ->

                    }
                    builder.show()
                }
                false
            }
            thread(start = true) {
                while (true) {

                    var i = 0
                    while(i<refreshTime)
                    {
                        TimeUnit.SECONDS.sleep(1)
                        i += 1
                    }

                    this.runOnUiThread(java.lang.Runnable {
                        refresh()
                    })
                    toastOdswiezenie.show()

                }
            }
        //}


    }
    class ViewPagerAdapter(val items: ArrayList<Fragment>,activity:AppCompatActivity): FragmentStateAdapter(activity)
    {
        override fun getItemCount(): Int {
            return items.size
        }

        override fun createFragment(position: Int): Fragment {
            return items[position]
        }
    }
    
    private fun refresh()
    {
        val vP:ViewPager2 = findViewById(R.id.viewPager)
        val fragment1 = FragmentSun.newInstance(dlugosc_geo,szerokosc_geo)
        val fragment2 = FragmentMoon.newInstance(dlugosc_geo,szerokosc_geo)
        val fragment3 = FragmentAstro1.newInstance(jsonData1,temperatureType)
        val fragment4 = FragmentAstro2.newInstance(jsonData1)
        val fragment5 = FragmentAstro3.newInstance(jsonData2,temperatureType)
        val fragmenty: ArrayList<Fragment> = arrayListOf(
                fragment1,
                fragment2,
                fragment3,
                fragment4,
                fragment5
        )
        val adapter2 = ViewPagerAdapter(fragmenty,this)
        vP.adapter = adapter2
    }
    /*
    private fun refreshOnTablet()
    {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.mainFragment,FragmentSun.newInstance(dlugosc_geo,szerokosc_geo)).commitAllowingStateLoss()
        fragmentManager.beginTransaction().replace(R.id.mainFragment2,FragmentMoon.newInstance(dlugosc_geo,szerokosc_geo)).commitAllowingStateLoss()
    }
    */

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val szer = findViewById<TextView>(R.id.textSzerokosc)
        outState.putString("memorySzerokosc", szer.text.toString())
        val dlug = findViewById<TextView>(R.id.textDługość)
        outState.putString("memoryDlugosc", dlug.text.toString())
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
}