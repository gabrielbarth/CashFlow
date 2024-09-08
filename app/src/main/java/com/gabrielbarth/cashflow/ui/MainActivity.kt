package com.gabrielbarth.cashflow.ui

import android.app.AlertDialog
import com.gabrielbarth.cashflow.databinding.ActivityMainBinding
import com.gabrielbarth.cashflow.database.DatabaseHandler
import com.gabrielbarth.cashflow.entity.FinancialTransaction
import com.gabrielbarth.cashflow.R

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = DatabaseHandler(this)

        loadSpinnerTypeData()
        loadEditTextValue()
    }

    private fun loadSpinnerTypeData() {
        val options: List<String> = listOf<String>("Débito", "Crédito")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        binding.spinnerType.setAdapter(adapter)

        binding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                loadSpinnerDetailsData(binding.spinnerType.selectedItem.toString())
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
        binding.spinnerDetail.setAdapter(adapter)
    }

    private fun loadEditTextValue() {
        binding.editTextValue.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    binding.editTextValue.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[R$,.\\s]".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    val formatted =
                        NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed / 100)

                    current = formatted
                    binding.editTextValue.setText(formatted)
                    binding.editTextValue.setSelection(formatted.length)

                    binding.editTextValue.addTextChangedListener(this)
                }
            }
        })
    }

    fun handleOpenDatePicker(view: View) {
        val dateDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val dateFormatted = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                binding.editTextDate.setText(dateFormatted)
            },
            2024, 8, 5
        )

        dateDialog.show()
    }

    private fun validate(): Boolean {
        if (binding.editTextValue.text.isEmpty() == null ||
            binding.editTextDate.text.isEmpty() == null
        ) {
            Toast.makeText(this, "Preencha todos os dados!", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun parseCurrency(currencyString: String): Double {
        val cleanedString = currencyString
            .replace("R$", "")
            .replace(" ", "")
            .replace(",", ".")
            .replace("\\s".toRegex(), "")
        return cleanedString.toDoubleOrNull() ?: 0.0
    }

    fun handleAdd(view: View) {
        logger()

        if (validate()) {
            database.insert(
                FinancialTransaction(
                    type = binding.spinnerType.selectedItem.toString(),
                    detail = binding.spinnerDetail.selectedItem.toString(),
                    amount = parseCurrency(binding.editTextValue.text.toString()),
                    date = binding.editTextDate.text.toString(),
                )
            )
            Toast.makeText(this, "Lançado com sucesso!", Toast.LENGTH_LONG).show()
        }
    }

    fun handleNavigateToStatement(view: View) {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun showCurrentBalanceDialog(currentBalance: Double) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Saldo atual")
        builder.setMessage("Seu saldo atual é de R$ ${currentBalance}")

        builder.setNegativeButton("Ok") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun handleShowCurrentBalance(view: View) {
        val totalAmount = database.sumAmount()
        showCurrentBalanceDialog(totalAmount)
    }

    private fun logger() {
        val myObject = FinancialTransaction(
            type = binding.spinnerType.selectedItem.toString(),
            detail = binding.spinnerDetail.selectedItem.toString(),
            amount = parseCurrency(binding.editTextValue.text.toString()),
            date = binding.editTextDate.text.toString(),
        )
        Log.d("Register", myObject.toString())
    }
}