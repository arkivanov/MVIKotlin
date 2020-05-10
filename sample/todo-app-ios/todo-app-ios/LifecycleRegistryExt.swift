//
//  LifecycleRegistryExt.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 5/12/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import TodoLib

extension LifecycleRegistry {
    
    func resume() {
        if (state == .initialized) {
            onCreate()
        }

        if (state == .created) {
            onStart()
        }

        if (state == .started) {
            onResume()
        }
    }
    
    func stop() {
        if (state == .resumed) {
            onPause()
        }

        if (state == .started) {
            onStop()
        }
    }
    
    func destroy() {
        stop()
        
        if (state == .created) {
            onDestroy()
        }
    }
}
