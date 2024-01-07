package com.dicoding.courseschedule.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.data.DataRepository

class EditCourseViewModelFactory(private val repository: DataRepository, private val courseId: Int) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditCourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditCourseViewModel(repository, courseId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}