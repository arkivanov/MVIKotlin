//
//  ContentView.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 24/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI

struct ContentView<V : View>: View {
    let mainView: V
    @State var isDrawerOpen: Bool = false
    
    var body: some View {
        NavigationView {
            ZStack {
                mainView
                
                if (isDrawerOpen) {
                    DebugDrawer(isOpen: true)
                        .background(Color.black.opacity(0.5))
                } else {
                    DebugDrawer(isOpen: false)
                }
            }
            .navigationBarTitle(Text("MviKotlin"))
            .navigationBarItems(
                trailing: Button(
                    action: {
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
                            self.isDrawerOpen.toggle()
                        }},
                    label: { Image(systemName: "sidebar.left") })
            )
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(mainView: Text(""))
    }
}
