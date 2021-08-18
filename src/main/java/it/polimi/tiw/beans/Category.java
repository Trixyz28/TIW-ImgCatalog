package it.polimi.tiw.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int position;
    private int numChild;

    private Boolean isTop = false;
    private Map<Category, Integer> subClasses = new HashMap<Category, Integer>();


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNumChild() {
        return numChild;
    }

    public void setNumChild(int numChild) {
        this.numChild = numChild;
    }

    public Boolean getTop() {
        return isTop;
    }

    public void setTop(Boolean top) {
        isTop = top;
    }

    public Map<Category, Integer> getSubClasses() {
        return subClasses;
    }

    public void addSubClass(Category category, Integer n) {
        subClasses.put(category, n);
    }

    public void removeSubClass(Category category) {
        subClasses.remove(category);
    }

    public String toString() {
        StringBuffer aBuffer = new StringBuffer("Category");
        aBuffer.append(" id: ");
        aBuffer.append(id);
        aBuffer.append(" name: ");
        aBuffer.append(name);
        aBuffer.append(" subparts: ");
        return aBuffer.toString();
    }



}
