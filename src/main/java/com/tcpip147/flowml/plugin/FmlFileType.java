package com.tcpip147.flowml.plugin;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class FmlFileType extends LanguageFileType {

    public static final FmlFileType INSTANCE = new FmlFileType();
    public static final Icon ICON = IconLoader.getIcon("/jar-gray.png", FmlFileType.class);
    public static final String EXTENSION = "flowml";

    protected FmlFileType() {
        super(XMLLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "FlowML File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "Flow Chart Management Language File";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return EXTENSION;
    }

    @Override
    public Icon getIcon() {
        return ICON;
    }
}
