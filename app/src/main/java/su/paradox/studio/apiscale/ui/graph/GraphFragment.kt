package su.paradox.studio.apiscale.ui.graph

import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.fragment_graph.view.*
import su.paradox.studio.apiscale.Data
import su.paradox.studio.apiscale.DataDBHandler
import su.paradox.studio.apiscale.FullscreenActivity
import su.paradox.studio.apiscale.R
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class GraphFragment : Fragment() {
    lateinit var pref: SharedPreferences

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_graph, container, false)
        pref = requireContext().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val dbHandler = DataDBHandler(this.requireContext(), null, null, 1)

        try {
            val contentResolver: ContentResolver = requireContext().contentResolver
            val smsInboxCursor = contentResolver.query(
                Uri.parse("content://sms/inbox"),
                null,
                "address = ?",
                arrayOf("+" + pref.getString("phone" + pref.getInt("hiveNow", 0), "")),
                null
            )
            val indexBody = smsInboxCursor!!.getColumnIndex("body")
            smsInboxCursor.moveToLast()

            smsInboxCursor.moveToLast()
            val datas = dbHandler.returnAllData()
            do {
                val sms = smsInboxCursor.getString(indexBody)
                val split = sms.split(" | ")
                if (datas.size != 0) {
                    datas.last().also {
                        val date =
                            it.dateDay.toString() + "." + it.dateMonth + "." + it.dateYear + " " + it.time
                        if (sdf.parse(date) < sdf.parse(split[0])) {
                            val split1 = split[0].split(" ")
                            val split2 = split1[0].split(".")
                            dbHandler.addProduct(
                                Data(
                                    split2[2],
                                    split2[1],
                                    split2[0],
                                    split1[1],
                                    split[1].split(" ")[0].toFloat(),
                                    split[2].replace("%", "").toInt(),
                                    split[3].replace(" C", "").toFloat().roundToInt()
                                )
                            )
                        }
                    }
                } else {
                    val split1 = split[0].split(" ")
                    val split2 = split1[0].split(".")
                    dbHandler.addProduct(
                        Data(
                            split2[2],
                            split2[1],
                            split2[0],
                            split1[1],
                            split[1].split(" ")[0].toFloat(),
                            split[2].replace("%", "").toInt(),
                            split[3].replace(" C", "").toFloat().roundToInt()
                        )
                    )
                }
            } while (smsInboxCursor.moveToPrevious())

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка! Выберите номер в настройках", Toast.LENGTH_LONG).show()
        }


        if (!pref.contains("date_start") && !pref.contains("date_end")) {
            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            pref.edit().putString("date_start", sdf.format(cal.time)).putString("date_end", sdf.format(cal.time)).apply()
        }

        v.text_start.text = pref.getString("date_start", "")
        v.text_end.text = pref.getString("date_end", "")

        if (dbHandler.returnDataByFilter(pref.getString("date_start", "")!!, pref.getString("date_end", "")!!).size == 0) {
            v.text_data.text = "Данных нет"
        }

        v.fb.setOnClickListener {
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            builder.setTitleText("Выберите промежуток")
            val materialDatePicker = builder.build()
            materialDatePicker.addOnPositiveButtonClickListener {

                val first = Calendar.getInstance()
                first.timeInMillis = it.first!!
                val second = Calendar.getInstance()
                second.timeInMillis = it.second!!

                pref.edit().putString("date_start", sdf.format(first.time)).putString("date_end", sdf.format(second.time)).apply()

                v.text_start.text = sdf.format(first.time)
                v.text_end.text = sdf.format(second.time)

                if (dbHandler.returnDataByFilter(pref.getString("date_start", "")!!, pref.getString("date_end", "")!!).size == 0) {
                    v.text_data.text = "Данных нет"
                } else {
                    v.text_data.text = "Данные есть"
                }
            }
            materialDatePicker.show(activity?.supportFragmentManager!!, "DATE_PICKER")

        }


        v.card_weight.setOnClickListener {
            when (dbHandler.returnDataByFilter(pref.getString("date_start", "")!!, pref.getString("date_end", "")!!).size) {
                0 -> {
                    Toast.makeText(context, "Ошибка! Данных нет", Toast.LENGTH_LONG).show()
                }
                1 -> {
                    Toast.makeText(context, "Ошибка! За выбранный период только одно значение", Toast.LENGTH_LONG).show()
                }
                else -> {
                    startActivity(Intent(context, FullscreenActivity::class.java).putExtra("mode", "weight"))
                }
            }
        }

        v.card_temp.setOnClickListener {
            when (dbHandler.returnDataByFilter(pref.getString("date_start", "")!!, pref.getString("date_end", "")!!).size) {
                0 -> {
                    Toast.makeText(context, "Ошибка! Данных нет", Toast.LENGTH_LONG).show()
                }
                1 -> {
                    Toast.makeText(context, "Ошибка! За выбранный период только одно значение", Toast.LENGTH_LONG).show()
                }
                else -> {
                    startActivity(Intent(context, FullscreenActivity::class.java).putExtra("mode", "temp"))
                }
            }
        }

        return v
    }
}