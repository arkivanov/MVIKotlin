//
//  RootView.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 5/10/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib
import Combine

struct RootView: View {
    
    let controllerDeps: ControllerDeps
    @State var selectedItemId: String? = nil
    @State var isDetails = false
    @State private var listInput = PassthroughSubject<TodoListControllerInput, Never>()
    
    var body: some View {
        if (!isDetails) {
            ensureDetailsDestroyed()
        }

        return VStack {
            TodoListParent(deps: controllerDeps, input: listInput.eraseToAnyPublisher(), output: listOutput)

            NavigationLink(
                destination: LazyView(TodoDetailsParent(deps: self.controllerDeps, itemId: self.selectedItemId!, output: self.detailsOutput)),
                isActive: $isDetails
            ) { EmptyView() }
        }
    }
    
    private func listOutput(output: TodoListControllerOutput) {
        switch (output) {
        case let selected as TodoListControllerOutput.ItemSelected:
            selectedItemId = selected.id
            isDetails = true
            break
        default: break
        }
    }
    
    private func detailsOutput(output: TodoDetailsControllerOutput) {
        switch (output) {
        case is TodoDetailsControllerOutput.Finished:
            isDetails = false
        case let deleted as TodoDetailsControllerOutput.ItemDeleted:
            listInput.send(TodoListControllerInput.ItemDeleted(id: deleted.id))
        case let changed as TodoDetailsControllerOutput.ItemChanged:
            listInput.send(TodoListControllerInput.ItemChanged(id: changed.id, data: changed.data))
        default: break
        }
    }
}
