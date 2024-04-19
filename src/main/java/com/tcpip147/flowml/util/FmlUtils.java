package com.tcpip147.flowml.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlText;

import java.util.ArrayList;
import java.util.List;

public class FmlUtils {

    public static <T> T nvl(T a, T b) {
        if (a == null) {
            return b;
        }
        return a;
    }

    public static PsiElement findFirstChildByName(PsiElement parent, String name) {
        for (PsiElement element : parent.getChildren()) {
            if (name.equals(element.toString())) {
                return element;
            }
        }
        return null;
    }

    public static List<PsiElement> findChildrenByName(PsiElement parent, String name) {
        List<PsiElement> elementList = new ArrayList<>();
        for (PsiElement element : parent.getChildren()) {
            if (name.equals(element.toString())) {
                elementList.add(element);
            }
        }
        return elementList;
    }

    public static PsiElement findAttribute(PsiElement tag, String name) {
        List<PsiElement> attributeList = findChildrenByName(tag, "PsiElement(XML_ATTRIBUTE)");
        for (PsiElement attribute : attributeList) {
            PsiElement id = findFirstChildByName(attribute, "XmlToken:XML_NAME");
            if (id != null && name.equals(id.getText())) {
                return attribute;
            }
        }
        return null;
    }

    public static PsiElement findParentByName(PsiElement child, String name, boolean includeSelf) {
        PsiElement parent;
        if (includeSelf) {
            parent = child;
        } else {
            parent = child.getParent();
        }
        while (parent != null) {
            if (name.equals(parent.toString())) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    public static PsiElement findPrevByName(PsiElement element, String name) {
        PsiElement prev = element.getPrevSibling();
        while (prev != null) {
            if (name.equals(prev.toString())) {
                return prev;
            }
            prev = prev.getPrevSibling();
        }
        return null;
    }

    public static PsiElement findNextByName(PsiElement element, String name) {
        PsiElement next = element.getNextSibling();
        while (next != null) {
            if (name.equals(next.toString())) {
                return next;
            }
            next = next.getNextSibling();
        }
        return null;
    }

    public static PsiElement findTextElement(PsiElement element) {
        PsiElement text = findFirstChildByName(element, "XmlText");
        if (text != null) {
            PsiElement cdata = findFirstChildByName(text, "PsiElement(XML_CDATA)");
            if (cdata != null) {
                text = cdata;
            }
            return findFirstChildByName(text, "XmlToken:XML_DATA_CHARACTERS");
        }
        return null;
    }

    public static String getText(PsiElement query) {
        for (PsiElement child : query.getChildren()) {
            if ("XmlText".equals(child.toString())) {
                return ((XmlText) child).getValue();
            }
        }
        return "";
    }

    public static String getAttributeValue(PsiElement tag, String name) {
        List<PsiElement> attributeList = findChildrenByName(tag, "PsiElement(XML_ATTRIBUTE)");
        for (PsiElement attribute : attributeList) {
            PsiElement id = findFirstChildByName(attribute, "XmlToken:XML_NAME");
            if (id != null && name.equals(id.getText())) {
                PsiElement value = findFirstChildByName(attribute, "PsiElement(XML_ATTRIBUTE_VALUE)");
                if (value != null) {
                    PsiElement token = findFirstChildByName(value, "XmlToken:XML_ATTRIBUTE_VALUE_TOKEN");
                    if (token != null) {
                        return token.getText();
                    }
                }
            }
        }
        return null;
    }
}
