//
//  TodoDetailsParent.swift
//  todo-app-ios
//
//  Created by stream on 4/18/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

private class ControllerHolder {
    
    let lifecycle: LifecycleWrapper
    let controller: TodoDetailsReaktiveController
    
    init(deps: ControllerDeps, itemId: String, output: @escaping (TodoDetailsControllerOutput) -> Void) {
        self.lifecycle = LifecycleWrapper()
        
        let controller = TodoDetailsReaktiveController(
            dependencies: TodoDetailsControllerDeps(
                storeFactory: deps.storeFactory,
                database: deps.database,
                lifecycle: lifecycle.lifecycle,
                itemId: itemId,
                detailsOutput: output
            )
        )
        self.controller = controller
    }
}

struct TodoDetailsParent: View {
    
    let deps: ControllerDeps
    let itemId: String
    let output: (TodoDetailsControllerOutput) -> Void
    @State private var controller: ControllerHolder?
    @State private var viewLifecycle: LifecycleRegistry?
    @State private var detailsView = TodoDetailsViewProxy()
    
    var body: some View {
        TodoDetails(proxy: detailsView)
            .onAppear {
                if (self.controller == nil) {
                    self.controller = ControllerHolder(deps: self.deps, itemId: self.itemId, output: self.output)
                }
                let viewLifecycle = LifecycleRegistry()
                self.viewLifecycle = viewLifecycle
                self.controller?.controller.onViewCreated(todoDetailsView: self.detailsView, viewLifecycle: viewLifecycle)
                self.controller?.lifecycle.start()
                viewLifecycle.resume()
        }
        .onDisappear {
            self.viewLifecycle?.destroy()
            self.controller?.lifecycle.stop()
        }
    }
}
