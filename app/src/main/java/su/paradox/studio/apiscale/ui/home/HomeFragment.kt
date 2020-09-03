package su.paradox.studio.apiscale.ui.home

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import su.paradox.studio.apiscale.MainActivity
import su.paradox.studio.apiscale.R
import java.lang.Exception
import kotlin.math.roundToInt


class HomeFragment : Fragment() {
    lateinit var pref: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        pref = requireContext().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)


        v.text_now.text = "Последние данные от " + pref.getString("name"+pref.getInt("hiveNow", 0).toString(), "") + ":"

        if (pref.contains("isCheckedDS"+pref.getInt("hiveNow", 0))) {
            if (pref.getBoolean("isCheckedDS"+pref.getInt("hiveNow", 0), false))
                v.layout_ds.visibility = View.VISIBLE
        }

        if (pref.contains("isCheckedDHT"+pref.getInt("hiveNow", 0))) {
            if (pref.getBoolean("isCheckedDHT"+pref.getInt("hiveNow", 0), false)) {
                v.layout_dht.visibility = View.VISIBLE
                v.layout_hum.visibility = View.VISIBLE
                v.text_another.visibility = View.VISIBLE
            }
        }

        if (pref.contains("isCheckedBME"+pref.getInt("hiveNow", 0))) {
            if (pref.getBoolean("isCheckedBME"+pref.getInt("hiveNow", 0), false)) {
                v.layout_bme.visibility = View.VISIBLE
                v.layout_pressure.visibility = View.VISIBLE
                v.layout_alt.visibility = View.VISIBLE
                v.text_another.visibility = View.VISIBLE
            }
        }

        updateUI(v)

        return v
    }

    private fun updateUI(v: View) {
        try {
            val data = returnLastData()

            v.value_date.text = data[0].split(" ")[0]
            v.value_time.text = data[0].split(" ")[1]
            v.value_weight.text = data[1]
            v.value_charge.text = data[2]
            v.text_rts.text = data[3]
            v.text_ds.text = data[4]
            v.text_dht.text = data[5]
            v.text_hum.text = data[6]
            v.text_bme.text = data[7]
            v.text_pressure.text = data[8]
            v.text_alt.text = data[9]
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Выбраны неправильные настройки", Toast.LENGTH_LONG).show()
        }
    }

    private fun returnLastData(): List<String> {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val smsInboxCursor = contentResolver.query(
            Uri.parse("content://sms/inbox"),
            null,
            "address = ?",
            arrayOf("+" + pref.getString("phone" + pref.getInt("hiveNow", 0), "")),
            null
        )
        val indexBody = smsInboxCursor!!.getColumnIndex("body")
        smsInboxCursor.moveToFirst()
        val sms = smsInboxCursor.getString(indexBody)
        println(sms)
        val split = sms.split(" | ")

        var ds = ""
        var dht_t = ""
        var dht_hum = ""
        var bme_t = ""
        var bme_press = ""
        var bme_alt = ""

        if (pref.getBoolean("isCheckedDS"+pref.getInt("hiveNow", 0), false)) {
            if (pref.getBoolean("isCheckedDHT"+pref.getInt("hiveNow", 0), false)) {
                if (pref.getBoolean("isCheckedBME"+pref.getInt("hiveNow", 0), false)) {
                    ds = split[4].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    dht_t = split[5].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    dht_hum = split[6]
                    bme_t = split[7].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    bme_press = split[8].replace("mm", "мм")
                    bme_alt = split[9].replace("m", "м")
                } else {
                    ds = split[4].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    dht_t = split[5].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    dht_hum = split[6]
                }
            } else {
                if (pref.getBoolean("isCheckedBME"+pref.getInt("hiveNow", 0), false)) {
                    ds = split[4].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    bme_t = split[5].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    bme_press = split[6].replace("mm", "мм")
                    bme_alt = split[7].replace("m", "м")
                } else {
                    ds = split[4].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                }
            }
        } else {
            if (pref.getBoolean("isCheckedDHT"+pref.getInt("hiveNow", 0), false)) {
                if (pref.getBoolean("isCheckedBME"+pref.getInt("hiveNow", 0), false)) {
                    dht_t = split[4].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    dht_hum = split[5]
                    bme_t = split[6].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    bme_press = split[7].replace("mm", "мм")
                    bme_alt = split[8].replace("m", "м")
                } else {
                    dht_t = split[4].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    dht_hum = split[5]
                }
            } else {
                if (pref.getBoolean("isCheckedBME"+pref.getInt("hiveNow", 0),false)) {
                    bme_t = split[4].split(" ")[0].toFloat().roundToInt().toString() + " C°"
                    bme_press = split[5].replace("mm", "мм")
                    bme_alt = split[6].replace("m", "м")
                }
            }
        }


        return listOf(split[0], split[1].split(" ")[0] + " кг", split[2], split[3].split(" ")[0].toFloat().roundToInt().toString() + " C°", ds, dht_t, dht_hum, bme_t, bme_press, bme_alt)
    }
}