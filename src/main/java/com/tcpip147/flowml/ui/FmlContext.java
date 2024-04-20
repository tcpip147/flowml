package com.tcpip147.flowml.ui;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FmlContext {

    private Project project;
    private VirtualFile file;
    private FmlFileEditor fileEditor;
    private PropertyChangeListener propertyChangeListener;
    private boolean modified;
    private String prevUndoText;
    private FmlCanvas canvas;
    private FmlController controller;

    public FmlContext(Project project, VirtualFile file, FmlFileEditor fileEditor) {
        this.project = project;
        this.file = file;
        this.fileEditor = fileEditor;
    }

    public Project getProject() {
        return project;
    }

    public VirtualFile getFile() {
        return file;
    }

    public FmlFileEditor getFileEditor() {
        return fileEditor;
    }

    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
        propertyChangeListener.propertyChange(new PropertyChangeEvent(fileEditor, FileEditor.PROP_MODIFIED, !modified, modified));
    }

    public String getPrevUndoText() {
        return prevUndoText;
    }

    public void setPrevUndoText(String prevUndoText) {
        this.prevUndoText = prevUndoText;
    }

    public FmlCanvas getCanvas() {
        return canvas;
    }

    public void setCanvas(FmlCanvas canvas) {
        this.canvas = canvas;
    }

    public FmlController getController() {
        return controller;
    }

    public void setController(FmlController controller) {
        this.controller = controller;
    }
}
