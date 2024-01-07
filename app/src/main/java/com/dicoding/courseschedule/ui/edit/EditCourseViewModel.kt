package com.dicoding.courseschedule.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.DataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCourseViewModel(private val repository: DataRepository, private val courseId: Int) : ViewModel() {

    val course: LiveData<Course> = repository.getCourse(courseId)

    fun updateCourse(courseName: String, lecturer: String, note: String, startTime: String, endTime: String, day: Int) {
        viewModelScope.launch {
            val currentCourse = course.value
            if (currentCourse != null) {
                val updatedCourse = Course(
                    currentCourse.id,
                    courseName,
                    day,
                    startTime,
                    endTime,
                    lecturer,
                    note
                )
                repository.updateCourse(updatedCourse)
            }
        }
    }
}

