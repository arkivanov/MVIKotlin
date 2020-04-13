//
//  ControllerDeps.swift
//  todo-app-ios
//
//  Created by stream on 4/13/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Foundation
import TodoLib

class ControllerDeps: ObservableObject {
    @Published var storeFactory = LoggingStoreFactory(
        delegate: TimeTravelStoreFactory(fallback: DefaultStoreFactory()),
        logger: DefaultLogger(),
        mode: LoggingMode.full,
        eventTypes: StoreEventType.Companion().ALL
    )
    
    @Published var database = TodoDatabaseImpl()
}
