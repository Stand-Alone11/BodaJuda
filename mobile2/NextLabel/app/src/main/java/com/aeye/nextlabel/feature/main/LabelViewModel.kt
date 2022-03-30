package com.aeye.nextlabel.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.model.dto.Label
import com.aeye.nextlabel.model.network.response.LabelingResponse
import com.aeye.nextlabel.repository.LabelingRepository
import com.aeye.nextlabel.util.MultiPartUtil
import com.aeye.nextlabel.util.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class LabelViewModel: ViewModel() {
    private val labelingRepository = LabelingRepository()

    private val _uploadLabelLiveData = MutableLiveData<Resource<LabelingResponse>>()
    val uploadLabelLiveData: LiveData<Resource<LabelingResponse>>
        get() = _uploadLabelLiveData

    fun uploadLabel(label: Label, absolutePath: String)  = viewModelScope.launch {
        _uploadLabelLiveData.postValue(Resource.loading(null))
        val labelToBody = Gson().toJson(label).toRequestBody("text/plain".toMediaType())
        val imgBody = MultiPartUtil.makeMultiPartBodyFile("file", absolutePath, "image/*")
        withContext(Dispatchers.IO) {
            val response = labelingRepository.uploadLabels(labelToBody, imgBody)
            _uploadLabelLiveData.postValue(response)
        }
    }
}