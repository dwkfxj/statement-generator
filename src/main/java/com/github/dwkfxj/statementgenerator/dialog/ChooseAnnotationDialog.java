package com.github.dwkfxj.statementgenerator.dialog;

import cn.hutool.core.util.StrUtil;
import com.github.dwkfxj.statementgenerator.enums.SupportAnnotation;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UField;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ChooseAnnotationDialog extends DialogWrapper {

    private JPanel contentPane;
    private JPanel selectedAnnotationsPanel;
    private JCheckBox addIdCheckBox;
    private JCheckBox addPrimaryKeyCheckBox;
    private JTextField idValue;
    private JTextField primaryKeyValue;
    private JCheckBox includeInnerClassCheckBox;
    private final Project project;
    private final UClass uClass;
    private final PsiElementFactory factory;
    private boolean addId = false;
    private boolean addPrimaryKey =false;

    public ChooseAnnotationDialog(Project project,UClass uClass){
        super(true);
        this.project = project;
        this.uClass = uClass;
        factory = JavaPsiFacade.getInstance(project).getElementFactory();
        for(SupportAnnotation supportAnnotation : SupportAnnotation.values()){
            JCheckBox fieldCheckBox = new JCheckBox(supportAnnotation.getText());
            fieldCheckBox.setName("annotationCheckBox"+supportAnnotation.name());
            fieldCheckBox.setSelected(supportAnnotation.isAutoSelected());
            fieldCheckBox.setVisible(true);
            selectedAnnotationsPanel.add(fieldCheckBox);
        }
        addIdCheckBox.addItemListener(e->{
            JCheckBox jcb = (JCheckBox)e.getItem();
            if(jcb.isSelected()){
                idValue.setEditable(true);
                idValue.setText("id");
            }else if(!jcb.isSelected()){
                idValue.setEditable(false);
                idValue.setText("");
            }
        });
        addPrimaryKeyCheckBox.addItemListener(e->{
            JCheckBox jcb = (JCheckBox)e.getItem();
            if(jcb.isSelected()){
                primaryKeyValue.setEditable(true);
                idValue.setText("id");
            }else if(!jcb.isSelected()){
                primaryKeyValue.setEditable(false);
                primaryKeyValue.setText("");
            }
        });
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }

    @Override
    protected void doOKAction() {
        try {
            addId = addIdCheckBox.isSelected() && !StrUtil.isBlank(idValue.getText());
            addPrimaryKey = addPrimaryKeyCheckBox.isSelected() && !StrUtil.isBlank(primaryKeyValue.getText());
            addAnnotation(uClass);
            UClass[] innerClasses =  uClass.getInnerClasses();
            if(includeInnerClassCheckBox.isSelected() && innerClasses.length > 0){
                for(UClass inner : innerClasses){
                    addAnnotation(inner);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            super.doOKAction();
        }
    }

    private void addAnnotation(UClass uClass){
        List<SupportAnnotation> selectedAnnotations = selectedAnnotations();
        if(selectedAnnotations.isEmpty()){
            return;
        }
        UField[] uFields =  uClass.getFields();
        addSelectedAnnotations(uFields,selectedAnnotations);
        if(addId){
            addIdAnnotation(uFields);
        }
        if(addPrimaryKey){
            addPrimaryKeyAnnotation(uFields);
        }
    }

    private void addIdAnnotation(UField[] uFields){
        for(UField uField : uFields){
            String fieldName = uField.getName();
            if(StrUtil.isBlank(fieldName)){
                continue;
            }
            if(fieldName.equals(idValue.getText())){
                WriteCommandAction.runWriteCommandAction(project,()->{
                    PsiModifierList psiModifierList = ((PsiField)uField.getJavaPsi()).getModifierList();
                    if(psiModifierList != null){
                        psiModifierList.addAnnotation("Id");
                    }
                });
            }
        }
    }

    private void addPrimaryKeyAnnotation(UField[] uFields){
        for(UField uField : uFields){
            String fieldName = uField.getName();
            if(StrUtil.isBlank(fieldName)){
                continue;
            }
            if(fieldName.equals(primaryKeyValue.getText())){
                WriteCommandAction.runWriteCommandAction(project,()->{
                    PsiModifierList psiModifierList = ((PsiField)uField.getJavaPsi()).getModifierList();
                    if(psiModifierList != null){
                        psiModifierList.addAnnotation("PrimaryKey");
                    }
                });
            }
        }
    }

    private void addSelectedAnnotations(UField[] uFields, List<SupportAnnotation> selectedAnnotations){
        selectedAnnotations.sort(Comparator.comparing(SupportAnnotation::getIndex));
        for(UField uField : uFields){
            for(SupportAnnotation supportAnnotation : selectedAnnotations){
                WriteCommandAction.runWriteCommandAction(project,()->{
                    supportAnnotation.annotate(factory,uField);
                });
            }
        }
    }

    private List<SupportAnnotation> selectedAnnotations(){
        Component[] components =  selectedAnnotationsPanel.getComponents();
        List<SupportAnnotation> selected = new ArrayList<>();
        if(components.length > 0){
            for(Component component : components){
                JCheckBox checkBox = (JCheckBox) component;
                if(checkBox.isSelected()){
                    SupportAnnotation supportAnnotation = SupportAnnotation.getByText(checkBox.getText());
                    if(supportAnnotation != null){
                        selected.add(supportAnnotation);
                    }
                }
            }
        }
        return selected;
    }
}
