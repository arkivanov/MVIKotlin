//
//  TodoDetailsParent.swift
//  todo-app-ios
//
//  Created by stream on 4/18/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoDetailsParent: View {
    var id: String
    
    @EnvironmentObject var controllerDeps: ControllerDeps
    
    var body: some View {
        
        let lifecycle = LifecycleRegistry()
        let todoDetails = TodoDetails()
        
        return todoDetails.onAppear() {
            let controller = TodoDetailsReaktiveController(
                dependencies: TodoDetailsControllerDeps(
                    storeFactory: self.controllerDeps.storeFactory,
                    database: self.controllerDeps.database,
                    lifecycle: lifecycle,
                    itemId: self.id
                )
            )
            
            lifecycle.onCreate()
            controller.onViewCreated(todoDetailsView: todoDetails.detailsView, viewLifecycle: lifecycle)
            lifecycle.onStart()
            lifecycle.onResume()
        }.onDisappear() {
            lifecycle.onPause()
            lifecycle.onStop()
            lifecycle.onDestroy()
        }
    }
}

struct TodoDetailsParent_Previews: PreviewProvider {
    static var previews: some View {
        TodoDetailsParent(id: "")
    }
}
