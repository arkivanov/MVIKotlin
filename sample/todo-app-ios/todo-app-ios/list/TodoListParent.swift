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
    @State var controller: TodoListReaktiveController?
    @State var viewLifecycle = ViewLifecycle()
    
    var body: some View {
        
        let todoList = TodoList()
        let todoAdd = TodoAdd()
        
        let todoListViews = VStack {
            todoList
            todoAdd
        }
        
        return todoListViews.onAppear() {
            if (self.controller == nil) {
                self.controller = TodoListReaktiveController(
                    dependencies: TodoListControllerDeps(
                        storeFactory: self.controllerDeps.storeFactory,
                        database: self.controllerDeps.database,
                        lifecycle: self.viewLifecycle.lifecycle,
                        stateKeeperProvider: nil
                    )
                )
                self.controller?.onViewCreated(todoListView: todoList.listView,
                                               todoAddView: todoAdd.addView,
                                               viewLifecycle: self.viewLifecycle.lifecycle)
            }
            
            self.viewLifecycle.lifecycle.onStart()
            self.viewLifecycle.lifecycle.onResume()
            
        }.onDisappear() {
            self.viewLifecycle.lifecycle.onPause()
            self.viewLifecycle.lifecycle.onStop()
        }
    }
}

struct TodoListParent_Previews: PreviewProvider {
    static var previews: some View {
        TodoListParent()
    }
}
