//
//  SceneDelegate.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 19/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import UIKit
import SwiftUI
import TodoLib

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    
    var window: UIWindow?
    private let controllerDeps = ControllerDeps()
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
        
        // Create the SwiftUI view that provides the window contents.
        
        // Use a UIHostingController as window root view controller.
        if let windowScene = scene as? UIWindowScene {
            let window = UIWindow(windowScene: windowScene)
            window.rootViewController = UIHostingController(rootView:
                ContentView(
                    mainView: RootView(controllerDeps: controllerDeps)
                )
            )
            self.window = window
            window.makeKeyAndVisible()
        }
    }
}
