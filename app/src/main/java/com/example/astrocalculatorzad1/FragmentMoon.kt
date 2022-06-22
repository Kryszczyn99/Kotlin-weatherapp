package com.example.astrocalculatorzad1

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime
import java.time.LocalDateTime

class FragmentMoon : Fragment() {

    private var dlugosc = 0.0
    private var szerokosc = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_moon, container, false)
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
        val wsKsiezyc = view.findViewById<TextView>(R.id.wschodKs)
        val zachKsiezyc = view.findViewById<TextView>(R.id.zachodKs)
        val fazaKsiezyca = view.findViewById<TextView>(R.id.ksiezycFaza)
        val nowNaj = view.findViewById<TextView>(R.id.najblizszyNow)
        val pelnia = view.findViewById<TextView>(R.id.najblizszaPelnia)
        val synodyczny = view.findViewById<TextView>(R.id.synodycznyDzien)

        nowNaj.text = zm3.moonInfo.nextNewMoon.toString()
        pelnia.text = zm3.moonInfo.nextFullMoon.toString()
        fazaKsiezyca.text = zm3.moonInfo.illumination.toString()
        wsKsiezyc.text = zm3.moonInfo.moonrise.toString()
        zachKsiezyc.text = zm3.moonInfo.moonset.toString()
        synodyczny.text = zm3.moonInfo.age.toString()
    }
    companion object {

        @JvmStatic
        fun newInstance(dlugoscTemp: Double,szerokoscTemp: Double) = FragmentMoon().apply {
            arguments = Bundle().apply {
                dlugosc=dlugoscTemp
                szerokosc=szerokoscTemp
            }
        }
    }
}