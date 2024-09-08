package com.gabrielbarth.cashflow.ui

import com.gabrielbarth.cashflow.adapter.FinancialTransactionAdapter
import com.gabrielbarth.cashflow.database.DatabaseHandler
import com.gabrielbarth.cashflow.databinding.ActivityListBinding

import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListBinding
    private lateinit var database: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = DatabaseHandler(this)
        initOrUpdateCursor()

    }

    private fun initOrUpdateCursor() {
        val data: Cursor = database.cursorList()
        val adapter = FinancialTransactionAdapter(this, data)

        binding.listViewPrincipal.adapter = adapter
    }

    fun handleAddNew(view: View) {
        finish()
    }

    fun handleRemoveItem(idToRemove: Int) {
        database.delete(idToRemove)
        initOrUpdateCursor()
        Toast.makeText(this, "Lançamento removido com sucesso!", Toast.LENGTH_LONG).show()
    }

}