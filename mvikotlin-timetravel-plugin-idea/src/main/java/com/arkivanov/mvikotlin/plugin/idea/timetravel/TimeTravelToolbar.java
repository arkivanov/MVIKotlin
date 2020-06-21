package com.arkivanov.mvikotlin.plugin.idea.timetravel;

import com.arkivanov.mvikotlin.timetravel.proto.internal.data.timetravelstateupdate.TimeTravelStateUpdate;
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
    private final Runnable onDebugListener;

    @NotNull
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    @NotNull
    private TimeTravelStateUpdate.Mode mode = TimeTravelStateUpdate.Mode.IDLE;
    private boolean isDebugEnabled = false;

    public TimeTravelToolbar(@NotNull Listener listener, @NotNull Runnable onDebugListener) {
        this.listener = listener;
        this.onDebugListener = onDebugListener;

        this.toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.COMMANDER_TOOLBAR, actionGroup(), true);
    }

    @NotNull
    public JComponent getComponent() {
        return toolbar.getComponent();
    }

    public void setConnectionStatus(@NotNull ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
        toolbar.updateActionsImmediately();
    }

    public void setMode(@NotNull TimeTravelStateUpdate.Mode mode) {
        this.mode = mode;
        toolbar.updateActionsImmediately();
    }

    public void setDebugEnabled(boolean isDebugEnabled) {
        this.isDebugEnabled = isDebugEnabled;
        toolbar.updateActionsImmediately();
    }

    public void update(@NotNull ConnectionStatus connectionStatus, @NotNull TimeTravelStateUpdate.Mode mode, boolean isDebugEnabled) {
        this.connectionStatus = connectionStatus;
        this.mode = mode;
        this.isDebugEnabled = isDebugEnabled;
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

        return group;
    }

    @NotNull
    private AnAction connectAction() {
        return anAction(
                "Connect",
                AllIcons.Debugger.AttachToProcess,
                (event) -> event.getPresentation().setEnabled(isDisconnected()),
                listener::onConnect
        );
    }

    @NotNull
    private AnAction disconnectAction() {
        return anAction(
                "Disconnect",
                AllIcons.Debugger.Db_invalid_breakpoint,
                (event) -> event.getPresentation().setEnabled(!isDisconnected()),
                listener::onDisconnect
        );
    }

    @NotNull
    private AnAction startRecordingAction() {
        return anAction(
                "Start recording",
                AllIcons.Debugger.Db_set_breakpoint,
                (event) -> event.getPresentation().setEnabled(isConnected() && isIdle()),
                listener::onStartRecording
        );
    }

    @NotNull
    private AnAction stopRecordingAction() {
        return anAction(
                "Stop recording",
                AllIcons.Actions.Suspend,
                (event) -> event.getPresentation().setEnabled(isConnected() && isRecording()),
                listener::onStopRecording
        );
    }

    @NotNull
    private AnAction moveToStartAction() {
        return anAction(
                "Move to start",
                AllIcons.Actions.Play_first,
                (event) -> event.getPresentation().setEnabled(isConnected() && isStopped()),
                listener::onMoveToStart
        );
    }

    @NotNull
    private AnAction stepBackwardAction() {
        return anAction(
                "Step backward",
                AllIcons.Actions.Play_back,
                (event) -> event.getPresentation().setEnabled(isConnected() && isStopped()),
                listener::onStepBackward
        );
    }

    @NotNull
    private AnAction stepForwardAction() {
        return anAction(
                "Step forward",
                AllIcons.Actions.Play_forward,
                (event) -> event.getPresentation().setEnabled(isConnected() && isStopped()),
                listener::onStepForward
        );
    }

    @NotNull
    private AnAction moveToEndAction() {
        return anAction(
                "Move to end",
                AllIcons.Actions.Play_last,
                (event) -> event.getPresentation().setEnabled(isConnected() && isStopped()),
                listener::onMoveToEnd
        );
    }

    @NotNull
    private AnAction cancelAction() {
        return anAction(
                "Cancel",
                AllIcons.Actions.Cancel,
                (event) -> event.getPresentation().setEnabled(isConnected() && !isIdle()),
                listener::onCancel
        );
    }

    @NotNull
    private AnAction debugAction() {
        return anAction(
                "Debug",
                AllIcons.Actions.StartDebugger,
                (event) -> event.getPresentation().setEnabled(isConnected() && isStopped() && isDebugEnabled),
                onDebugListener
        );
    }

    private boolean isIdle() {
        return mode == TimeTravelStateUpdate.Mode.IDLE;
    }

    private boolean isRecording() {
        return mode == TimeTravelStateUpdate.Mode.RECORDING;
    }

    private boolean isStopped() {
        return mode == TimeTravelStateUpdate.Mode.STOPPED;
    }

    private boolean isConnected() {
        return connectionStatus == ConnectionStatus.CONNECTED;
    }

    private boolean isDisconnected() {
        return connectionStatus == ConnectionStatus.DISCONNECTED;
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
    }
}
