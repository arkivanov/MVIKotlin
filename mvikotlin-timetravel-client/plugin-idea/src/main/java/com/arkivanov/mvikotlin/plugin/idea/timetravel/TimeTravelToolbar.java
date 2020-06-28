package com.arkivanov.mvikotlin.plugin.idea.timetravel;

import com.arkivanov.mvikotlin.timetravel.client.internal.TimeTravelClientView;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JComponent;

public class TimeTravelToolbar {

    @NotNull
    private final Listener listener;
    @NotNull
    private final ActionToolbar toolbar;

    @NotNull
    private TimeTravelClientView.Model.Buttons buttons =
            new TimeTravelClientView.Model.Buttons(
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false,
                    false
            );

    public TimeTravelToolbar(@NotNull Listener listener) {
        this.listener = listener;

        this.toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.COMMANDER_TOOLBAR, actionGroup(), true);
    }

    @NotNull
    public JComponent getComponent() {
        return toolbar.getComponent();
    }

    public void render(@NotNull TimeTravelClientView.Model.Buttons buttons) {
        this.buttons = buttons;
        toolbar.updateActionsImmediately();
    }

    @NotNull
    private DefaultActionGroup actionGroup() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.addAll(connectAction(), disconnectAction());
        group.addSeparator();
        group.addAll(
                startRecordingAction(),
                stopRecordingAction(),
                moveToStartAction(),
                stepBackwardAction(),
                stepForwardAction(),
                moveToEndAction(),
                cancelAction()
        );
        group.addSeparator();
        group.add(debugAction());
        group.addSeparator();
        group.add(exportAction());
        group.add(importAction());

        return group;
    }

    @NotNull
    private AnAction connectAction() {
        return anAction(
                "Connect",
                AllIcons.Debugger.AttachToProcess,
                (event) -> event.getPresentation().setEnabled(buttons.isConnectEnabled()),
                listener::onConnect
        );
    }

    @NotNull
    private AnAction disconnectAction() {
        return anAction(
                "Disconnect",
                AllIcons.Debugger.Db_invalid_breakpoint,
                (event) -> event.getPresentation().setEnabled(buttons.isDisconnectEnabled()),
                listener::onDisconnect
        );
    }

    @NotNull
    private AnAction startRecordingAction() {
        return anAction(
                "Start recording",
                AllIcons.Debugger.Db_set_breakpoint,
                (event) -> event.getPresentation().setEnabled(buttons.isStartRecordingEnabled()),
                listener::onStartRecording
        );
    }

    @NotNull
    private AnAction stopRecordingAction() {
        return anAction(
                "Stop recording",
                AllIcons.Actions.Suspend,
                (event) -> event.getPresentation().setEnabled(buttons.isStopRecordingEnabled()),
                listener::onStopRecording
        );
    }

    @NotNull
    private AnAction moveToStartAction() {
        return anAction(
                "Move to start",
                AllIcons.Actions.Play_first,
                (event) -> event.getPresentation().setEnabled(buttons.isMoveToStartEnabled()),
                listener::onMoveToStart
        );
    }

    @NotNull
    private AnAction stepBackwardAction() {
        return anAction(
                "Step backward",
                AllIcons.Actions.Play_back,
                (event) -> event.getPresentation().setEnabled(buttons.isMoveToEndEnabled()),
                listener::onStepBackward
        );
    }

    @NotNull
    private AnAction stepForwardAction() {
        return anAction(
                "Step forward",
                AllIcons.Actions.Play_forward,
                (event) -> event.getPresentation().setEnabled(buttons.isStepBackwardEnabled()),
                listener::onStepForward
        );
    }

    @NotNull
    private AnAction moveToEndAction() {
        return anAction(
                "Move to end",
                AllIcons.Actions.Play_last,
                (event) -> event.getPresentation().setEnabled(buttons.isMoveToEndEnabled()),
                listener::onMoveToEnd
        );
    }

    @NotNull
    private AnAction cancelAction() {
        return anAction(
                "Cancel",
                AllIcons.Actions.Cancel,
                (event) -> event.getPresentation().setEnabled(buttons.isCancelEnabled()),
                listener::onCancel
        );
    }

    @NotNull
    private AnAction debugAction() {
        return anAction(
                "Debug selected event",
                AllIcons.Actions.StartDebugger,
                (event) -> event.getPresentation().setEnabled(buttons.isDebugEventEnabled()),
                listener::onDebug
        );
    }

    @NotNull
    private AnAction exportAction() {
        return anAction(
                "Export events",
                AllIcons.ToolbarDecorator.Export,
                (event) -> event.getPresentation().setEnabled(buttons.isExportEventsEnabled()),
                listener::onExport
        );
    }

    @NotNull
    private AnAction importAction() {
        return anAction(
                "Import events",
                AllIcons.ToolbarDecorator.Import,
                (event) -> event.getPresentation().setEnabled(buttons.isImportEventsEnabled()),
                listener::onImport
        );
    }

    @NotNull
    private AnAction anAction(
            @Nullable String text,
            @Nullable Icon icon,
            @Nullable Consumer<AnActionEvent> onUpdate,
            @NotNull Runnable onAction
    ) {
        final AnAction action =
                new AnAction() {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent event) {
                        onAction.run();
                    }

                    @Override
                    public void update(@NotNull AnActionEvent event) {
                        if (onUpdate != null) {
                            onUpdate.accept(event);
                        }
                    }
                };

        final Presentation presentation = action.getTemplatePresentation();
        presentation.setText(text);
        presentation.setIcon(icon);

        return action;
    }

    interface Listener {
        void onConnect();

        void onDisconnect();

        void onStartRecording();

        void onStopRecording();

        void onMoveToStart();

        void onStepBackward();

        void onStepForward();

        void onMoveToEnd();

        void onCancel();

        void onDebug();

        void onExport();

        void onImport();
    }
}
