package com.github.dwkfxj.statementgenerator.enums;

import cn.hutool.core.util.StrUtil;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.uast.UField;

/**
 * @author Victor
 */
public enum SupportAnnotation {

    NOT_NULL("NotNull",true,1){
        @Override
        public void annotate(PsiElementFactory factory, UField uField) {
            PsiModifierList psiModifierList = ((PsiField)uField.getJavaPsi()).getModifierList();
            if(psiModifierList != null){
                psiModifierList.addAnnotation(getText());
            }
        }
    },
    NOT_BLANK("NotBlank",true,0){
        @Override
        public void annotate(PsiElementFactory factory, UField uField) {
            // just add to field which type is String
            String type = uField.getType().getCanonicalText();
            if(!"java.lang.String".equals(type)){
                return;
            }
            PsiModifierList psiModifierList = ((PsiField)uField.getJavaPsi()).getModifierList();
            if(psiModifierList != null){
                psiModifierList.addAnnotation(getText());
            }
        }
    },
    PROPERTY("Property",true,-1){
        @Override
        public void annotate(PsiElementFactory factory, UField uField) {
            PsiModifierList psiModifierList = ((PsiField)uField.getJavaPsi()).getModifierList();
            if(psiModifierList != null){
                String name = StrUtil.toUnderlineCase(uField.getName());
                psiModifierList.addAnnotation(getText()+ String.format("(name = \"%s\")", name));
            }
        }
    },
    COLUMN("Column",false,-1){
        @Override
        public void annotate(PsiElementFactory factory, UField uField) {
            PsiModifierList psiModifierList = ((PsiField)uField.getJavaPsi()).getModifierList();
            if(psiModifierList != null){
                String name = StrUtil.toUnderlineCase(uField.getName());
                psiModifierList.addAnnotation(getText()+ String.format("(name = \"%s\")", name));
            }
        }
    },
    FIELD("Field",false,-1){
        @Override
        public void annotate(PsiElementFactory factory, UField uField) {
            PsiModifierList psiModifierList = ((PsiField)uField.getJavaPsi()).getModifierList();
            if(psiModifierList != null){
                String name = StrUtil.toUnderlineCase(uField.getName());
                psiModifierList.addAnnotation(getText()+ String.format("(name = \"%s\")", name));
            }
        }
    };

    private final String text;

    private final boolean autoSelected;

    private final Integer index;

    SupportAnnotation(String text,boolean autoSelected,Integer index){
        this.text = text;
        this.autoSelected = autoSelected;
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public boolean isAutoSelected() {
        return autoSelected;
    }

    public Integer getIndex() {
        return index;
    }

    public static SupportAnnotation getByText(@NotNull String text){
        for(SupportAnnotation supportAnnotation : SupportAnnotation.values()){
            if(supportAnnotation.text.equals(text)){
                return supportAnnotation;
            }
        }
        return null;
    }

    public abstract void annotate(PsiElementFactory factory,UField uField);
}
