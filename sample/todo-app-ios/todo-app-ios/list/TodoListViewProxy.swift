//
//  TodoListViewImpl.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 22/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Foundation
import TodoLib

class TodoListViewProxy: BaseMviView<TodoListViewModel, TodoListViewEvent>, TodoListView, ObservableObject {
    
    @Published var model: TodoListViewModel?
    
    override func render(model: TodoListViewModel) {
        self.model = model
    }
}
