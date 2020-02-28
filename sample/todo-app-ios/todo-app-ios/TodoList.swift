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
    @ObservedObject var listView = TodoListViewImpl()

    var body: some View {
        List(listView.model?.items ?? []) { item in
            TodoRow(
                text: item.data.text,
                isDone: item.data.isDone,
                onDoneClicked: { self.listView.dispatch(event: TodoListViewEvent.ItemDoneClicked(id: item.id)) },
                onDeleteClicked: { self.listView.dispatch(event: TodoListViewEvent.ItemDeleteClicked(id: item.id)) }
            )
        }
    }
}

struct TodoList_Previews: PreviewProvider {
    static var previews: some View {
        TodoList()
    }
}
