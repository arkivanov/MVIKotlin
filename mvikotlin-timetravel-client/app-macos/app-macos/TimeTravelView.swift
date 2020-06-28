//
//  TimeTravelView.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/25/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import SwiftUI
import TimeTravelClient

struct TimeTravelView: View {
    @ObservedObject var proxy: TimeTravelViewProxy
    
    var body: some View {
        let model = proxy.model
        
        return VStack(spacing: 0) {
            ButtonsView(buttons: model.buttons, dispatch: dispatch)

            GeometryReader { gemoetry in
                HStack {
                    EventsView(
                        events: model.events,
                        currentIndex: model.currentEventIndex,
                        selectedIndex: model.selectedEventIndex,
                        dispatch: self.dispatch
                    )
                        .frame(maxWidth: gemoetry.size.width * 0.4)
                    
                    EventValueView(value: model.selectedEventValue)
                        .frame(maxWidth: .infinity)
                        .background(Color.white)
                }
            }
        }.alert(item: $proxy.errorMessage) { text in
            Alert(title: Text("MVIKotlin"), message: Text(text), dismissButton: Alert.Button.default(Text("Close")) { self.proxy.errorMessage = nil })
        }
    }
    
    private func dispatch(_ event: TimeTravelClientViewEvent) {
        proxy.dispatch(event: event)
    }
    
}

struct TimeTravelView_Previews: PreviewProvider {
    static var previews: some View {
        TimeTravelView(
            proxy: TimeTravelViewProxy(
                model: TimeTravelClientViewModel(
                    events: ["Item1", "Item2", "Item3"],
                    currentEventIndex: 1,
                    buttons: TimeTravelClientViewModel.Buttons(
                        isConnectEnabled: true,
                        isDisconnectEnabled: false,
                        isStartRecordingEnabled: true,
                        isStopRecordingEnabled: false,
                        isMoveToStartEnabled: true,
                        isStepBackwardEnabled: false,
                        isStepForwardEnabled: true,
                        isMoveToEndEnabled: false,
                        isCancelEnabled: true,
                        isDebugEventEnabled: false,
                        isExportEventsEnabled: false,
                        isImportEventsEnabled: false
                    ),
                    selectedEventIndex: 0,
                    selectedEventValue: Value.ObjectUnparsed(type: "String", value: "Value")
                )
            )
        )
    }
}
