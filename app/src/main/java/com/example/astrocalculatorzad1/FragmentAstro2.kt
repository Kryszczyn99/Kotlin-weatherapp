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


class FragmentAstro2 : Fragment() {

    private var jsonData1:JSONObject? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_astro2, container, false)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sila = view.findViewById<TextView>(R.id.sila)
        val kierunek = view.findViewById<TextView>(R.id.kierunek)
        val wilgotnosc = view.findViewById<TextView>(R.id.wilgotnosc)
        val widocznosc = view.findViewById<TextView>(R.id.widocznosc)
        sila.text = jsonData1?.getJSONObject("wind")?.getString("speed") + " m/s"
        kierunek.text = jsonData1?.getJSONObject("wind")?.getString("deg") + " deg"
        wilgotnosc.text = jsonData1?.getJSONObject("main")?.getString("humidity") + " %"
        widocznosc.text = jsonData1?.getString("visibility") + " m"
    }
    companion object {

        @JvmStatic
        fun newInstance(jsonDataReceive: JSONObject) = FragmentAstro2().apply {
            //fun newInstance() = FragmentAstro1().apply {
            arguments = Bundle().apply {
                jsonData1 = jsonDataReceive
            }
        }
    }
}