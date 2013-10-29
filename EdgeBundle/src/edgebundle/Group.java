/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edgebundle;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Michael Lee
 */
public class Group {

    Map<String, Page> pages = new HashMap<String, Page>();
    final String name;
    Float ExtraLocationX;
    Float ExtraLocationY;
    Float IntraLocationX;
    Float IntraLocationY;
    Float ColorR;
    Float ColorG;
    Float ColorB;
    //Bar Location
    Float barX1;
    Float barX2;
    Float barY1;
    Float barY2;
    //Circle Location
    Float degreeStart;
    Float degreeEnd;
    
    List<Entry<Page, Integer>> pagesByPageViews;

    public String toString() {
        return "Group{" + name + "}";
    }

    Group(String groupName) {
        this.name = groupName;
    }

    Page findOrCreatePage(String pageName) {
        if (!pages.containsKey(pageName)) {
            Page p = new Page(pageName);
            p.group = this;
            pages.put(pageName, p);
        }
        return pages.get(pageName);
    }

    int getPageCount() {
        return pages.size();
    }

    Collection<? extends Page> getPages() {
        return pages.values();
    }

    void setLocation(float cartesianX, float cartesianY) {
    }

    void setColor(float r, float g, float b) {
        this.ColorR = r;
        this.ColorG = g;
        this.ColorB = b;
    }

    void setExtraLocation(float cartesianX, float cartesianY) {
        this.ExtraLocationX = cartesianX;
        this.ExtraLocationY = cartesianY;
    }

    void setIntraLocation(float cartesianX, float cartesianY) {
        this.IntraLocationX = cartesianX;
        this.IntraLocationY = cartesianY;
    }

    Integer getPageViewCount() {
        int pageviews = 0;

        for (Page p : pages.values()) {
            if (p.pageviews != null) {
                pageviews += p.pageviews;
            }
        }
        return pageviews;
    }

    void setBarLocation(float x, float y, float width, float height) {
        this.barX1 = x;
        this.barY1 = y;
        this.barX2 = x + width;
        this.barY2 = y + height;
    }

    float[] getBarLocation() {
        return new float[]{this.barX1, this.barY1, this.barX2, this.barY2};
    }

    void setCircleLocation(float degreeStart, float degreeEnd) {
        this.degreeStart = degreeStart;
        this.degreeEnd = degreeEnd;
    }

    float[] getCircleLocation() {
        return new float[]{this.degreeStart, this.degreeEnd};
    }

    Integer getMaxPageViews() {
        int pageviews = 0;
        for (Page p : pages.values()) {
            if (p.pageviews != null && p.pageviews > pageviews) {
                pageviews = p.pageviews;
            }
        }
        return pageviews;
    }

        private void setPagesByPageViews() {
        Map<Page, Integer> totals = new HashMap();
        for (Page p : pages.values()) {
            totals.put(p, p.pageviews);
        }

        pagesByPageViews = new LinkedList(totals.entrySet());

        Collections.sort(pagesByPageViews, new Comparator<Entry<Page, Integer>>() {

            @Override
            public int compare(Entry<Page, Integer> a, Entry<Page, Integer> b) {
                return a.getValue().compareTo(b.getValue())*-1;
            }
        });
    }
    
    public List<Entry<Page, Integer>> getPagesByPageViews(){
        if(pagesByPageViews==null){
            setPagesByPageViews();
        }
        return pagesByPageViews;
    }
}
