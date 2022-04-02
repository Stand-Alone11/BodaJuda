package com.aeye.nextlabel.feature.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aeye.nextlabel.model.dto.Project
import com.aeye.nextlabel.repository.ProjectRepository
import com.aeye.nextlabel.util.Resource
import com.aeye.nextlabel.util.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectViewModel: ViewModel() {
    private val projectRepository = ProjectRepository()
    private var page = 0
    private val size = 20
    var isFirstLoaded = false
    var isLastPage = false

    private val _projectResponseLiveData = MutableLiveData<Resource<List<Project>>>()
    val projectResponseLiveData: LiveData<Resource<List<Project>>>
            get() = _projectResponseLiveData

    private val _projectListLiveData = MutableLiveData<List<Project>>()
    val projectListLiveData: LiveData<List<Project>>
        get() = _projectListLiveData

    fun getProject() = viewModelScope.launch {
        if(isLastPage) return@launch

        _projectResponseLiveData.postValue(Resource.loading(null))
        withContext(Dispatchers.IO) {
            val response = projectRepository.getProjectByPage(page)
            if(response.status == Status.SUCCESS) {
                isFirstLoaded = true
                updateItems(response.data!!)
            }
            _projectResponseLiveData.postValue(response)
        }
    }

    private fun updateItems(data: List<Project>) {
        if(data.isNotEmpty() && data.size <= size) {
            page++
            _projectListLiveData.value?.let {
                val origin = it.toMutableList().apply {
                    this.addAll(data)
                }
                _projectListLiveData.postValue(origin)
            }?: _projectListLiveData.postValue(data)
        } else {
            isLastPage = true
        }
    }
}