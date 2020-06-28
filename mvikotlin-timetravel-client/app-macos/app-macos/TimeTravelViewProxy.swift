//
//  TimeTravelViewProxy.swift
//  app-macos
//
//  Created by Arkadii Ivanov on 6/25/20.
//  Copyright © 2020 Arkadii Ivanov. All rights reserved.
//

import TimeTravelClient

class TimeTravelViewProxy: BaseMviView<TimeTravelClientViewModel, TimeTravelClientViewEvent>, TimeTravelClientView, ObservableObject     {
    
    @Published var model: TimeTravelClientViewModel
    @Published var errorMessage: String?
    
    init(model: TimeTravelClientViewModel) {
        self.model = model
    }
    
    override init() {
        self.model = TimeTravelClientViewModel(
            events: [],
            currentEventIndex: -1,
            buttons: TimeTravelClientViewModel.Buttons(
                isConnectEnabled: false,
                isDisconnectEnabled: false,
                isStartRecordingEnabled: false,
                isStopRecordingEnabled: false,
                isMoveToStartEnabled: false,
                isStepBackwardEnabled: false,
                isStepForwardEnabled: false,
                isMoveToEndEnabled: false,
                isCancelEnabled: false,
                isDebugEventEnabled: false,
                isExportEventsEnabled: false,
                isImportEventsEnabled: false
            ),
            selectedEventIndex: -1,
            selectedEventValue: nil
        )
    }
    
    override func render(model: TimeTravelClientViewModel) {
        self.model = model
    }
    
    func execute(action: TimeTravelClientViewAction) {
        switch action {
        case let showError as TimeTravelClientViewAction.ShowError: errorMessage = showError.text
        default: break
        }
    }
    
    func showError(text: String) {
        errorMessage = text
    }
}
