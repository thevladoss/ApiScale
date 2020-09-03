package su.paradox.studio.apiscale.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_settings.view.*
import su.paradox.studio.apiscale.MainActivity
import su.paradox.studio.apiscale.R
import java.util.regex.Pattern
import kotlin.toString as toString

class SettingsFragment : Fragment() {
    lateinit var pref: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_settings, container, false)
        pref = requireContext().getSharedPreferences("settings", AppCompatActivity.MODE_PRIVATE)

        pref.getString("name"+pref.getInt("hiveNow", 0).toString(), "").also {
            v.edit_name.setText(it)
            v.text_settings.text = "$it:"
        }

        if (pref.contains("phone"+pref.getInt("hiveNow", 0).toString())) v.edit_phone.setText(pref.getString("phone"+pref.getInt("hiveNow", 0).toString(), ""))
        if (pref.contains("isCheckedDS"+pref.getInt("hiveNow", 0).toString())) v.check_ds.isChecked = pref.getBoolean("isCheckedDS"+pref.getInt("hiveNow", 0).toString(), false)
        if (pref.contains("isCheckedDHT"+pref.getInt("hiveNow", 0).toString())) v.check_dht.isChecked = pref.getBoolean("isCheckedDHT"+pref.getInt("hiveNow", 0).toString(), false)
        if (pref.contains("isCheckedBME"+pref.getInt("hiveNow", 0).toString())) v.check_bme.isChecked = pref.getBoolean("isCheckedBME"+pref.getInt("hiveNow", 0).toString(), false)

        println(pref.getInt("maxHive", 0))
        println(pref.getInt("hiveNow", 0))

        if (pref.getInt("maxHive", 0) == pref.getInt("hiveNow", 0) && pref.getInt("maxHive", 0) != 1) v.btn_delete.isEnabled = true

        v.edit_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != "") {
                    v.nameTextField.isErrorEnabled = false
                    pref.edit().putString("name"+pref.getInt("hiveNow", 0).toString(), s.toString()).apply()
                    v.text_settings.text = "$s:"
                } else {
                    v.nameTextField.error = "Ошибка! Должен быть введен хотя бы один символ"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        v.edit_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isEmpty()) {
                    v.phoneTextField.error = "Ошибка! Должен быть введен хотя бы один символ"
                } else if (s.length > 11) {
                    v.phoneTextField.error = "Ошибка! Должен быть введено всего 11 символов"
                } else if (s.length == 11) {
                    v.phoneTextField.isErrorEnabled = false
                    pref.edit().putString("phone"+pref.getInt("hiveNow", 0).toString(), s.toString()).apply()
                } else if (!Pattern.matches("[0-9]+", s.toString())) {
                    v.phoneTextField.error = "Ошибка! Вводите только цифры"
                } else {
                    v.phoneTextField.isErrorEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        v.check_ds.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().putBoolean("isCheckedDS"+pref.getInt("hiveNow", 0).toString(), isChecked).apply()
        }

        v.check_dht.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().putBoolean("isCheckedDHT"+pref.getInt("hiveNow", 0).toString(), isChecked).apply()
        }

        v.check_bme.setOnCheckedChangeListener { _, isChecked ->
            pref.edit().putBoolean("isCheckedBME"+pref.getInt("hiveNow", 0).toString(), isChecked).apply()
        }

        v.btn_delete.setOnClickListener {
            pref.edit().remove("name"+pref.getInt("hiveNow", 0)).apply()
            pref.edit().remove("phone"+pref.getInt("hiveNow", 0)).apply()
            pref.edit().remove("isCheckedDS"+pref.getInt("hiveNow", 0)).apply()
            pref.edit().remove("isCheckedDHT"+pref.getInt("hiveNow", 0)).apply()
            pref.edit().remove("isCheckedBME"+pref.getInt("hiveNow", 0)).apply()
            pref.edit().putInt("maxHive", pref.getInt("maxHive", 0)-1).putInt("hiveNow", pref.getInt("maxHive", 0)-1).apply()
            activity?.finish()
            startActivity(Intent(context, MainActivity::class.java))
        }

        return v
    }
}