//
//  AutoCancellable.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 5/11/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Combine

class AutoCancellable {
    private let cancellable: AnyCancellable
    
    init(cancellable: AnyCancellable) {
        self.cancellable = cancellable
    }
    
    deinit {
        cancellable.cancel()
    }
}
