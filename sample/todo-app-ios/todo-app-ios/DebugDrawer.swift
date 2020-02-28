//
//  DebugDrawer.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 25/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI

struct DebugDrawer: View {
    private let margin: CGFloat = 96
    private let width = UIScreen.main.bounds.width - 96
    let isOpen: Bool
    
    var body: some View {
        HStack {
            TimeTravelView()
                .background(Color.white)
                .frame(width: self.width)
                .offset(x: self.isOpen ? self.margin : self.width + self.margin)
                .animation(.default)
            Spacer()
        }
    }
}

struct DebugDrawer_Previews: PreviewProvider {
    static var previews: some View {
        DebugDrawer(isOpen: false)
    }
}
