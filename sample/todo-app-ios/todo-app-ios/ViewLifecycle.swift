//
//  ViewLifecycle.swift
//  todo-app-ios
//
//  Created by stream on 4/29/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import TodoLib

class ViewLifecycle {
    var lifecycle = LifecycleRegistry()
    
    init() {
        self.lifecycle.onCreate()
    }
    
    deinit {
        self.lifecycle.onDestroy()
    }
}
