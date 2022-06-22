package com.example.astrocalculatorzad1

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime
import java.time.LocalDateTime


class FragmentSun : Fragment() {

    private var dlugosc = 0.0
    private var szerokosc = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_sun, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val now: LocalDateTime = LocalDateTime.now()
        var year: Int = now.year
        var month: Int = now.monthValue
        var day: Int = now.dayOfMonth
        var hour: Int = now.hour
        var minute: Int = now.minute
        var second: Int = now.second
        var zm = AstroDateTime(year,month,day,hour,minute,second,2,false)
        var zm2= AstroCalculator.Location(szerokosc,  dlugosc)
        var zm3= AstroCalculator(zm,zm2)

        val wsCzas = view.findViewById<TextView>(R.id.wschodSlonce)
        val wsAzymut = view.findViewById<TextView>(R.id.wschodAzymut)
        val zchCzas = view.findViewById<TextView>(R.id.zachodCzas)
        val zchAzymut = view.findViewById<TextView>(R.id.zachodAzymut)
        val swCywilny = view.findViewById<TextView>(R.id.switCywilny)
        val zmCywilny = view.findViewById<TextView>(R.id.zmierzchCywilny)

        wsCzas.text = zm3.sunInfo.sunrise.toString()
        wsAzymut.text = zm3.sunInfo.azimuthRise.toString()

        zchCzas.text = zm3.sunInfo.sunset.toString()
        zchAzymut.text = zm3.sunInfo.azimuthSet.toString()

        swCywilny.text = zm3.sunInfo.twilightMorning.toString()
        zmCywilny.text = zm3.sunInfo.twilightEvening.toString()
    }


    companion object {

        @JvmStatic
        fun newInstance(dlugoscTemp: Double,szerokoscTemp: Double) = FragmentSun().apply {
            arguments = Bundle().apply {
                dlugosc=dlugoscTemp
                szerokosc=szerokoscTemp

            }
        }
    }
}