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
                database: holder.database
            )
        }
    }
}

private class Holder : ObservableObject {
    let storeFactory: StoreFactory
    let database: TodoDatabase
    private let timeTravelServer: TimeTravelServer

    init() {
        storeFactory = LoggingStoreFactory(delegate: TimeTravelStoreFactory())
        database = DefaultTodoDatabase()
        
        timeTravelServer = TimeTravelServer()
        timeTravelServer.start()
    }

    deinit {
        timeTravelServer.stop()
    }
}
