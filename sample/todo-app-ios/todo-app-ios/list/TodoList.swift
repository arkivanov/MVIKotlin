//
//  TodoList.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 22/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoList<Details: View>: View {
    @ObservedObject var listView = TodoListViewImpl()
    var details: (String) -> Details
    
    var body: some View {
        List() {
            ForEach(listView.model?.items ?? []) { item in
                NavigationLink(destination: LazyView(self.details(item.id))) {
                    TodoRow(
                        text: item.data.text,
                        isDone: item.data.isDone,
                        onDoneClicked: { self.listView.dispatch(event: TodoListViewEvent.ItemDoneClicked(id: item.id)) }
                    )
                }
            }.onDelete(perform: delete)
        }
    }
    
    func delete(at offsets: IndexSet) {
        offsets.forEach { index in
            guard let id = listView.model?.items[index].id else { return }
            self.listView.dispatch(event: TodoListViewEvent.ItemDeleteClicked(id: id))
        }
    }
}

struct TodoList_Previews: PreviewProvider {
    static var previews: some View {
        TodoList(details: { id in Text("") })
    }
}
