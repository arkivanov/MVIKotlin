//
//  TodoDetail.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 3/31/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoDetail: View {
    @ObservedObject var listView = TodoListViewImpl()
    
    var body: some View {
        List(listView.model?.items ?? []) { item in
            TodoRow(
                text: item.data.text,
                isDone: item.data.isDone,
                onItemClicked: { self.listView.dispatch(event: TodoListViewEvent.ItemClicked(id: item.id)) },
                onDoneClicked: { self.listView.dispatch(event: TodoListViewEvent.ItemDoneClicked(id: item.id)) },
                onDeleteClicked: { self.listView.dispatch(event: TodoListViewEvent.ItemDeleteClicked(id: item.id)) }
            )
        }
    }
}

struct TodoDetail_Previews: PreviewProvider {
    static var previews: some View {
        TodoList()
    }
}
