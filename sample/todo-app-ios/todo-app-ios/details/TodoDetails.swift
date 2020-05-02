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
    @ObservedObject var detailsView = TodoDetailsViewImpl()
    @Environment(\.presentationMode) var presentation
    
    var body: some View {
        
        return VStack() {
            
            TextField("Write ToDo", text: Binding<String>(
                get: { self.detailsView.model?.text ?? "" },
                set: { text in self.detailsView.dispatch(event: TodoDetailsViewEvent.TextChanged(text: text)) }
            ))
            
            Spacer()
            
            Toggle(isOn: Binding<Bool>(
                get: { self.detailsView.model?.isDone ?? false },
                set: { bool in self.detailsView.dispatch(event: TodoDetailsViewEvent.DoneClicked()) }
            )) {
                Text("Completed")
            }
            .navigationBarTitle("Details")
            .navigationBarItems(trailing: Button(action: {
                self.detailsView.dispatch(event: TodoDetailsViewEvent.DeleteClicked())
                self.presentation.wrappedValue.dismiss()
            }) {
                Image(systemName: "trash")
            })
        }
    }
}

struct TodoDetails_Previews: PreviewProvider {
    static var previews: some View {
        TodoDetails()
    }
}
