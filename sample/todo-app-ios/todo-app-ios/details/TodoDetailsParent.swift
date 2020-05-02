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
    var controllerDeps: ControllerDeps
        
    var body: some View {
        let lifecycle = LifecycleWrapper()

        let controller = TodoDetailsReaktiveController(
            dependencies: TodoDetailsControllerDeps(
                storeFactory: controllerDeps.storeFactory,
                database: controllerDeps.database,
                lifecycle: lifecycle.lifecycle,
                itemId: id
            )
        )

        let todoDetails = TodoDetails()

        let dv = todoDetails.detailsView
        controller.onViewCreated(todoDetailsView: dv, viewLifecycle: lifecycle.lifecycle)
        
        return todoDetails
            .onAppear(perform: lifecycle.start)
            .onDisappear(perform: lifecycle.stop)
    }
}
