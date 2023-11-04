//
//  MainContent.swift
//  app-ios (iOS)
//
//  Created by Arkadii Ivanov on 23/04/2022.
//

import SwiftUI
import Combine
import Todo

struct MainContent: View {
    
    @StateObject
    private var view = MainViewProxy()
    
    @StateObject
    private var holder: ControllerHolder
    
    private var model: MainViewModel { view.model }
    
    init(
        storeFactory: StoreFactory,
        database: TodoDatabase,
        dispatchers: TodoDispatchers,
        input: AnyPublisher<MainInput, Never>,
        onItemSelected: @escaping (_ id: String) -> Void
    ) {
        _holder = StateObject(
            wrappedValue: ControllerHolder(input: input) { lifecycle in
                MainController(
                    storeFactory: storeFactory,
                    database: database,
                    lifecycle: lifecycle,
                    instanceKeeper: InstanceKeeperDispatcherKt.InstanceKeeperDispatcher(),
                    dispatchers: dispatchers,
                    onItemSelected: onItemSelected
                )
            }
        )
    }
    
    var body: some View {
        VStack {
            List {
                ForEach(model.items, id: \.self) { item in
                    HStack {
                        Image(systemName: item.isDone ? "checkmark.square" : "square")
                            .onTapGesture { view.dispatch(event: .ItemDoneClicked(id: item.id)) }
                        
                        Text(item.text)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        
                        Image(systemName: "trash")
                            .onTapGesture { view.dispatch(event: .ItemDeleteClicked(id: item.id)) }
                    }
                    .contentShape(Rectangle())
                    .onTapGesture { view.dispatch(event: .ItemClicked(id: item.id)) }
                }
            }.listStyle(.plain)
            
            InputView(
                textBinding: Binding(
                    get: { model.text },
                    set: { view.dispatch(event: .TextChanged(text: $0)) }
                ),
                onAddClicked: { view.dispatch(event: .AddClicked()) }
            )
        }
        .onFirstAppear { holder.controller.onViewCreated(view: view, viewLifecycle: holder.lifecycle) }
        .onAppear { LifecycleRegistryExtKt.resume(holder.lifecycle) }
        .onDisappear { LifecycleRegistryExtKt.stop(holder.lifecycle) }
    }
}

private struct InputView: View {
    var textBinding: Binding<String>
    var onAddClicked: () -> Void
        
    var body: some View {
        HStack {
            TextField("Todo text", text: self.textBinding)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .edgesIgnoringSafeArea(Edge.Set.bottom)

            Button(action: self.onAddClicked) {
                Image(systemName: "plus")
            }.frame(minWidth: 36, minHeight: 36)
        }.padding(8)
    }
}

private class ControllerHolder : ObservableObject {
    let lifecycle: LifecycleRegistry = LifecycleRegistryKt.LifecycleRegistry()
    let controller: MainController
    private var cancellable: AnyCancellable?

    init(input: AnyPublisher<MainInput, Never>, factory: (Lifecycle) -> MainController) {
        controller = factory(lifecycle)
        
        cancellable = input.sink { [weak self] in
            switch $0 {
            case let .ItemChanged(id, data): self?.controller.onItemChanged(id: id, data: data)
            case let .ItemDeleted(id): self?.controller.onItemDeleted(id: id)
            }
        }
    }
    
    deinit {
        cancellable?.cancel()
        LifecycleRegistryExtKt.destroy(lifecycle)
    }
}

private class MainViewProxy : BaseMviView<MainViewModel, MainViewEvent>, MainView, ObservableObject {
    
    @Published
    var model: MainViewModel = MainViewModel()

    override func render(model: MainViewModel) {
        self.model = model
    }
}

enum MainInput {
    case ItemChanged(id: String, data: TodoItem.Data)
    case ItemDeleted(id: String)
}
