//
//  TodoDetail.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 3/31/20.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI
import TodoLib

struct TodoDetails: View {
    var id: String
    
    @ObservedObject var detailsView = TodoDetailsViewImpl()
    
    var body: some View {
        Text(id)
    }
}

struct TodoDetails_Previews: PreviewProvider {
    static var previews: some View {
        TodoDetails(id: "")
    }
}
