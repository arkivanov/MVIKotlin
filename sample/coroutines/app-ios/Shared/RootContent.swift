//
//  ContentView.swift
//  Shared
//
//  Created by Arkadii Ivanov on 21/04/2022.
//

import SwiftUI
import Combine
import Todo

struct RootContent: View {

    var storeFactory: StoreFactory
    var database: TodoDatabase
    var dispatchers: TodoDispatchers
    
    @StateObject
    private var mainInput = PassthroughSubject<MainInput, Never>()

    @State
    private var selectedItemId: String? = nil
    
    var body: some View {
        NavigationView {
            VStack {
                NavigationLink(
                    destination: LazyView {
                        DetailsContent(
                            storeFactory: storeFactory,
                            database: database,
                            dispatchers: dispatchers,
                            itemId: selectedItemId!,
                            onItemChanged: { id, data in mainInput.send(.ItemChanged(id: id, data: data)) },
                            onItemDeleted: { id in
                                mainInput.send(.ItemDeleted(id: id))
                                selectedItemId = nil
                            }
                        )
                    },
                    isActive: Binding(
                        get: { selectedItemId != nil },
                        set: {
                            if (!$0) {
                                selectedItemId = nil
                            }
                        }
                    )
                ) { EmptyView() }
                
                MainContent(
                    storeFactory: storeFactory,
                    database: database,
                    dispatchers: dispatchers,
                    input: mainInput.eraseToAnyPublisher(),
                    onItemSelected: { self.selectedItemId = $0 }
                )
            }
            .navigationTitle("MVIKotlin")
        }
        .navigationViewStyle(StackNavigationViewStyle())
    }
}

extension PassthroughSubject : ObservableObject {
}
