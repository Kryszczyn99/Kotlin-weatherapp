package com.example.astrocalculatorzad1

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import org.json.JSONObject


class FragmentAstro1 : Fragment() {

    private var jsonData1:JSONObject? = null
    private var temperatureType = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_astro1, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val miasto = view.findViewById<TextView>(R.id.sila)
        val dlugosc = view.findViewById<TextView>(R.id.kierunek)
        val szerokosc = view.findViewById<TextView>(R.id.wilgotnosc)
        val cisnienie = view.findViewById<TextView>(R.id.widocznosc)
        val temperatura = view.findViewById<TextView>(R.id.temperatura)
        val opis = view.findViewById<TextView>(R.id.opis)
        miasto.text = jsonData1?.getString("name")
        dlugosc.text = jsonData1?.getJSONObject("coord")?.getString("lon")
        szerokosc.text = jsonData1?.getJSONObject("coord")?.getString("lat")
        cisnienie.text = jsonData1?.getJSONObject("main")?.getString("pressure") + " hPa"
        if(temperatureType)//temperatura F
        {
            val celcius = jsonData1?.getJSONObject("main")?.getString("temp")?.toDouble()
            if (celcius != null) {
                temperatura.text = (celcius*9/5+32).toString() + " F"
            }
        }
        else //temperatura C
        {
            temperatura.text = jsonData1?.getJSONObject("main")?.getString("temp") + " C"
        }

        opis.text = jsonData1?.getJSONArray("weather")?.getJSONObject(0)?.getString("description")
        val iconCode = jsonData1?.getJSONArray("weather")?.getJSONObject(0)?.getString("icon")
        val obraz = view.findViewById<ImageView>(R.id.obrazek)
        val idOfImage = resources.getIdentifier("i$iconCode","drawable",activity?.packageName)
        obraz.setBackgroundResource(idOfImage)

    }
    companion object {

        @JvmStatic
        fun newInstance(jsonDataReceive: JSONObject, tempType: Boolean) = FragmentAstro1().apply {
        //fun newInstance() = FragmentAstro1().apply {
            arguments = Bundle().apply {
                jsonData1 = jsonDataReceive
                temperatureType = tempType
            }
        }
    }
}