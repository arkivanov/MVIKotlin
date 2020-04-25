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
    
    @State var lifecycle: LifecycleRegistry?
    @State var controller: TodoListReaktiveController?
    
    var body: some View {
        
        let todoList = TodoList()
        let todoAdd = TodoAdd()
        
        let todoListViews = VStack {
            todoList
            todoAdd
        }
        
        return todoListViews.onAppear() {
            self.lifecycle = LifecycleRegistry()
            if let lifecycle = self.lifecycle {
                
                if (self.controller == nil) {
                    self.controller = TodoListReaktiveController(
                        dependencies: TodoListControllerDeps(
                            storeFactory: self.controllerDeps.storeFactory,
                            database: self.controllerDeps.database,
                            lifecycle: lifecycle,
                            stateKeeperProvider: nil
                        )
                    )
                }
                
                self.controller?.onViewCreated(todoListView: todoList.listView,
                                               todoAddView: todoAdd.addView,
                                               viewLifecycle: lifecycle)
                lifecycle.onCreate()
                lifecycle.onStart()
                lifecycle.onResume()
            }
        }.onDisappear() {
            self.lifecycle?.onPause()
            self.lifecycle?.onStop()
            self.lifecycle?.onDestroy()
            self.lifecycle = nil
        }
    }
}

struct TodoListParent_Previews: PreviewProvider {
    static var previews: some View {
        TodoListParent()
    }
}
