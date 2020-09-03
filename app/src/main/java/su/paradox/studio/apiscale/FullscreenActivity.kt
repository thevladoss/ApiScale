package su.paradox.studio.apiscale

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_fullscreen.*
import java.text.SimpleDateFormat
import java.util.*

class FullscreenActivity : AppCompatActivity() {
    lateinit var series: LineGraphSeries<DataPoint>
    private val dbHandler = DataDBHandler(this, null, null, 1)
    lateinit var pref: SharedPreferences
    val sdf1 = SimpleDateFormat("dd.MM.yyyy HH:mm")
    val sdf2 = SimpleDateFormat("dd.MM.yyyy")
    val sdf3 = SimpleDateFormat("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        supportActionBar?.hide()
        pref = getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)
        series = LineGraphSeries(returnData())
        graph.addSeries(series)

        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    if (pref.getString("date_start", "")!!.split(".")[0] == pref.getString("date_end", "")!!
                            .split(".")[0]) {
                        sdf3.format(Date(value.toLong()))
                    } else {
                        sdf2.format(Date(value.toLong()))
                    }
                } else {
                    if (intent.getStringExtra("mode") == "weight") {
                        super.formatLabel(value, isValueX) + " кг"
                    } else {
                        super.formatLabel(value, isValueX) + " °С"
                    }
                }
            }
        }

        graph.gridLabelRenderer.setHorizontalLabelsAngle(45)
        graph.viewport.isScrollable = true
        graph.viewport.isScalable = true
        graph.viewport.setScrollableY(true)
        graph.viewport.setScalableY(true)

    }

    private fun returnData(): Array<DataPoint> {
        val dp: MutableList<DataPoint> = mutableListOf()

        dbHandler.returnDataByFilter(pref.getString("date_start", "")!!, pref.getString("date_end", "")!!).forEachIndexed { i, data ->
            val y = if (intent.getStringExtra("mode") == "weight") data.weight.toDouble() else data.temp.toDouble()
            dp.add(DataPoint(sdf1.parse(data.dateDay.toString() + "." + data.dateMonth + "." + data.dateYear + " " + data.time), y))
        }

        return dp.toTypedArray()
    }
}