package com.aeye.nextlabel.feature.labeling

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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class LabelingViewModel: ViewModel() {
    private val labelingRepository = LabelingRepository()

    private val _uploadLabelResponse = MutableLiveData<Resource<LabelingResponse>>()
    val uploadLabelResponse: LiveData<Resource<LabelingResponse>>
        get() = _uploadLabelResponse

    fun uploadLabel(label: Label, absolutePath: String) = viewModelScope.launch {
        _uploadLabelResponse.postValue(Resource.loading(null))
        val labelRequestBody = RequestBody.create("text/plain".toMediaType(), Gson().toJson(label))
        val imageMultipart = MultiPartUtil.makeMultiPartBodyFile("file", absolutePath, "image/*")
        withContext(Dispatchers.IO) {
            _uploadLabelResponse.postValue(labelingRepository.uploadLabels(labelRequestBody, imageMultipart))
        }
    }
}