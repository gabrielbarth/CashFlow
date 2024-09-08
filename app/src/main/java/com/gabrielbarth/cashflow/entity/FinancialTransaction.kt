package com.gabrielbarth.cashflow.entity

class FinancialTransaction(
    var _id: Int? = null,
    var type: String,
    var detail: String,
    var amount: Double,
    var date: String
)