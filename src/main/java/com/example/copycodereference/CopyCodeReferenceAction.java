package com.example.copycodereference;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.StringSelection;

public class CopyCodeReferenceAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        if (project == null || editor == null || virtualFile == null) {
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        Document document = editor.getDocument();

        int startOffset = selectionModel.getSelectionStart();
        int endOffset = selectionModel.getSelectionEnd();

        int startLine = document.getLineNumber(startOffset) + 1;
        int endLine = document.getLineNumber(endOffset) + 1;

        if (endOffset > startOffset) {
            int endLineIndex = document.getLineNumber(endOffset);
            if (endOffset == document.getLineStartOffset(endLineIndex)) {
                endLine = endLineIndex;
            }
        }

        String projectBasePath = project.getBasePath();
        String filePath = virtualFile.getPath();
        String relativePath = filePath;

        if (projectBasePath != null && filePath.startsWith(projectBasePath)) {
            relativePath = filePath.substring(projectBasePath.length());
            if (relativePath.startsWith("/") || relativePath.startsWith("\\")) {
                relativePath = relativePath.substring(1);
            }
        }

        String reference;
        if (startLine == endLine) {
            reference = relativePath + ":" + startLine;
        } else {
            reference = relativePath + ":" + startLine + "~" + endLine;
        }

        StringSelection clipboardSelection = new StringSelection(reference);
        CopyPasteManager.getInstance().setContents(clipboardSelection);
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);

        e.getPresentation().setEnabledAndVisible(project != null && editor != null && virtualFile != null);
    }
}
