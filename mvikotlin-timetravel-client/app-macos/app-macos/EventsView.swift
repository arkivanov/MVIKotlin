//
//  EventsView.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/26/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import SwiftUI
import TimeTravelClient

private let COLOR_SELECTED = Color(rgb: 0xBBDEFB)
private let COLOR_CURRENT = Color(rgb: 0xF0F0F0)

struct EventsView: View {
    let events: [String]
    let currentIndex: Int32
    let selectedIndex: Int32
    let dispatch: (TimeTravelClientViewEvent) -> Void
    
    var body: some View {
        List() {
            ForEach(events.indices, id: \.self) { index in
                Text(self.events[index])
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .contentShape(Rectangle())
                    .listRowBackground(self.getItemColor(index))
                    .onTapGesture { self.dispatch(.EventSelected(index: Int32(index))) }
            }
        }
    }
    
    private func getItemColor(_ index: Int) -> Color {
        if (index == selectedIndex) {
            return COLOR_SELECTED
        } else if (index == currentIndex) {
            return COLOR_CURRENT
        } else {
            return Color.clear
        }
    }
}

struct EventsView_Previews: PreviewProvider {
    static var previews: some View {
        EventsView(events: ["Item1", "Item2", "Item3"], currentIndex: 0, selectedIndex: 1) { _ in }
    }
}
