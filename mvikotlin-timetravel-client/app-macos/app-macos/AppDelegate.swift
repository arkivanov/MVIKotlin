//
//  AppDelegate.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/25/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import Cocoa
import SwiftUI
import TimeTravelClient

@NSApplicationMain
class AppDelegate: NSObject, NSApplicationDelegate {

    var window: NSWindow!
    
    private var client: TimeTravelClient?


    func applicationDidFinishLaunching(_ aNotification: Notification) {
        let viewProxy = TimeTravelViewProxy()
        self.client = TimeTravelClientFactoryKt.TimeTravelClient(view: viewProxy)
        
        self.client?.onCreate()
        // Create the SwiftUI view that provides the window contents.
        let view = TimeTravelView(proxy: viewProxy)
        // Create the window and set the content view.
        window = NSWindow(
            contentRect: NSRect(x: 0, y: 0, width: 1024, height: 768),
            styleMask: [.titled, .closable, .miniaturizable, .resizable, .fullSizeContentView],
            backing: .buffered, defer: false)
        window.center()
        window.setFrameAutosaveName("Main Window")
        window.contentView = NSHostingView(rootView: view)
        window.makeKeyAndOrderFront(nil)
        window.title = "MVIKotlin Time Travel Client"
    }

    func applicationWillTerminate(_ aNotification: Notification) {
        self.client?.onDestroy()
    }
}

