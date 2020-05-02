//
//  TimeTravelView.swift
//  todo-app-ios
//
//  Created by Arkadii Ivanov on 24/02/2020.
//  Copyright © 2020 arkivanov. All rights reserved.
//

import Foundation
import UIKit
import TodoLib

class TimeTravelViewController : UIViewController, UITableViewDelegate, UITableViewDataSource, Observer {

    private let colorSelectedCell = UIColor(red: 0.9, green: 0.9, blue: 0.9, alpha: 1)
    private weak var tableView: UITableView?
    private weak var recordButton: UIButton?
    private weak var stopButton: UIButton?
    private weak var moveToStartButton: UIButton?
    private weak var stepBackwardButton: UIButton?
    private weak var stepForwardButton: UIButton?
    private weak var moveToEndButton: UIButton?
    private weak var cancelButton: UIButton?
    private var disposable: Disposable?
    private var state: TimeTravelState?
    private let timeTravelController = TimeTravelControllerProviderKt.timeTravelController

    override func viewDidLoad() {
        super.viewDidLoad()
       
        let tableView = setupTableView()
        let buttons = setupButtons()
        alignViews(tableView: tableView, buttons: buttons)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        disposable = timeTravelController.states(observer: self)
    }

    override func viewDidDisappear(_ animated: Bool) {
        disposable?.dispose()
        disposable = nil
        state = nil

        super.viewDidDisappear(animated)
    }
    
    func onNext(value: Any?) {
        state = value as? TimeTravelState
        updateButtons()
        tableView?.reloadData()
    }
    
    private func updateButtons() {
        guard let mode = self.state?.mode else { return }
        
        switch mode {
        case .idle:
            recordButton?.isHidden = false
            stopButton?.isHidden = true
            moveToStartButton?.isHidden = true
            stepBackwardButton?.isHidden = true
            stepForwardButton?.isHidden = true
            moveToEndButton?.isHidden = true
            cancelButton?.isHidden = true
        case .recording:
            recordButton?.isHidden = true
            stopButton?.isHidden = false
            moveToStartButton?.isHidden = true
            stepBackwardButton?.isHidden = true
            stepForwardButton?.isHidden = true
            moveToEndButton?.isHidden = true
            cancelButton?.isHidden = false
        case .stopped:
            recordButton?.isHidden = true
            stopButton?.isHidden = true
            moveToStartButton?.isHidden = false
            stepBackwardButton?.isHidden = false
            stepForwardButton?.isHidden = false
            moveToEndButton?.isHidden = false
            cancelButton?.isHidden = false
        default:
            break
        }
    }
    
    func onComplete() {
    }

    private func setupTableView() -> UITableView {
        let tableView = UITableView()
        tableView.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(tableView)
        tableView.delegate = self
        tableView.dataSource = self
        tableView.rowHeight = UITableView.automaticDimension
        //tableView.register(UITableViewCell.self, forCellReuseIdentifier: "TimeTravelEvent")
        self.tableView = tableView
        
        return tableView
    }
    
    private func setupButtons() -> UIStackView {
        let recordButton = createButton(imageSystemName: "circle.fill", clickAction: #selector(self.onStartRecordingClick))
        self.recordButton = recordButton
        let stopButton = createButton(imageSystemName: "stop.fill", clickAction: #selector(self.onStopRecordingClick))
        self.stopButton = stopButton
        let moveToStartButton = createButton(imageSystemName: "backward.end.fill", clickAction: #selector(self.onMoveToStartClick))
        self.moveToStartButton = moveToStartButton
        let stepBackwardButton = createButton(imageSystemName: "chevron.left", clickAction: #selector(self.onStepBackwardClick))
        self.stepBackwardButton = stepBackwardButton
        let stepForwardButton = createButton(imageSystemName: "chevron.right", clickAction: #selector(self.onStepForwardClick))
        self.stepForwardButton = stepForwardButton
        let moveToEndButton = createButton(imageSystemName: "forward.end.fill", clickAction: #selector(self.onMoveToEndClick))
        self.moveToEndButton = moveToEndButton
        let cancelButton = createButton(imageSystemName: "xmark", clickAction: #selector(self.onCancelClick))
        self.cancelButton = cancelButton

        let stack = UIStackView()
        stack.axis = NSLayoutConstraint.Axis.horizontal
        stack.distribution = UIStackView.Distribution.fillEqually
        stack.alignment = UIStackView.Alignment.center
        stack.spacing = 16.0
        stack.addArrangedSubview(recordButton)
        stack.addArrangedSubview(stopButton)
        stack.addArrangedSubview(moveToStartButton)
        stack.addArrangedSubview(stepBackwardButton)
        stack.addArrangedSubview(stepForwardButton)
        stack.addArrangedSubview(moveToEndButton)
        stack.addArrangedSubview(cancelButton)
        stack.translatesAutoresizingMaskIntoConstraints = false
        self.view.addSubview(stack)
        
        return stack
    }
    
    @objc private func onStartRecordingClick() {
        timeTravelController.startRecording()
    }
    
    @objc private func onStopRecordingClick() {
        timeTravelController.stopRecording()
    }

    @objc private func onMoveToStartClick() {
        timeTravelController.moveToStart()
    }
    
    @objc private func onStepBackwardClick() {
        timeTravelController.stepBackward()
    }
    
    @objc private func onStepForwardClick() {
        timeTravelController.stepForward()
    }
    
    @objc private func onMoveToEndClick() {
        timeTravelController.moveToEnd()
    }
    
    @objc private func onCancelClick() {
        timeTravelController.cancel()
    }

    private func createButton(imageSystemName: String, clickAction: Selector) -> UIButton {
        let button = UIButton(type: .system)
        button.setTitle(title, for: .normal)
        button.addTarget(self, action: clickAction, for: .touchUpInside)
        button.setImage(UIImage(systemName: imageSystemName), for: .normal)
        
        return button
    }
    
    private func alignViews(tableView: UITableView, buttons: UIStackView) {
        tableView.topAnchor.constraint(equalTo:view.topAnchor).isActive = true
        tableView.leftAnchor.constraint(equalTo:view.leftAnchor).isActive = true
        tableView.rightAnchor.constraint(equalTo:view.rightAnchor).isActive = true
        tableView.bottomAnchor.constraint(equalTo:buttons.topAnchor).isActive = true

        buttons.leftAnchor.constraint(equalTo:view.leftAnchor).isActive = true
        buttons.rightAnchor.constraint(equalTo:view.rightAnchor).isActive = true
        buttons.bottomAnchor.constraint(equalTo:view.bottomAnchor).isActive = true
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return state?.events.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "TimeTravelEvent") as? Cell
            ?? Cell(style: .subtitle, reuseIdentifier: "TimeTravelEvent")

        guard let state = self.state else { return cell }
        let item = state.events[indexPath.row]
        let value = item.value as AnyObject
        cell.event = item
        cell.myTitle.text = item.storeName
        cell.myText.text = String(value.description?.prefix(200) ?? "")
        cell.myDebugButton.isHidden = item.type == .state
        cell.backgroundColor = state.selectedEventIndex == indexPath.row ? colorSelectedCell : UIColor.white

        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        guard let state = self.state else { return }
        let item = state.events[indexPath.row]
        let value = item.value as AnyObject

        let alert = UIAlertController(title: item.storeName, message: String(value.description ?? ""), preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: "Close", style: .cancel, handler: nil))

        self.present(alert, animated: true)
    }

    private class Cell : UITableViewCell {
        let myTitle = UILabel()
        let myText = UILabel()
        let myDebugButton = UIButton()
        var event: TimeTravelEvent?
        
        override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
            super.init(style: style, reuseIdentifier: reuseIdentifier)
        
            myTitle.translatesAutoresizingMaskIntoConstraints = false
            myText.translatesAutoresizingMaskIntoConstraints = false
            myDebugButton.translatesAutoresizingMaskIntoConstraints = false

            myTitle.font = UIFont.systemFont(ofSize: 16)
            myText.font = UIFont.systemFont(ofSize: 12)
            myText.numberOfLines = 3
            myDebugButton.setImage(UIImage(systemName: "play.fill") , for: .normal)
            myDebugButton.addTarget(self, action: #selector(self.onDebugClick), for: .touchUpInside)

            contentView.addSubview(myTitle)
            contentView.addSubview(myText)
            contentView.addSubview(myDebugButton)
                       
            myTitle.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 8).isActive = true
            myTitle.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 8).isActive = true
            myTitle.trailingAnchor.constraint(equalTo: myDebugButton.leadingAnchor).isActive = true
            
            myText.topAnchor.constraint(equalTo: myTitle.bottomAnchor, constant: 4).isActive = true
            myText.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 8).isActive = true
            myText.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -8).isActive = true
            myText.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -8).isActive = true

            myDebugButton.trailingAnchor.constraint(equalTo: contentView.trailingAnchor).isActive = true
            myDebugButton.topAnchor.constraint(equalTo: contentView.topAnchor).isActive = true
            myDebugButton.widthAnchor.constraint(equalToConstant: 32).isActive = true
            myDebugButton.heightAnchor.constraint(equalToConstant: 32).isActive = true
        }
        
        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }
        
        @objc private func onDebugClick() {
            guard let event = self.event else { return }
            TimeTravelControllerProviderKt.timeTravelController.debugEvent(eventId: event.id)
        }
    }
}
