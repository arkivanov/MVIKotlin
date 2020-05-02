//
//  ViewLifecycle.swift
//  todo-app-ios
//
//  Created by stream on 4/29/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import TodoLib

class LifecycleWrapper {

    let lifecycle = LifecycleRegistry()
    
    init() {
        self.lifecycle.onCreate()
    }
    
    deinit {
        self.lifecycle.onDestroy()
    }
    
    func start() {
        lifecycle.onStart()
        lifecycle.onResume()
    }
    
    func stop() {
        lifecycle.onPause()
        lifecycle.onStop()
    }
}
