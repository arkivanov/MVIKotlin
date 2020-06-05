//
//  TodoDetail.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 3/31/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoDetails: View {

    @ObservedObject var proxy: TodoDetailsViewProxy
    
    var body: some View {
        VStack() {
            TextField("Write ToDo", text: Binding<String>(
                get: { self.proxy.model?.text ?? "" },
                set: { text in self.proxy.dispatch(event: TodoDetailsViewEvent.TextChanged(text: text)) }
            ))
            
            Spacer()
            
            Toggle(isOn: Binding<Bool>(
                get: { self.proxy.model?.isDone ?? false },
                set: { bool in self.proxy.dispatch(event: TodoDetailsViewEvent.DoneClicked()) }
            )) {
                Text("Completed")
            }
            .navigationBarTitle("Details")
            .navigationBarItems(trailing: Button(action: {
                self.proxy.dispatch(event: TodoDetailsViewEvent.DeleteClicked())
            }) {
                Image(systemName: "trash")
            })
        }
    }
}

struct TodoDetails_Previews: PreviewProvider {
    static var previews: some View {
        TodoDetails(proxy: TodoDetailsViewProxy())
    }
}
