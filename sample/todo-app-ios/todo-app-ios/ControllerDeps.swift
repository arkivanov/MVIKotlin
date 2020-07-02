//
//  ControllerDeps.swift
//  todo-app-ios
//
//  Created by stream on 4/13/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Foundation
import TodoLib

class ControllerDeps {

    let storeFactory = LoggingStoreFactory(delegate: TimeTravelStoreFactory(fallback: DefaultStoreFactory()))
    let database = TodoDatabaseImpl()
}
