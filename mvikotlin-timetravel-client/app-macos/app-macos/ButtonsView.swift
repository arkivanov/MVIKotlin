//
//  ButtonsView.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/26/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import SwiftUI
import TimeTravelClient

struct ButtonsView: View {
    
    let buttons: TimeTravelClientViewModel.Buttons
    let dispatch: (TimeTravelClientViewEvent) -> Void

    var body: some View {
        HStack(spacing: 0) {
            ImageButton("connect") { self.dispatch(.ConnectClicked()) }.disabled(!buttons.isConnectEnabled)
            ImageButton("disconnect") { self.dispatch(.DisconnectClicked()) }.disabled(!buttons.isDisconnectEnabled)
            ImageButton("start_recording") { self.dispatch(.StartRecordingClicked()) }.disabled(!buttons.isStartRecordingEnabled)
            ImageButton("stop_recording") { self.dispatch(.StopRecordingClicked()) }.disabled(!buttons.isStopRecordingEnabled)
            ImageButton("move_to_start") { self.dispatch(.MoveToStartClicked()) }.disabled(!buttons.isMoveToStartEnabled)
            ImageButton("step_backward") { self.dispatch(.StepBackwardClicked()) }.disabled(!buttons.isStepBackwardEnabled)
            ImageButton("step_forward") { self.dispatch(.StepForwardClicked()) }.disabled(!buttons.isStepForwardEnabled)
            ImageButton("move_to_end") { self.dispatch(.MoveToEndClicked()) }.disabled(!buttons.isMoveToEndEnabled)
            ImageButton("cancel") { self.dispatch(.CancelClicked()) }.disabled(!buttons.isCancelEnabled)
            ImageButton("debug") { self.dispatch(.DebugEventClicked()) }.disabled(!buttons.isDebugEventEnabled)
        }
    }
}

struct ButtonsView_Previews: PreviewProvider {
    static var previews: some View {
        ButtonsView(
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
                isDebugEventEnabled: false
            )
        ) { _ in }
    }
}
