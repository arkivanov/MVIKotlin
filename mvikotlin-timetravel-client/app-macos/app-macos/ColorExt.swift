//
//  ColorExt.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/26/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import SwiftUI

extension Color {
    init(red: Int, green: Int, blue: Int) {
       assert(red >= 0 && red <= 255, "Invalid red component")
       assert(green >= 0 && green <= 255, "Invalid green component")
       assert(blue >= 0 && blue <= 255, "Invalid blue component")

       self.init(red: Double(red) / 255.0, green: Double(green) / 255.0, blue: Double(blue) / 255.0)
   }

   init(rgb: Int) {
       self.init(red: (rgb >> 16) & 0xFF, green: (rgb >> 8) & 0xFF, blue: rgb & 0xFF)
   }
}
