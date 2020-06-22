//
//  EventValueView.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/26/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import SwiftUI
import TimeTravelClient

struct EventValueView: View {
    let value: Value?
    
    var body: some View {
        ScrollView {
            Text(value?.getValueText() ?? "")
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(8)
        }
    }
}

struct EventValueView_Previews: PreviewProvider {
    static var previews: some View {
        EventValueView(value: .ObjectUnparsed(type: "Type", value: "Value"))
    }
}
