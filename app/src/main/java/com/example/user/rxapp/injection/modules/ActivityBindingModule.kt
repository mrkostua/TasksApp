package com.example.user.rxapp.injection.modules

import com.example.user.rxapp.addTask.AddTaskActivity
import com.example.user.rxapp.addTask.AddTaskModule
import com.example.user.rxapp.firstScreen.MainActivity
import com.example.user.rxapp.firstScreen.MainActivityModule
import com.example.user.rxapp.injection.ActivityScope
import com.example.user.rxapp.displayTasks.TasksActivity
import com.example.user.rxapp.displayTasks.TasksActivityModule
import com.example.user.rxapp.wikiSearch.WikiSearchActivity
import com.example.user.rxapp.wikiSearch.WikiSearchModule
import com.example.user.rxapp.wikiSearch.wikiAPI.WikiApiModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author Kostiantyn Prysiazhnyi on 5/22/2018.
 */
@Module
abstract class ActivityBindingModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = [(TasksActivityModule::class)])
    abstract fun getTasksActivity(): TasksActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    abstract fun getMainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(AddTaskModule::class)])
    abstract fun getAddTaskActivity(): AddTaskActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [(WikiSearchModule::class), (WikiApiModule::class)])
    abstract fun getWikiSearchModule(): WikiSearchActivity
}