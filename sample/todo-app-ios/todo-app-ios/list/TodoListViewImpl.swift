//
//  TodoListViewImpl.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 22/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Foundation
import TodoLib

class TodoListViewImpl: BaseMviView<TodoListViewModel, TodoListViewEvent>, TodoListView, ObservableObject {
    
    @Published var model: TodoListViewModel?
    
    override func render(model: TodoListViewModel) {
        self.model = model
        
        DiffKt.diff { (diffBuilder: DiffBuilder) in
            diffBuilder.diff(get: { (model) -> String? in
                model.selectedItemId
            }, compare: { (old, new) -> KotlinBoolean in
                if (old == nil && new == nil) {
                    return true
                } else if (old != nil && new != nil) {
                    let old = old as! String
                    let new = new as! String
                    return KotlinBoolean(value: old == new)
                } else {
                    return false
                }
            }) { (selectedItemId) in
                if (selectedItemId != nil) {
                    self.dispatch(event: TodoListViewEvent.ItemSelectionHandled())
                }
            }
        }
    }
}
