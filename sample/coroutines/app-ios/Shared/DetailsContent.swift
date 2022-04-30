//
//  DetailsContent.swift
//  app-ios (iOS)
//
//  Created by Arkadii Ivanov on 23/04/2022.
//

import SwiftUI
import Combine
import Todo

struct DetailsContent: View {
    
    @StateObject
    private var view = DetailsViewProxy()
    
    @StateObject
    private var holder: ControllerHolder
    
    private var controller: DetailsController { holder.controller }
    private var model: DetailsViewModel { view.model }
    
    init(
        storeFactory: StoreFactory,
        database: TodoDatabase,
        dispatchers: TodoDispatchers,
        itemId: String,
        onItemChanged: @escaping (_ id: String, _ data: TodoItem.Data) -> Void,
        onItemDeleted: @escaping (_ id: String) -> Void
    ) {
        _holder = StateObject(
            wrappedValue: ControllerHolder { lifecycle in
                DetailsController(
                    storeFactory: storeFactory,
                    database: database,
                    lifecycle: lifecycle,
                    dispatchers: dispatchers,
                    itemId: itemId,
                    onItemChanged: onItemChanged,
                    onItemDeleted: onItemDeleted
                )
            }
        )
    }
    
    var body: some View {
        VStack() {
            TextField(
                "Write a todo",
                text: Binding<String>(
                    get: { model.text },
                    set: { view.dispatch(event: .TextChanged(text: $0)) }
                )
            )
            
            Spacer()
            
            Toggle(
                isOn: Binding<Bool>(
                    get: { model.isDone },
                    set: { _ in view.dispatch(event: .DoneClicked.shared) }
                )
            ) {
                Text("Completed")
            }
            .navigationBarTitle("Details")
            .navigationBarItems(
                trailing: Button(action: { view.dispatch(event: .DeleteClicked.shared) }) {
                    Image(systemName: "trash")
                }
            )
        }
        .onFirstAppear { holder.controller.onViewCreated(view: view, viewLifecycle: holder.lifecycle) }
        .onAppear { LifecycleRegistryExtKt.resume(holder.lifecycle) }
        .onDisappear { LifecycleRegistryExtKt.stop(holder.lifecycle) }
    }
}

private class ControllerHolder : ObservableObject {
    let lifecycle: LifecycleRegistry = LifecycleRegistryKt.LifecycleRegistry()
    let controller: DetailsController
    
    init(factory: (Lifecycle) -> DetailsController) {
        controller = factory(lifecycle)
    }
    
    deinit {
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}

private class DetailsViewProxy : BaseMviView<DetailsViewModel, DetailsViewEvent>, DetailsView, ObservableObject {
    
    @Published
    var model: DetailsViewModel = DetailsViewModel()
    
    override func render(model: DetailsViewModel) {
        self.model = model
    }
}
