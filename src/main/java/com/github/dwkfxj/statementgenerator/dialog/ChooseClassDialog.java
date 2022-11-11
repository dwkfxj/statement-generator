package com.github.dwkfxj.statementgenerator.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class ChooseClassDialog extends DialogWrapper {
    private JPanel contentPane;
    private JPanel choosePanel;
    private final Project project;

    private final UClass outerClass;

    private Map<String,UClass> uClassMap = new HashMap<>();
    private ButtonGroup buttonGroup = new ButtonGroup();

    public ChooseClassDialog(Project project,UClass outerClass,UClass[] innerClasses) {
        super(true);
        this.project = project;
        this.outerClass = outerClass;
        JRadioButton radioButton = new JRadioButton(outerClass.getName());
        radioButton.setSelected(true);
        buttonGroup.add(radioButton);
        choosePanel.add(radioButton);
        uClassMap.put(outerClass.getName(),outerClass);
        for(int i = 0;i<innerClasses.length;i++){
            UClass innerClass = innerClasses[i];
            JRadioButton innerClassRadioButton = new JRadioButton(innerClass.getQualifiedName());
            buttonGroup.add(innerClassRadioButton);
            choosePanel.add(innerClassRadioButton);
            uClassMap.put(innerClass.getQualifiedName(),innerClass);
        }
        init();
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        try {
            String selectedClassName = findSelectedClassName();
            GenAssignStatementConfigDialog configDialog =  new GenAssignStatementConfigDialog(project,
                selectedClassName.contains(".") ? outerClass : null, uClassMap.get(selectedClassName));
            configDialog.show();
        } finally {
            super.doOKAction();
        }
    }

    private String findSelectedClassName(){
        Iterator<AbstractButton> iterator =  buttonGroup.getElements().asIterator();
        while (iterator.hasNext()){
            AbstractButton button = iterator.next();
            if(button.isSelected()){
                return button.getText();
            }
        }
        return outerClass.getName();
    }
}
