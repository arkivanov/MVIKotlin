//
//  TimeTravelView.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 24/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import SwiftUI

struct TimeTravelView: UIViewControllerRepresentable {
    func makeUIViewController(context: UIViewControllerRepresentableContext<TimeTravelView>) -> TimeTravelViewController {
         return TimeTravelViewController()
    }
    
    func updateUIViewController(_ uiViewController: TimeTravelViewController, context: UIViewControllerRepresentableContext<TimeTravelView>) {
    }
}

struct TimeTravelView_Previews: PreviewProvider {
    static var previews: some View {
        TimeTravelView()
    }
}
