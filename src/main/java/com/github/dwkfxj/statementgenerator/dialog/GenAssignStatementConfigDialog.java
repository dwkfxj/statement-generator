package com.github.dwkfxj.statementgenerator.dialog;

import com.github.dwkfxj.statementgenerator.ClipboardHandler;
import com.github.dwkfxj.statementgenerator.Notifier;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UField;

import javax.swing.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class GenAssignStatementConfigDialog extends DialogWrapper {
    private JPanel contentPane;
    private JLabel targetVar;
    private JTextField targetVarName;
    private JLabel targetClass;
    private JTextField targetClassName;
    private JLabel sourceVar;
    private JTextField sourceVarName;
    private JPanel selectedFieldsPanel;

    private final Project project;

    public GenAssignStatementConfigDialog(Project project,UClass outerClass,UClass uClass) {
        super(true);
        this.project = project;
        setTitle("Generate Assign Statement Configure");
        targetClassName.setText(outerClass == null ? uClass.getName() : String.join(".",outerClass.getName(),uClass.getName()));
        targetClassName.setEditable(false);
        targetVarName.setText(createTargetVariableNameByClassName(uClass.getName()));
        sourceVarName.setText(createSourceVariableNameByClassName(uClass.getName()));
        UField[] fields =  uClass.getFields();
        for(int i =0;i< fields.length;i++){
            JCheckBox fieldCheckBox = new JCheckBox(fields[i].getName());
            fieldCheckBox.setName("fieldCheckBox"+fields[i].getName());
            fieldCheckBox.setSelected(true);
            fieldCheckBox.setVisible(true);
            selectedFieldsPanel.add(fieldCheckBox);
        }
        init();
    }

    @Override
    protected void doOKAction() {
        try {
            Component[] components =  selectedFieldsPanel.getComponents();
            if(components.length > 0){
                List<String> fieldNames = new ArrayList<>();
                for(Component component : components){
                    JCheckBox checkBox = (JCheckBox) component;
                    if(checkBox.isSelected()){
                        fieldNames.add(checkBox.getText());
                    }
                }
                copyAssignStatement(fieldNames);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            super.doOKAction();
        }
    }

    private void copyAssignStatement(List<String> fieldNames){
        ClipboardHandler.copyToClipboard(buildContent(fieldNames));
        Notifier.info("Copy Assign Statements Success, Enjoy it!", project);

    }

    private String buildContent(List<String> fieldNames){
        StringBuilder sb = new StringBuilder();
        final String newInstantStatementTemplate = "{0} {1} = new {2}();\n";
        sb.append(MessageFormat.format(newInstantStatementTemplate,targetClassName.getText(),targetVarName.getText(),targetClassName.getText()));
        final String statementTemplate = "{0}.{1} = {2}.{3};\n";
        for(String fieldName : fieldNames){
            sb.append(MessageFormat.format(statementTemplate,targetVarName.getText(),
                fieldName, sourceVarName.getText(),fieldName));
        }
        return sb.toString();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    private String createTargetVariableNameByClassName(String className){
        String firstChar = className.substring(0,1);
        String lowerCaseFirstChar = firstChar.toLowerCase();
        return lowerCaseFirstChar + className.substring(1);
    }

    private String createSourceVariableNameByClassName(String className){
        return "source" + className;
    }

}
