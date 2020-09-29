//
//  TodoListParent.swift
//  todo-app-ios
//
//  Created by stream on 4/23/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib
import Combine

private class ControllerHolder {
    
    let lifecycle: LifecycleWrapper
    let controller: TodoListReaktiveController
    private let cancellable: AutoCancellable
    
    init(deps: ControllerDeps, input: AnyPublisher<TodoListControllerInput, Never>, output: @escaping (TodoListControllerOutput) -> Void) {
        self.lifecycle = LifecycleWrapper()
        
        let controller = TodoListReaktiveController(
            dependencies: TodoListControllerDeps(
                storeFactory: deps.storeFactory,
                database: deps.database,
                lifecycle: lifecycle.lifecycle,
                instanceKeeper: DefaultInstanceKeeper(),
                listOutput: output
            )
        )
        self.controller = controller
        
        self.cancellable = AutoCancellable(cancellable: input.sink(receiveValue: self.controller.input))
    }
}

struct TodoListParent: View {
    
    let deps: ControllerDeps
    let input: AnyPublisher<TodoListControllerInput, Never>
    let output: (TodoListControllerOutput) -> Void
    @State private var controller: ControllerHolder?
    @State private var viewLifecycle: LifecycleRegistry?
    @State private var listView = TodoListViewProxy()
    @State private var addView = TodoAddViewProxy()
    
    var body: some View {
        VStack {
            TodoList(proxy: listView)
            TodoAdd(proxy: addView)
        }.onAppear {
            if (self.controller == nil) {
                self.controller = ControllerHolder(deps: self.deps, input: self.input, output: self.output)
            }
            let viewLifecycle = LifecycleRegistry()
            self.viewLifecycle = viewLifecycle
            self.controller?.controller.onViewCreated(todoListView: self.listView, todoAddView: self.addView, viewLifecycle: viewLifecycle)
            self.controller?.lifecycle.start()
            viewLifecycle.resume()
        }.onDisappear {
            self.viewLifecycle?.destroy()
            self.controller?.lifecycle.stop()
        }
    }
}
