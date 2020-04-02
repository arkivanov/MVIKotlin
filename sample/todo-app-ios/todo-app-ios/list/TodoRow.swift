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

    var onItemClicked: () -> Void
    var onDoneClicked: () -> Void
    var onDeleteClicked: () -> Void
    
    var body: some View {
        Button(action: self.onItemClicked) {
            HStack {
                Image(systemName: isDone ? "checkmark.square" : "square")
                    .onTapGesture(perform: self.onDoneClicked)
                
                Text(text)
                    .frame(minWidth: nil, idealWidth: nil, maxWidth: .infinity, minHeight: nil, idealHeight: nil, maxHeight: .infinity, alignment: .leading)
                
                Button(action: self.onDeleteClicked) {
                    Image(systemName: "trash")
                }
                
            }
        }
    }
}

struct TodoRow_Previews: PreviewProvider {
    static var previews: some View {
        TodoRow(text: "Item text", isDone: false, onItemClicked: {}, onDoneClicked: {}, onDeleteClicked: {})
    }
}
