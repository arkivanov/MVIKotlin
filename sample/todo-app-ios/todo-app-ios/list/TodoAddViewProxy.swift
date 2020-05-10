//
//  TodoAddViewImpl.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 22/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Foundation
import TodoLib

class TodoAddViewProxy : BaseMviView<TodoAddViewModel, TodoAddViewEvent>, TodoAddView, ObservableObject {
    
    @Published var model: TodoAddViewModel?
    
    override func render(model: TodoAddViewModel) {
        self.model = model
    }
}
