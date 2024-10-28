package com.trustio.importantdocuments.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText


class MaskedTextInputEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var isUpdating = false
    private val phoneMask = "+998-##-###-##-##"
    private var unmaskedText: String = "" // Holds the unmasked version of the text

    init {
        // Add TextWatcher for phone number mask
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                // Get only the digits to format as unmasked text
                unmaskedText = s.toString().replace(Regex("[^\\d]"), "")
                val maskedText = StringBuilder()

                // Apply mask
                var i = 0
                for (char in phoneMask) {
                    if (char == '#' && i < unmaskedText.length) {
                        maskedText.append(unmaskedText[i])
                        i++
                    } else if (i < unmaskedText.length || char != '#') {
                        maskedText.append(char)
                    }
                }

                isUpdating = true
                setText(maskedText.toString())
                setSelection(maskedText.length) // Move cursor to the end
                isUpdating = false
            }
        })
    }

    // Get the full masked text
    fun getMaskedText(): String {
        return text.toString()
    }

    // Get the unmasked text (digits only)
    fun getUnmaskedText(): String {
        return unmaskedText
    }
}
