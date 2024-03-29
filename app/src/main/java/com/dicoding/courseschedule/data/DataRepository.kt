package com.dicoding.courseschedule.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.QueryUtil
import com.dicoding.courseschedule.util.QueryUtil.nearestQuery
import com.dicoding.courseschedule.util.SortType
import com.dicoding.courseschedule.util.executeThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.GregorianCalendar

//TODO 4 : Implement repository with appropriate dao
class DataRepository(private val dao: CourseDao) {

    fun getNearestSchedule(queryType: QueryType) : LiveData<Course?> {
        val nearestQuery = nearestQuery(queryType)
        return dao.getNearestSchedule(nearestQuery)
    }

    fun getAllCourse(sortType: SortType): LiveData<PagedList<Course>> {
        val query = QueryUtil.sortedQuery(sortType)
        val factory = dao.getAll(query)
        return LivePagedListBuilder(factory, PAGE_SIZE).build()
    }

    fun getCourse(id: Int) : LiveData<Course> {
        return dao.getCourse(id)
    }

    fun getTodaySchedule() : List<Course> {
        var day = GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1
        if (day == 0) {
            day = 7
        }
        return dao.getTodaySchedule(day)
    }

    fun insert(course: Course) = executeThread {
        dao.insert(course)
    }

    suspend fun updateCourse(course: Course) {
        withContext(Dispatchers.IO) {
            dao.update(course)
        }
    }

    fun delete(course: Course) = executeThread {
        dao.delete(course)
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null
        private const val PAGE_SIZE = 10

        fun getInstance(context: Context): DataRepository? {
            return instance ?: synchronized(DataRepository::class.java) {
                if (instance == null) {
                    val database = CourseDatabase.getInstance(context)
                    instance = DataRepository(database.courseDao())
                }
                return instance
            }
        }
    }
}