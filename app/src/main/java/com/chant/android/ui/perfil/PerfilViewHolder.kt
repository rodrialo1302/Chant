package com.chant.android.ui.perfil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PerfilViewHolder : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Perfil ejemplo"
    }
    val text: LiveData<String> = _text
}
