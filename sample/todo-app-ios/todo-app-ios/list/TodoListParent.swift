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
    
    @EnvironmentObject var controllerDeps: ControllerDeps
    
    var body: some View {
        let lifecycle = LifecycleRegistry()
        
        let todoList = TodoList()
        let todoAdd = TodoAdd()
        
        let todoListViews = VStack {
            todoList
            todoAdd
        }
        
        return todoListViews.onAppear() {
            let controller = TodoListReaktiveController(
                dependencies: TodoListControllerDeps(
                    storeFactory: self.controllerDeps.storeFactory,
                    database: self.controllerDeps.database,
                    lifecycle: lifecycle,
                    stateKeeperProvider: nil
                )
            )
            
            lifecycle.onCreate()
            controller.onViewCreated(todoListView: todoList.listView,
                                     todoAddView: todoAdd.addView,
                                     viewLifecycle: lifecycle)
            
            lifecycle.onStart()
            lifecycle.onResume()
        }.onDisappear() {
            lifecycle.onPause()
            lifecycle.onStop()
            lifecycle.onDestroy()
            
        }
    }
}

struct TodoListParent_Previews: PreviewProvider {
    static var previews: some View {
        TodoListParent()
    }
}
