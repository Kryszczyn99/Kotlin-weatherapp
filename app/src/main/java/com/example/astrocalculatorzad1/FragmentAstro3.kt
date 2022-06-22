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


class FragmentAstro3 : Fragment() {

    private var jsonData2:JSONObject? = null
    private var temperatureType = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_astro3, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tempdzien1 = view.findViewById<TextView>(R.id.tempdzien1)
        val tempdzien2 = view.findViewById<TextView>(R.id.tempdzien2)
        val tempdzien3 = view.findViewById<TextView>(R.id.tempdzien3)

        val tempnoc1 = view.findViewById<TextView>(R.id.tempnoc1)
        val tempnoc2 = view.findViewById<TextView>(R.id.tempnoc2)
        val tempnoc3 = view.findViewById<TextView>(R.id.tempnoc3)

        val cis1 = view.findViewById<TextView>(R.id.cis1)
        val cis2 = view.findViewById<TextView>(R.id.cis2)
        val cis3 = view.findViewById<TextView>(R.id.cis3)

        val wil1 = view.findViewById<TextView>(R.id.wil1)
        val wil2 = view.findViewById<TextView>(R.id.wil2)
        val wil3 = view.findViewById<TextView>(R.id.wil3)

        if(temperatureType)//temperatura F
        {
            val celcius = jsonData2?.getJSONArray("list")?.getJSONObject(1)?.getJSONObject("temp")?.getString("day")?.toDouble()
            val celcius2 = jsonData2?.getJSONArray("list")?.getJSONObject(1)?.getJSONObject("temp")?.getString("night")?.toDouble()
            if (celcius != null && celcius2 != null) {
                tempdzien1.text = (celcius*9/5+32).toString() + " F"
                tempnoc1.text = (celcius2*9/5+32).toString() + " F"
            }
        }
        else //temperatura C
        {
            tempdzien1.text = jsonData2?.getJSONArray("list")?.getJSONObject(1)?.getJSONObject("temp")?.getString("day") + " C"
            tempnoc1.text = jsonData2?.getJSONArray("list")?.getJSONObject(1)?.getJSONObject("temp")?.getString("night") + " C"
        }

        cis1.text = jsonData2?.getJSONArray("list")?.getJSONObject(1)?.getString("pressure") + " hPa"
        wil1.text = jsonData2?.getJSONArray("list")?.getJSONObject(1)?.getString("humidity") + " %"
        var iconCode = jsonData2?.getJSONArray("list")?.getJSONObject(1)?.getJSONArray("weather")?.getJSONObject(0)?.getString("icon")
        var obraz = view.findViewById<ImageView>(R.id.obrazek1)
        var idOfImage = resources.getIdentifier("i$iconCode","drawable",activity?.packageName)
        obraz.setBackgroundResource(idOfImage)

        if(temperatureType)//temperatura F
        {
            val celcius = jsonData2?.getJSONArray("list")?.getJSONObject(2)?.getJSONObject("temp")?.getString("day")?.toDouble()
            val celcius2 = jsonData2?.getJSONArray("list")?.getJSONObject(2)?.getJSONObject("temp")?.getString("night")?.toDouble()
            if (celcius != null && celcius2 != null) {
                tempdzien2.text = (celcius*9/5+32).toString() + " F"
                tempnoc2.text = (celcius2*9/5+32).toString() + " F"
            }
        }
        else //temperatura C
        {
            tempdzien2.text = jsonData2?.getJSONArray("list")?.getJSONObject(2)?.getJSONObject("temp")?.getString("day") + " C"
            tempnoc2.text = jsonData2?.getJSONArray("list")?.getJSONObject(2)?.getJSONObject("temp")?.getString("night") + " C"
        }

        cis2.text = jsonData2?.getJSONArray("list")?.getJSONObject(2)?.getString("pressure") + " hPa"
        wil2.text = jsonData2?.getJSONArray("list")?.getJSONObject(2)?.getString("humidity") + " %"
        iconCode = jsonData2?.getJSONArray("list")?.getJSONObject(2)?.getJSONArray("weather")?.getJSONObject(0)?.getString("icon")
        obraz = view.findViewById<ImageView>(R.id.obrazek2)
        idOfImage = resources.getIdentifier("i$iconCode","drawable",activity?.packageName)
        obraz.setBackgroundResource(idOfImage)


        if(temperatureType)//temperatura F
        {
            val celcius = jsonData2?.getJSONArray("list")?.getJSONObject(3)?.getJSONObject("temp")?.getString("day")?.toDouble()
            val celcius2 = jsonData2?.getJSONArray("list")?.getJSONObject(3)?.getJSONObject("temp")?.getString("night")?.toDouble()
            if (celcius != null && celcius2 != null) {
                tempdzien3.text = (celcius*9/5+32).toString() + " F"
                tempnoc3.text = (celcius2*9/5+32).toString() + " F"
            }
        }
        else //temperatura C
        {
            tempdzien3.text = jsonData2?.getJSONArray("list")?.getJSONObject(3)?.getJSONObject("temp")?.getString("day") + " C"
            tempnoc3.text = jsonData2?.getJSONArray("list")?.getJSONObject(3)?.getJSONObject("temp")?.getString("night") + " C"
        }
        cis3.text = jsonData2?.getJSONArray("list")?.getJSONObject(3)?.getString("pressure") + " hPa"
        wil3.text = jsonData2?.getJSONArray("list")?.getJSONObject(3)?.getString("humidity") + " %"
        iconCode = jsonData2?.getJSONArray("list")?.getJSONObject(3)?.getJSONArray("weather")?.getJSONObject(0)?.getString("icon")
        obraz = view.findViewById<ImageView>(R.id.obrazek3)
        idOfImage = resources.getIdentifier("i$iconCode","drawable",activity?.packageName)
        obraz.setBackgroundResource(idOfImage)
    }
    companion object {

        @JvmStatic
        fun newInstance(jsonDataReceive: JSONObject, tempType: Boolean) = FragmentAstro3().apply {
            //fun newInstance() = FragmentAstro1().apply {
            arguments = Bundle().apply {
                jsonData2 = jsonDataReceive
                temperatureType = tempType
            }
        }
    }
}