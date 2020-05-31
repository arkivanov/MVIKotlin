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
        self.lifecycle.destroy()
    }
    
    func start() {
        lifecycle.resume()
    }
    
    func stop() {
        lifecycle.stop()
    }
}
