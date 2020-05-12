//
//  TodoDetailsViewImpl.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 4/4/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Foundation
import TodoLib

class TodoDetailsViewProxy: BaseMviView<TodoDetailsViewModel, TodoDetailsViewEvent>, TodoDetailsView, ObservableObject {
    
    @Published var model: TodoDetailsViewModel?
    
    override func render(model: TodoDetailsViewModel) {
        self.model = model
    }
}
