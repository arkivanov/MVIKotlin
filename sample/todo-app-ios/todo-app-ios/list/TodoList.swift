//
//  TodoList.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 22/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoList: View {
    @ObservedObject var proxy: TodoListViewProxy

    var body: some View {
        List() {
            ForEach(proxy.model?.items ?? []) { item in
                TodoRow(
                    text: item.data.text,
                    isDone: item.data.isDone,
                    onDoneClicked: { self.proxy.dispatch(event: .ItemDoneClicked(id: item.id)) },
                    onDeleteClicked: { self.proxy.dispatch(event: .ItemDeleteClicked(id: item.id)) }
                ).onTapGesture {
                    self.proxy.dispatch(event: TodoListViewEvent.ItemClicked(id: item.id))
                }
            }.onDelete(perform: delete)
        }
    }
    
    func delete(at offsets: IndexSet) {
        offsets.forEach { index in
            guard let id = proxy.model?.items[index].id else { return }
            self.proxy.dispatch(event: TodoListViewEvent.ItemDeleteClicked(id: id))
        }
    }
}

struct TodoList_Previews: PreviewProvider {
    static var previews: some View {
        TodoList(proxy: TodoListViewProxy())
    }
}
