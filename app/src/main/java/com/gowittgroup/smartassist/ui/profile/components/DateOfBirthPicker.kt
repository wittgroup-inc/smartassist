package com.gowittgroup.smartassist.ui.profile.components

import android.app.DatePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.gowittgroup.smartassist.ui.components.textfields.ClickablePrimaryTextField
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
internal fun DateOfBirthPicker(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    error: String = ""
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    var showDatePickerDialog by remember { mutableStateOf(false) }


    if (showDatePickerDialog) {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->

                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time
                val formattedDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
                onValueChange(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        showDatePickerDialog = false
    }

    ClickablePrimaryTextField(
        value = value,
        onValueChange = {},
        placeholderText = placeholderText,
        leadingIcon = Icons.Default.CalendarMonth,
        readOnly = true,
        error = error,
        onClick = { showDatePickerDialog = true },
    )

}