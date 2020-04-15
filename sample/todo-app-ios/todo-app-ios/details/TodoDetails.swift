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
    var id: String
    
    @ObservedObject var detailsView = TodoDetailsViewImpl()
    @EnvironmentObject var controllerDeps: ControllerDeps
    
    @Environment(\.presentationMode) var presentation
    @State var lifecycle = LifecycleRegistry()
    
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
            
        }.onAppear() {
            let controller = TodoDetailsReaktiveController(
                dependencies: TodoDetailsControllerDeps(
                    storeFactory: self.controllerDeps.storeFactory,
                    database: self.controllerDeps.database,
                    lifecycle: self.lifecycle,
                    itemId: self.id
                )
            )
            
            self.lifecycle.onCreate()
            controller.onViewCreated(todoDetailsView: self.detailsView, viewLifecycle: self.lifecycle)
            self.lifecycle.onStart()
            self.lifecycle.onResume()
        }.onDisappear() {
            self.lifecycle.onPause()
            self.lifecycle.onStop()
            self.lifecycle.onDestroy()
        }
    }
}

struct TodoDetails_Previews: PreviewProvider {
    static var previews: some View {
        TodoDetails(id: "")
    }
}
