//
//  ImageButton.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/26/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import SwiftUI

struct ImageButton : View {
    private let name: String
    private let action: () -> Void
    
    init(_ name: String, _ action: @escaping () -> Void) {
        self.name = name
        self.action = action
    }
    
    var body: some View {
        return Button(action: action) { Image(name).frame(width: 32, height: 32) }
            .buttonStyle(LinkButtonStyle())
            .contentShape(Rectangle())
    }
}

struct ImageButton_Previews: PreviewProvider {
    static var previews: some View {
        ImageButton("debug") {}
    }
}
