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
    private let lifecycle = LifecycleRegistry()
    private let controllerDeps = ControllerDeps()
    private let controller: TodoListController

    
    override init() {
        controller = TodoListReaktiveController(
            dependencies: TodoListControllerDeps(
                storeFactory: controllerDeps.storeFactory,
                database: controllerDeps.database,
                lifecycle: lifecycle,
                stateKeeperProvider: nil
            )
        )
    }
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).

        // Create the SwiftUI view that provides the window contents.
        let todoList = TodoList()
        let todoAdd = TodoAdd()
        
        let v = todoList.listView
        let a = todoAdd.addView
        
        self.lifecycle.onCreate()
        controller.onViewCreated(todoListView: v, todoAddView: a, viewLifecycle: lifecycle)

        // Use a UIHostingController as window root view controller.
        if let windowScene = scene as? UIWindowScene {
            let window = UIWindow(windowScene: windowScene)
            window.rootViewController = UIHostingController(rootView:
                ContentView(
                    mainView: VStack {
                        todoList
                        todoAdd
                    }.onAppear() { [weak self] in
                        self?.lifecycle.onStart()
                        self?.lifecycle.onResume()
                    }.onDisappear() { [weak self] in
                        self?.lifecycle.onPause()
                        self?.lifecycle.onStop()
                    }
                ).environmentObject(controllerDeps)
            )
            self.window = window
            window.makeKeyAndVisible()
        }
    }

    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will move from an active state to an inactive state.
        // This may occur due to temporary interruptions (ex. an incoming phone call).
        
        self.lifecycle.onDestroy()
    }
}
