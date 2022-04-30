//
//  LazyView.swift
//  app-ios (iOS)
//
//  Created by Arkadii Ivanov on 21/04/2022.
//

import SwiftUI

struct LazyView<Content: View>: View {
    var build: () -> Content

    var body: Content {
        build()
    }
}
