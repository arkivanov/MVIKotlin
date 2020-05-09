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
                lifecycle: lifecycle.lifecycle
            )
        )
        
        let todoList = TodoList<TodoDetailsParent>(details: { id in TodoDetailsParent(id: id, controllerDeps: self.controllerDeps) })
        let todoAdd = TodoAdd()
        
        let lv = todoList.listView
        let av = todoAdd.addView
        controller.onViewCreated(todoListView: lv, todoAddView: av, viewLifecycle: lifecycle.lifecycle, output: handleOutput)
        
        let todoListViews = VStack {
            todoList
            todoAdd
        }
        
        return todoListViews
            .onAppear(perform: lifecycle.start)
            .onDisappear(perform: lifecycle.stop)
    }
    
    private func handleOutput(output: TodoListControllerOutput) {
        switch (output) {
        case is TodoListControllerOutput.ItemSelected:
        break // Could not find a way to push the details view programmatically, so it's handled by the NavigationLink in TodoList
        default: break
        }
    }
}
