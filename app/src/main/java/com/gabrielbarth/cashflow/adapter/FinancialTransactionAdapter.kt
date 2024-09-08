package com.gabrielbarth.cashflow.adapter

import com.gabrielbarth.cashflow.entity.FinancialTransaction
import com.gabrielbarth.cashflow.ui.ListActivity

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.gabrielbarth.cashflow.R
import java.text.NumberFormat
import java.util.Locale

class FinancialTransactionAdapter(val context: Context, val cursor: Cursor) : BaseAdapter() {

    override fun getCount(): Int {
        return cursor.count
    }

    override fun getItem(position: Int): Any {
        cursor.moveToPosition(position)
        val transaction = FinancialTransaction(
            cursor.getInt(0),
            cursor.getString(1),
            cursor.getString(2),
            cursor.getDouble(3),
            cursor.getString(4),
        )
        return transaction
    }

    override fun getItemId(position: Int): Long {
        cursor.moveToPosition(position)
        return cursor.getLong(0)
    }


    private fun formatToBRL(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        return format.format(value)
    }

    private fun handleConfirm(idToRemove: Int) {
        (context as ListActivity).handleRemoveItem(idToRemove)
    }

    private fun showConfirmationDialog(idToRemove: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Excluir lançamento")
        builder.setMessage("Você confirma a remoção deste lançamento?")

        builder.setPositiveButton("Sim") { dialog, which ->
            handleConfirm(idToRemove)
        }

        builder.setNegativeButton("Não") { dialog, which ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.list_element, null)

        val imageViewType = v.findViewById<ImageView>(R.id.imageViewType)
        val textViewDetail = v.findViewById<TextView>(R.id.textViewDetail)
        val textViewValue = v.findViewById<TextView>(R.id.textViewValue)
        val textViewDate = v.findViewById<TextView>(R.id.textViewDate)
        val buttonRemove = v.findViewById<ImageButton>(R.id.buttonRemove)

        cursor.moveToPosition(position)

        if (cursor.getString(1) == "Crédito") {
            imageViewType.setImageResource(android.R.drawable.ic_input_add)
        } else {
            imageViewType.setImageResource(android.R.drawable.ic_delete)
        }

        textViewDetail.setText(cursor.getString(2))
        textViewValue.setText(formatToBRL((cursor.getDouble(3))))
        textViewDate.setText(cursor.getString(4))

        buttonRemove.setOnClickListener {
            cursor.moveToPosition(position)
            showConfirmationDialog(cursor.getInt(0))
        }
        return v
    }

}