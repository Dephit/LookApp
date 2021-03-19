package com.sergeenko.lookapp.models

data class CheckUserNameResponse(
        var data: CheckNameData = CheckNameData(),
        var message: String = "",
        var ok: Boolean = false,
        var status: Int = 0
)