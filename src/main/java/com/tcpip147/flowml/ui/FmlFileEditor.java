package com.tcpip147.flowml.ui;

import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.NavigatableFileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public class FmlFileEditor implements NavigatableFileEditor {

    public static final Key<FmlContext> CONTEXT = Key.create("CONTEXT");
    private final FmlContext ctx;
    private FmlChartEditor fmlChartEditor;

    public FmlFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        ctx = new FmlContext(project, file, this);
        file.putUserData(CONTEXT, ctx);
        this.fmlChartEditor = new FmlChartEditor(ctx);
    }

    @Override
    public @NotNull JComponent getComponent() {
        return fmlChartEditor;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return null;
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "QmlFileEditor";
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {

    }

    @Override
    public boolean isModified() {
        return ctx.isModified();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
        ctx.setPropertyChangeListener(propertyChangeListener);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T t) {

    }

    @Override
    public boolean canNavigateTo(@NotNull Navigatable navigatable) {
        return true;
    }

    @Override
    public void navigateTo(@NotNull Navigatable navigatable) {

    }

    @Override
    public VirtualFile getFile() {
        return ctx.getFile();
    }
}
