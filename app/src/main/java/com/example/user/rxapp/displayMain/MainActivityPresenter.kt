package com.example.user.rxapp.displayMain

import android.util.Log
import com.example.user.rxapp.data.local.LocalDataHelper
import com.example.user.rxapp.data.local.dbRoom.SimpleTaskDO
import com.example.user.rxapp.data.local.dbRoom.TaskDo
import com.example.user.rxapp.tools.getNextCalendarDay
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 5/24/2018.
 */
class MainActivityPresenter @Inject constructor(private val db: LocalDataHelper) : MainActivityContract.Presenter {
    private val TAG = this.javaClass.simpleName
    private lateinit var view: MainActivityContract.View
    private val disposables = CompositeDisposable()
    private val calendar = Calendar.getInstance()

    init {
        calendar.timeInMillis = System.currentTimeMillis()

    }

    override fun start() {
    }

    override fun takeView(view: MainActivityContract.View) {
        this.view = view
    }

    override fun disposeAll() {
        disposables.clear()

    }

    override fun deleteAllTasks() {
        disposables.add(Completable.fromAction { db.deleteAllTasks() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        view.showToast("success")

                    }

                    override fun onError(e: Throwable) {
                        view.showFailedDialog("Message : ${e.message}")
                    }

                }))
    }

    override fun startTaskActivity() {
        disposables.add(db.getAllTasksCount()
                .subscribeOn(Schedulers.computation())
                .map { it > 0 }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.setPBVisibility(true) }
                .subscribeWith(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(t: Boolean) {
                        if (t) {
                            view.startTaskActivity()
                        } else {
                            view.showToast("no tasks to show :( First add some")

                        }
                        view.setPBVisibility(false)

                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "startTaskActivity error : ${e.message}")
                        view.setPBVisibility(false)
                        view.showFailedDialog("Some error occurred please try again later")
                    }
                }))

    }

    override fun deleteTodayTasks() {
        disposables.add(Completable.fromAction { db.deleteAllTasksFromToday() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        view.showToast("success")

                    }

                    override fun onError(e: Throwable) {
                        view.showFailedDialog("Message : ${e.message}")
                    }

                }))
    }

    //TODO move to AddTaskActivityPresenter
    override fun addTask(simpleTaskDO: SimpleTaskDO) {
        calendar[Calendar.DAY_OF_WEEK] = getNextCalendarDay(calendar)
        disposables.add(Completable.fromAction {
            db.addTaskToLocalDB(TaskDo(null, simpleTaskDO.taskName, simpleTaskDO.taskDescription,
                    simpleTaskDO.isDone, Date(calendar.timeInMillis)))
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        view.showToast("Task was saved successfully")
                    }

                    override fun onError(e: Throwable) {
                        view.showFailedSavingTaskDialog("Some problem occurred with saving task(${simpleTaskDO.taskName}, please try again.",
                                simpleTaskDO)

                    }

                }))

    }
}