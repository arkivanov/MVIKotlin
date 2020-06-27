//
//  ValueExt.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/26/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import Foundation
import TimeTravelClient

extension Value {
    func getValueText() -> String {
        switch self {
        case let obj as ObjectUnparsed: return obj.value
        default: return description
        }
    }
}
