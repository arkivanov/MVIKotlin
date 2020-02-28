//
//  TodoAdd.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 24/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoAdd: View {
    @ObservedObject var addView = TodoAddViewImpl()

    var body: some View {
        let b = Binding<String>(
            get: { self.addView.model?.text ?? "" },
            set: { text in self.addView.dispatch(event: TodoAddViewEvent.TextChanged(text: text)) }
        )
        
        return HStack {
            TextField("Add todo", text: b)

            Button(action: { self.addView.dispatch(event: TodoAddViewEvent.AddClicked()) }) {
                Text("Add")
            }
        }.padding(16)
    }
}

struct TodoAdd_Previews: PreviewProvider {
    static var previews: some View {
        TodoAdd()
    }
}
