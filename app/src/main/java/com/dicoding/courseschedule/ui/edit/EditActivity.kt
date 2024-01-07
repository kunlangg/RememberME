package com.dicoding.courseschedule.ui.edit

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.data.CourseDatabase
import com.dicoding.courseschedule.data.DataRepository
import com.dicoding.courseschedule.databinding.ActivityEditBinding
import com.dicoding.courseschedule.ui.detail.DetailActivity
import com.dicoding.courseschedule.ui.list.ListActivity
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    private lateinit var viewModel: EditCourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val courseId = intent.getIntExtra(DetailActivity.COURSE_ID, 0)
        val courseDay = intent.getIntExtra(DetailActivity.COURSE_DAY, 0)

        val database = CourseDatabase.getInstance(applicationContext)
        val repository = DataRepository(database.courseDao())

        val factory = EditCourseViewModelFactory(repository, courseId)
        viewModel = ViewModelProvider(this, factory).get(EditCourseViewModel::class.java)

        viewModel.course.observe(this, { course ->
            showCourseDetails(course)
        })

        binding.editBtn.setOnClickListener {
            updateCourse()
        }

        binding.ibStartTime.setOnClickListener {
            showTimePicker(true)
        }

        binding.ibEndTime.setOnClickListener {
            showTimePicker(false)
        }
        binding.spinnerDay.setSelection(courseDay)
    }

    private fun showCourseDetails(course: Course?) {
        course?.apply {
            binding.edCourseName.setText(courseName)
            binding.edLecturer.setText(lecturer)
            binding.edNote.setText(note)
            binding.startTime.text = startTime
            binding.endTime.text = endTime

            val daysArray = resources.getStringArray(R.array.day)

            if (day in 0 until daysArray.size) {
                binding.spinnerDay.setSelection(day - 1)
            }
        }
    }

    private fun updateCourse() {
        val courseName = binding.edCourseName.text.toString()
        val lecturer = binding.edLecturer.text.toString()
        val note = binding.edNote.text.toString()
        val startTime = binding.startTime.text.toString()
        val endTime = binding.endTime.text.toString()

        val selectedDay = binding.spinnerDay.selectedItemPosition + 1
        viewModel.updateCourse(courseName, lecturer, note, startTime, endTime, selectedDay)
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                val selectedTime = formatTime(hourOfDay, minute)
                if (isStartTime) {
                    binding.startTime.text = selectedTime
                } else {
                    binding.endTime.text = selectedTime
                }
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}