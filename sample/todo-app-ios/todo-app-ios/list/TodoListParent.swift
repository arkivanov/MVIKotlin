//
//  TodoListParent.swift
//  todo-app-ios
//
//  Created by stream on 4/23/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoListParent: View {

    var controllerDeps: ControllerDeps
    
    var body: some View {
        let lifecycle = LifecycleWrapper()

        let controller = TodoListReaktiveController(
            dependencies: TodoListControllerDeps(
                storeFactory: controllerDeps.storeFactory,
                database: controllerDeps.database,
                lifecycle: lifecycle.lifecycle,
                stateKeeperProvider: nil
            )
        )

        let todoList = TodoList<TodoDetailsParent>(details: { id in TodoDetailsParent(id: id, controllerDeps: self.controllerDeps) })
        let todoAdd = TodoAdd()

        let lv = todoList.listView
        let av = todoAdd.addView
        controller.onViewCreated(todoListView: lv, todoAddView: av, viewLifecycle: lifecycle.lifecycle)
        
        let todoListViews = VStack {
            todoList
            todoAdd
        }
        
        return todoListViews
            .onAppear(perform: lifecycle.start)
            .onDisappear(perform: lifecycle.stop)
    }
}
