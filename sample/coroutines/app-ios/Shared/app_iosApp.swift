//
//  app_iosApp.swift
//  Shared
//
//  Created by Arkadii Ivanov on 21/04/2022.
//

import SwiftUI
import Todo

@main
struct app_iosApp: App {
    
    @StateObject
    private var holder = Holder()

    var body: some Scene {
        WindowGroup {
            RootContent(
                storeFactory: holder.storeFactory,
                database: holder.database,
                dispatchers: holder.dispatchers
            )
        }
    }
}

private class Holder : ObservableObject {
    let storeFactory: StoreFactory
    let database: TodoDatabase
    let dispatchers: TodoDispatchers
    private let timeTravelServer: TimeTravelServer

    init() {
        storeFactory = LoggingStoreFactory(delegate: TimeTravelStoreFactory())
        database = MemoryTodoDatabase()
        dispatchers = DefaultDispatchers.shared
        
        timeTravelServer = TimeTravelServer()
        timeTravelServer.start()
    }

    deinit {
        timeTravelServer.stop()
    }
}
