/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edgebundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Michael Lee
 */
class Page {
    float barX1;
    float barY1;
    float barX2;
    float barY2;

    public String toString() {
        return "Page{" + name + "," + group + "}";
    }
    String name;
    Integer pageviews = 0;
    //Double degree;
    Group group;
    Float LocationX;
    Float LocationY;
    float degreeStart;
    float degreeEnd;
    Map<Page, Integer> to = new HashMap<Page, Integer>();
//    Map<Page, Integer> from = new HashMap<Page, Integer>();

    Page(String pageName) {
        this.name = pageName;
    }

    void addPageViews(int pageviews) {
        this.pageviews += pageviews;
    }

    void addToPage(Page to, Integer parseInt) {
        if (!this.to.containsKey(to)) {
            this.to.put(to, 0);
        }

        this.to.put(to, this.to.get(to) + parseInt);
    }

    void setLocation(float cartesianX, float cartesianY) {
        this.LocationX = cartesianX;
        this.LocationY = cartesianY;
    }

    Map<Page, Integer> getToPages() {
        return Collections.unmodifiableMap(this.to);
    }

    void setCircleLocation(float degreeStart, float degreeEnd) {
        this.degreeStart = degreeStart;
        this.degreeEnd = degreeEnd;
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
//    Integer getMaxPageViews() {
//        int pageviews = 0;
//        for (Integer p : to.values()) {
//            if (p != null && p > pageviews) {
//                pageviews = p;
//            }
//        }
//        return pageviews;
//    }
}
