package com.gabrielbarth.cashflow

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var main: LinearLayout
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerDetail: Spinner
    private lateinit var editTextValue: EditText
    private lateinit var editTextDate: EditText
    private lateinit var imageButtonDate: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        main = findViewById(R.id.main)
        spinnerType = findViewById(R.id.spinnerType)
        spinnerDetail = findViewById(R.id.spinnerDetail)
        editTextValue = findViewById(R.id.editTextValue)
        editTextDate = findViewById(R.id.editTextDate)
        imageButtonDate = findViewById(R.id.imageButtonDate)

        loadSpinnerTypeData()
        loadEditTextValue()
    }

    private fun loadSpinnerTypeData() {
        val options: List<String> = listOf<String>("Débito", "Crédito")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        spinnerType.setAdapter(adapter)

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                loadSpinnerDetailsData(spinnerType.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadSpinnerDetailsData(selectedOption: String) {
        val creditOptions: List<String> = listOf<String>("Salário", "Extras")
        val debitOptions: List<String> =
            listOf<String>("Alimentação", "Transporte", "Saúde", "Moradia")

        val options = if (selectedOption == "Crédito") creditOptions else debitOptions

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        spinnerDetail.setAdapter(adapter)
    }

    private fun loadEditTextValue() {
        editTextValue.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editTextValue.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[R$,.\\s]".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    val formatted =
                        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed / 100)

                    current = formatted
                    editTextValue.setText(formatted)
                    editTextValue.setSelection(formatted.length)

                    editTextValue.addTextChangedListener(this)
                }
            }
        })
    }


    fun handleOpenDatePicker(view: View) {
        val dateDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val dateFormatted = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                editTextDate.setText(dateFormatted)
            },
            2024, 8, 5
        )

        dateDialog.show()
    }

    fun handleAdd(view: View) {
        logger()
    }

    fun handleNavigateToStatement(view: View) {
        logger()
    }

    fun handleShowCurrentBalance(view: View) {
        logger()
    }

    data class Register(
        val type: String,
        val detail: String,
        val value: String,
        val date: String,
    )

    private fun logger() {
        val myObject = Register(
            type = spinnerType.selectedItem.toString(),
            detail = spinnerDetail.selectedItem.toString(),
            value = editTextValue.text.toString(),
            date = editTextDate.text.toString(),
        )

        Log.d("Register", myObject.toString())
    }


}