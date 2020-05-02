//
//  LazyView.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 02/05/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI

struct LazyView<Content: View>: View {
    let build: () -> Content
    init(_ build: @autoclosure @escaping () -> Content) {
        self.build = build
    }
    var body: Content {
        build()
    }
}
