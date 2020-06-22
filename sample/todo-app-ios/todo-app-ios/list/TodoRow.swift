//
//  TodoRow.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 22/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoRow: View {
    var text: String
    var isDone: Bool
    
    var onDoneClicked: () -> Void
    var onDeleteClicked: () -> Void
    
    var body: some View {
        HStack {
            Image(systemName: isDone ? "checkmark.square" : "square")
                .onTapGesture(perform: self.onDoneClicked)
            
            Text(text)
                .frame(maxWidth: .infinity, alignment: .leading)
            
            Image(systemName: "trash")
                .onTapGesture(perform: self.onDeleteClicked)
        }
        .frame(minWidth: nil, maxWidth: .infinity, alignment: .leading)
        .background(Color.white)
    }
}

struct TodoRow_Previews: PreviewProvider {
    static var previews: some View {
        TodoRow(text: "Item text", isDone: false, onDoneClicked: {}, onDeleteClicked: {})
    }
}
