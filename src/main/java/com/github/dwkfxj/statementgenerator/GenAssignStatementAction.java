package com.github.dwkfxj.statementgenerator;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.popup.PopupComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UVariable;
import org.jetbrains.uast.UastContextKt;
import org.jetbrains.uast.UastLanguagePlugin;
import org.jetbrains.uast.UastUtils;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * @author Victor
 */
public class GenAssignStatementAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final VirtualFile[] selectFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);

        boolean menuAllowed =
            selectFiles != null
                && selectFiles.length  == 1
                && Arrays.stream(selectFiles).noneMatch(VirtualFile::isDirectory)
                && project != null;

        e.getPresentation().setEnabledAndVisible(menuAllowed);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Project project = e.getProject();
        final VirtualFile[] selectFiles = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        PsiFile psiFile = PsiUtil.getPsiFile(project,selectFiles[0]);
        if (!uastSupported(psiFile)) {
            Notifier.warn("This file can't copy assign statement.", project);
            return;
        }
        String fileText = psiFile.getText();
        int offset = fileText.contains("class") ? fileText.indexOf("class") : fileText.indexOf("record");
        if (offset < 0) {
            Notifier.warn("Can't find class scope.", project);
            return;
        }
        PsiElement elementAt = psiFile.findElementAt(offset);
        UClass uClass = UastUtils.findContaining(elementAt, UClass.class);
        GenAssignStatementConfigDialog configDialog =  new GenAssignStatementConfigDialog(project,uClass);
        configDialog.show();
    }

    public boolean uastSupported(@NotNull final PsiFile psiFile) {
        return UastLanguagePlugin.Companion.getInstances()
            .stream()
            .anyMatch(l -> l.isFileSupported(psiFile.getName()));
    }
}
