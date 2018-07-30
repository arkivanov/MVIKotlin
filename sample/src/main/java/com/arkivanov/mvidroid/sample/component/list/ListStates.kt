package com.arkivanov.mvidroid.sample.component.list

import com.arkivanov.mvidroid.sample.store.todoaction.TodoActionState
import com.arkivanov.mvidroid.sample.store.todolist.TodoListState
import io.reactivex.Observable

class ListStates(
    val todoListStates: Observable<TodoListState>,
    val todoActionStates: Observable<TodoActionState>
)
