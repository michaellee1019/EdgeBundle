package edgebundle;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.joda.time.DateTime;
import org.joda.time.Days;
import processing.core.PApplet;

/**
 *
 * @author Michael Lee
 */
public class EdgeBundle extends PApplet {

    public static void main(String[] args) {
        new Thread() {

            @Override
            public void run() {
                PApplet.main(new String[]{edgebundle.EdgeBundle.class.getName()});
            }
        }.start();

        try {
            mngr = new PageMngr(new EdgeBundle());
            GoogleAnalytics ga = new GoogleAnalytics(mngr);

            Calendar c = Calendar.getInstance();
            c.setTime(startDate);

            //System.out.println("Auth");
            isReady.set(1);
            //String token = ga.Authenticate();

            isReady.set(2);

            //ga.getData("test");

            for (; c.getTime().compareTo(endDate) <= 0; c.add(Calendar.DATE, 1)) {
                ga.getData(c.getTime());
                pullProgress.set((float) Days.daysBetween(new DateTime(startDate), new DateTime(c.getTime())).getDays()
                        / Days.daysBetween(new DateTime(startDate), new DateTime(endDate)).getDays());
            }

            isReady.set(4);

            ga.mngr.getPageCount();
        } catch (Exception e) {
            setErrorString(e);
        }
    }
    
    static Date startDate = new Date("04/28/2012");
    static Date endDate = new Date("05/4/2012");
    //Circle Variables
    float radius = 0;
    float padding = 100;
    float centerX;
    float centerY;
    float groupEndRadius;
    float pageStartRadius;
    float pageEndRadius;
    float extraRadius;
    float intraRadius;
    //Treading Variables
    static AtomicInteger isReady = new AtomicInteger(0);
    static AtomicReference<Float> pullProgress = new AtomicReference(0f);
    static AtomicReference<String> error = new AtomicReference();
    //Pages and Groups
    static PageMngr mngr;
    //Interaction
    Group selectedGroup;
    Page selectedPage;
    Integer drawDirection = 0;
    Integer colorDirection = 1;
    Page hoverPage;
    Group hoverGroup;
    //Formatting
    DecimalFormat format = new DecimalFormat("#,###");
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
    //Animation
    int[] counter = {0, 10, 20, 30, 40};

    @Override
    public void setup() {
        smooth();
        noFill();
        size(1300, 800);
        stroke(255);
        strokeWeight(20);


        radius = (min(width, height) - 200) / 2;
        centerX = width * 2 / 3;
        centerY = height / 2;
        groupEndRadius = radius + 20 + 40;
        pageStartRadius = radius;
        pageEndRadius = radius + 20;
        extraRadius = radius * .5f;
        intraRadius = radius * .9f;
    }
    int degreeprogress = 0;

    @Override
    public void draw() {

        if (isReady.get() == 0 || isReady.get() == 1 || isReady.get() == 2 || isReady.get() == 3) {
            background(127);

            textSize(60);
            textAlign(CENTER);
            text("Loading", width / 2, height / 2);

            for (int i = 0; i < 360; i += 72) {
                arc(width / 2, height / 2, 500, 500, (float) Math.toRadians(i + degreeprogress), (float) Math.toRadians(i + pullProgress.get() * 72 + degreeprogress));
            }
            degreeprogress++;
        }

        if (isReady.get() == -1) {
            background(96, 0, 0);
            textSize(60);
            textAlign(CENTER);
            fill(255, 50, 50);
            text("Error", width / 2, 200);
            textSize(15);
            textAlign(LEFT);
            text(error.get(), 50, 260);
        }

        textSize(15);
        textAlign(LEFT);

        if (isReady.get() == 0) {
            text("Initializing...", width / 2, height / 2 + 60);
        }


        if (isReady.get() == 1) {
            text("Authenticating with Google...", width / 2, height / 2 + 60);
        }


        if (isReady.get() == 2) {
            text("Fetching Google Data (" + Math.round(pullProgress.get() * 100) + "%)...", width / 2, height / 2 + 60);
        }

        if (isReady.get() == 3) {
            text("Parsing Data...", width / 2, height / 2 + 60);
        } else if (isReady.get() == 4) {
            try {
                background(0);
                drawOptions();
                frameRate(10);
                strokeWeight(1);

                fill(255);
                textSize(40);
                text("Visitor Behavior of Stevens.edu", 20, 40);
                textSize(20);
                text("From " + dateFormat.format(startDate) + " To " + dateFormat.format(endDate), 20, 65);

                //stroke(0);
                //ellipse(centerX, centerY, diameter / 2, diameter / 2);
                //ellipse(centerX, centerY, diameter * 3 / 4, diameter * 3 / 4);
                //ellipse(centerX, centerY, diameter * .25f, diameter * .25f);
                //drawPointsOnCircle(mngr.getPageCount());
                //stroke(255);
                //curve(50,50,centerX,centerY,centerX+5,centerY+5,width-1,height-1);
                drawPagesAndGroups();
                //ellipse(cartesianX(width / 2, radius, 60), cartesianY(height / 2, radius, 60), 3, 3);
                drawConnections();
                drawBars();

                noFill();
                stroke(0);
                ellipse(centerX, centerY, radius * 2, radius * 2);

                drawHover();

                for (int i = 0; i < counter.length; i++) {
                    counter[i]++;
                    if (counter[i] >= 50) {
                        counter[i] = 0;
                    }
                }
            } catch (Exception e) {
                setErrorString(e);

            }
        }

    }

    void drawPointsOnCircle(int pointCount) {
        for (int i = 0; i < pointCount; i++) {
            float x = degreeToCartesianX(centerX, radius, map(i, 0, pointCount, 0, 360));
            float y = degreeToCartesianY(centerY, radius, map(i, 0, pointCount, 0, 360));
            ellipse(x, y, 6, 6);
        }
    }

    void drawPagesAndGroups() {
        int groupStart = 0;
        int groupEnd = 0;
        int curPage = 0;
        int curGroup = 0;
        int totalGroups = mngr.getGroupCount();
        int totalPages = mngr.getPageCount();

        for (Group g : mngr.groups.values()) {


            groupEnd = groupStart + g.getPageCount();

            g.setCircleLocation(map(groupStart - .5f, 0, totalPages, 0, 360), map(groupEnd - .5f, 0, totalPages, 0, 360));
            //stroke();
            g.setColor(map(curGroup, 0, totalGroups, 0, 255), 200f, 255f);
            g.setExtraLocation(degreeToCartesianX(centerX, extraRadius, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360)), degreeToCartesianY(centerY, extraRadius, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360)));
            g.setIntraLocation(degreeToCartesianX(centerX, intraRadius, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360)), degreeToCartesianY(centerY, intraRadius, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360)));
            colorMode(HSB);
            noStroke();
            fill(g.ColorR, g.ColorG, g.ColorB, 255);
            //arc(centerX, centerY, groupEndRadius * 2, groupEndRadius * 2, (float) Math.toRadians(map(groupStart - .5f, 0, totalPages, 0, 360)), (float) Math.toRadians(map(groupEnd - .5f, 0, totalPages, 0, 360)));
            arc(centerX, centerY, groupEndRadius * 2, groupEndRadius * 2, (float) Math.toRadians(g.degreeStart), (float) Math.toRadians(g.degreeEnd));
            colorMode(RGB);
            stroke(0);

            line(degreeToCartesianX(centerX, pageEndRadius, g.degreeStart), degreeToCartesianY(centerY, pageEndRadius, g.degreeStart),
                    degreeToCartesianX(centerX, groupEndRadius, g.degreeStart), degreeToCartesianY(centerY, groupEndRadius, g.degreeStart));

//            float textX = cartesianX(centerX, groupEndRadius - (groupSize + .5f) / 2, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360));
//            float textY = cartesianY(centerY, groupEndRadius - (groupSize + .5f) / 2, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360));

            float textX = degreeToCartesianX(centerX, (pageEndRadius + groupEndRadius) / 2, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360));
            float textY = degreeToCartesianY(centerY, (pageEndRadius + groupEndRadius) / 2, map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360));

            textAlign(CENTER);
            textSize(10);
            fill(0);
            float rotRadians = (float) Math.toRadians(map((groupStart + groupEnd - 1) / 2f, 0, totalPages, 0, 360) + 90);
            translate(textX, textY);
            rotate(rotRadians);
            text(g.name, 0, 0);

            rotate(-rotRadians);
            translate(-textX, -textY);

            for (Page p : g.getPages()) {
                p.setCircleLocation(map(curPage - .5f, 0, totalPages, 0, 360), map(curPage + .5f, 0, totalPages, 0, 360));
                p.setLocation(degreeToCartesianX(centerX, pageStartRadius, map(curPage, 0, totalPages, 0, 360)), degreeToCartesianY(centerY, pageStartRadius, map(curPage, 0, totalPages, 0, 360)));
                line(degreeToCartesianX(centerX, pageEndRadius, map(curPage - .5f, 0, totalPages, 0, 360)), degreeToCartesianY(centerY, pageEndRadius, map(curPage - .5f, 0, totalPages, 0, 360)),
                        degreeToCartesianX(centerX, pageStartRadius, map(curPage - .5f, 0, totalPages, 0, 360)), degreeToCartesianY(centerY, pageStartRadius, map(curPage - .5f, 0, totalPages, 0, 360)));

                curPage++;
            }
            groupStart = groupEnd;
            curGroup++;

        }

        fill(0);
        ellipse(centerX, centerY, pageStartRadius * 2, pageStartRadius * 2);

        noFill();
        ellipse(centerX, centerY, pageStartRadius * 2, pageStartRadius * 2);
        ellipse(centerX, centerY, pageEndRadius * 2, pageEndRadius * 2);
        ellipse(centerX, centerY, groupEndRadius * 2, groupEndRadius * 2);
    }

    void drawConnections() {
        for (Page p : mngr.getAllPages()) {
            for (Entry<Page, Integer> to : p.getToPages().entrySet()) {
                //if (drawDirection) {
                connect(p, to.getKey());
                //} else {
                //    connect(to.getKey(), p);
                //}
            }
        }
    }

    void connect(Page from, Page to) {

        colorMode(HSB);

        int max;

        if (selectedGroup == null && selectedPage == null) {
            max = mngr.getMaxPageViews();
        } else if (selectedGroup != null) {
            max = from.group.getMaxPageViews();
        } else {
            max = from.pageviews;
        }

        float opacity = map(from.pageviews, 0, max, 0, 255);

        if (colorDirection == 0) {
            stroke(from.group.ColorR, from.group.ColorG, from.group.ColorB, opacity);
            fill(from.group.ColorR, from.group.ColorG, from.group.ColorB, opacity);
        } else {
            stroke(to.group.ColorR, to.group.ColorG, to.group.ColorB, opacity);
            fill(to.group.ColorR, to.group.ColorG, to.group.ColorB, opacity);
        }
        if ((selectedGroup == null
                || (drawDirection == 0 && (from.group.equals(selectedGroup) || to.group.equals(selectedGroup)))
                || (drawDirection == 2 && from.group.equals(selectedGroup))
                || (drawDirection == 1 && to.group.equals(selectedGroup)))
                && (selectedPage == null
                || (drawDirection == 0 && (from.equals(selectedPage) || to.equals(selectedPage)))
                || (drawDirection == 2 && from.equals(selectedPage))
                || (drawDirection == 1 && to.equals(selectedPage)))) {

            line(from.LocationX, from.LocationY, to.LocationX, to.LocationY);

            if (selectedGroup != null || selectedPage != null) {
                for (int i = 0; i < counter.length; i++) {
                    ellipse(map(counter[i], 0, 50, from.LocationX, to.LocationX), map(counter[i], 0, 50, from.LocationY, to.LocationY), 3, 3);
                }
            }

        }

//        if (from.group.equals(to.group)) {
//            //println("True");
//            drawCurve(from.LocationX, from.LocationY,
//                    to.group.IntraLocationX, to.group.IntraLocationY,
//                    to.LocationX, to.LocationY, 255);
//
//
//        } else {
//            drawCurve(from.LocationX, from.LocationY,
//                    from.group.ExtraLocationX, from.group.ExtraLocationY,
//                    to.group.ExtraLocationX, to.group.ExtraLocationY,
//                    to.LocationX, to.LocationY, 255);
//
//        }

        colorMode(RGB);
    }

    void drawCurve(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, int a) {

        beginShape();
        curveVertex(x1, y1);
        curveVertex(x1, y1);
        curveVertex(x2, y2);
        curveVertex(x3, y3);
        curveVertex(x4, y4);
        endShape();

        beginShape();
        curveVertex(x1, y1);
        curveVertex(x2, y2);
        curveVertex(x3, y3);
        curveVertex(x4, y4);
        curveVertex(x4, y4);
        endShape();
    }

    void drawCurve(float x1, float y1, float x2, float y2, float x3, float y3, int a) {

        beginShape();
        curveVertex(x1, y1);
        curveVertex(x1, y1);
        curveVertex(x2, y2);
        curveVertex(x3, y3);
        endShape();

        beginShape();
        curveVertex(x1, y1);
        curveVertex(x2, y2);
        curveVertex(x3, y3);
        curveVertex(x3, y3);
        endShape();
    }

    float degreeToCartesianX(float centerX, float radius, float degree) {
        return (float) (centerX + radius * Math.cos(Math.toRadians(degree)));
    }

    float degreeToCartesianY(float centerY, float radius, float degree) {
        return (float) (centerY + radius * Math.sin(Math.toRadians(degree)));
    }

    float pointToDegree(float centerX, float centerY, float x, float y) {
        float degree = (float) Math.toDegrees(Math.atan(Math.abs(y - centerY) / Math.abs(x - centerX)));

        if (x - centerX < 0 && y - centerY < 0) {
            return degree + 180;
        } else if (x - centerX < 0) {
            return 90 - degree + 90;
        } else if (y - centerY < 0) {
            return 90 - degree + 270;
        } else {
            return degree;
        }
    }

    

    static void setErrorString(Exception e) {
        //setErrorString(e);
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));

        String stackTrace = stringWriter.toString();
        //System.out.println();
        error.set(stackTrace);
        System.out.println(stackTrace);
        isReady.set(-1);
    }

    @Override
    public void mousePressed() {
        //trans = !trans;
        if (isReady.get() == 4) {

            //System.out.println("pressed: " + mouseX + ", " + mouseY);
            if (selectedGroup == null) {
                for (Group g : mngr.groups.values()) {
                    //System.out.println(g.barX1 + ", " + g.barX2 + ", " + g.barY1 + ", " + g.barY2);
                    if (mouseX >= g.barX1 && mouseX <= g.barX2 && mouseY >= g.barY1 && mouseY <= g.barY2) {
                        ChangeSelections(g, null);
                        //System.out.println(g.toString());
                        return;
                    }

                }
            } else {
                for (Page p : selectedGroup.getPages()) {
                    if (mouseX >= p.barX1 && mouseX <= p.barX2 && mouseY >= p.barY1 && mouseY <= p.barY2) {
                        System.out.println(p.toString());
                        ChangeSelections(selectedGroup, p);
                        return;
                    }
                }
            }

            ChangeSelections(hoverGroup, hoverPage);
//            float distance = dist(mouseX, mouseY, centerX, centerY);
//
//            if (distance >= pageStartRadius && distance <= pageEndRadius) {
//                float degree = pointToDegree(centerX, centerY, mouseX, mouseY);
//                for (Page p : mngr.getAllPages()) {
//                    //System.out.println(g.degreeStart + ", " + g.degreeEnd);
//                    if (degree >= p.degreeStart && degree <= p.degreeEnd) {
//                        selectedPage = p;
//                        selectedGroup = null;
//                        //System.out.println(g.toString());
//                        return;
//                    }
//                }
//            }
//
//            if (distance >= pageEndRadius && distance <= groupEndRadius) {
//                float degree = pointToDegree(centerX, centerY, mouseX, mouseY);
//                for (Group g : mngr.groups.values()) {
//                    //System.out.println(g.degreeStart + ", " + g.degreeEnd);
//                    if (degree >= g.degreeStart && degree <= g.degreeEnd) {
//                        selectedGroup = g;
//                        selectedPage= null;
//                        //System.out.println(g.toString());
//                        return;
//                    }
//                }
//            }
//            selectedPage = null;
//            selectedGroup = null;



        }
    }

    @Override
    public void keyPressed() {

        if (isReady.get() == 4) {
            if (key == 'd' && selectedGroup != null || selectedPage != null) {
                drawDirection++;

                if (drawDirection == 3) {
                    drawDirection = 0;
                    colorDirection = 0;
                } else if (drawDirection == 1) {
                    colorDirection = 0;
                } else if (drawDirection == 2) {
                    colorDirection = 1;
                }
            }
        }
    }

    private void drawBars() {
        stroke(0);
        fill(255);
        colorMode(HSB);
        textAlign(LEFT, CENTER);
        textSize(16);

        if (selectedGroup == null) {
            int i = 0;
            int max = mngr.getGroupsByPageViews().get(0).getValue();
            int length = mngr.getGroupsByPageViews().size();
            for (Entry<Group, Integer> e : mngr.getGroupsByPageViews()) {

                e.getKey().setBarLocation(20, 90 + i * ((height - 110) / length), map(e.getValue(), 0, max, 2, width - height - 150), (height / length) - 7);
                fill(e.getKey().ColorR, e.getKey().ColorG, e.getKey().ColorB);
                float[] location = e.getKey().getBarLocation();
                rect(location[0], location[1], location[2] - location[0], location[3] - location[1]);
                text(e.getKey().name + ": " + format.format(e.getValue()), location[2] + 5, (location[1] + location[3]) / 2);

                i++;
            }
        } else {
            int i = 1;

            int groupMax = mngr.getGroupsByPageViews().get(0).getValue();
            int groupLength = mngr.getGroupsByPageViews().size();
            int max = selectedGroup.getPageViewCount();
            int length = selectedGroup.getPageCount() + 1;

            fill(selectedGroup.ColorR, selectedGroup.ColorG, selectedGroup.ColorB);
            rect(20, 90, width - height - 150, (height / (length + 5)));
            text(selectedGroup.name + ": " + format.format(selectedGroup.getPageViewCount()), width - height - 150 + 25, (height / (length + 5)) / 2 + 90);

            for (Entry<Page, Integer> e : selectedGroup.getPagesByPageViews()) {

                e.getKey().setBarLocation(20, 90 + i * ((height - 110) / length), map(e.getValue(), 0, max, 2, width - height - 150), (height / (length + 5)));
                fill(e.getKey().group.ColorR, e.getKey().group.ColorG, e.getKey().group.ColorB);
                float[] location = e.getKey().getBarLocation();
                rect(location[0], location[1], location[2] - location[0], location[3] - location[1]);
                text(e.getKey().name + ": " + format.format(e.getValue()), location[2] + 5, (location[1] + location[3]) / 2);

                i++;
            }
        }
        colorMode(RGB);
    }

    private void drawOptions() {
        fill(255);
        textSize(16);
        //text("Key: ", 10, 16);
        String showing = "Traffic ";

        if (drawDirection == 0) {
            showing += "To and From ";
        } else if (drawDirection == 1) {
            showing += "To ";
        } else {
            showing += "From ";
        }

        if (selectedGroup == null && selectedPage == null) {
            showing += "All Pages";
        } else if (selectedPage != null) {
            showing += selectedPage.group.name + " -> " + selectedPage.name;
        } else if (selectedGroup != null) {
            showing += selectedGroup.name;
        }

        textAlign(RIGHT, CENTER);
        text(showing, width - 10, 16);
        text("Color Of " + (colorDirection == 0 ? "Origin" : "Destination"), width - 10, 32);
        textAlign(LEFT);
    }

    private void drawHover() {
        float distance = dist(mouseX, mouseY, centerX, centerY);
        float degree = pointToDegree(centerX, centerY, mouseX, mouseY);
        if (distance >= pageStartRadius && distance <= pageEndRadius) {
            for (Page p : mngr.getAllPages()) {
                //System.out.println(g.degreeStart + ", " + g.degreeEnd);
                if (degree >= p.degreeStart && degree <= p.degreeEnd) {
                    hoverPage = p;
                    hoverGroup = null;
                    //System.out.println(g.toString());

                }
            }
        } else if (distance >= pageEndRadius && distance <= groupEndRadius) {
            for (Group g : mngr.groups.values()) {
                //System.out.println(g.degreeStart + ", " + g.degreeEnd);
                if (degree >= g.degreeStart && degree <= g.degreeEnd) {
                    hoverGroup = g;
                    hoverPage = null;
                    //System.out.println(g.toString());

                }
            }
        } else {
            hoverGroup = null;
            hoverPage = null;
        }

        if (hoverGroup != null || hoverPage != null) {

//            System.out.println(degree);

            textSize(16);
            textAlign(LEFT, BASELINE);

            if (hoverGroup != null) {
                float tooltipGroupX = 0;
                float tooltipGroupY = 0;
                float tooltipGroupWidth = max(textWidth("Group: " + hoverGroup.name), textWidth("Views: " + format.format(hoverGroup.getPageViewCount()))) + 8;
                float tooltipGroupHeight = 40;
                if (degree >= 0 && degree <= 90) {
                    tooltipGroupX = mouseX - 20 - tooltipGroupWidth;
                    tooltipGroupY = mouseY - 20 - tooltipGroupHeight;
                } else if (degree > 90 && degree <= 180) {
                    tooltipGroupX = mouseX + 20;
                    tooltipGroupY = mouseY - 20 - tooltipGroupHeight;
                } else if (degree > 180 && degree <= 270) {
                    tooltipGroupX = mouseX + 20;
                    tooltipGroupY = mouseY + 20;
                } else if (degree > 270 && degree <= 360) {
                    tooltipGroupX = mouseX - 20 - tooltipGroupWidth;
                    tooltipGroupY = mouseY + 20;
                }

                fill(50, 220);
                rect(tooltipGroupX, tooltipGroupY, tooltipGroupWidth, tooltipGroupHeight);
                fill(255);
                text("Group: " + hoverGroup.name, tooltipGroupX + 4, tooltipGroupY + 16);
                text("Views: " + format.format(hoverGroup.getPageViewCount()), tooltipGroupX + 4, tooltipGroupY + 36);
            }
            if (hoverPage != null) {

                float tooltipPageX = 0;
                float tooltipPageY = 0;
                float tooltipPageWidth = max(textWidth("Page: " + hoverPage.group.name + " -> " + hoverPage.name), textWidth("Views: " + format.format(hoverPage.pageviews))) + 8;
                float tooltipPageHeight = 40;

                if (degree >= 0 && degree <= 90) {
                    tooltipPageX = mouseX - 20 - tooltipPageWidth;
                    tooltipPageY = mouseY - 20 - tooltipPageHeight;
                } else if (degree > 90 && degree <= 180) {
                    tooltipPageX = mouseX + 20;
                    tooltipPageY = mouseY - 20 - tooltipPageHeight;
                } else if (degree > 180 && degree <= 270) {
                    tooltipPageX = mouseX + 20;
                    tooltipPageY = mouseY + 20;

                } else if (degree > 270 && degree <= 360) {
                    tooltipPageX = mouseX - 20 - tooltipPageWidth;
                    tooltipPageY = mouseY + 20;
                }

                fill(50, 220);
                rect(tooltipPageX, tooltipPageY, tooltipPageWidth, tooltipPageHeight);
                fill(255);
                text("Page: " + hoverPage.group.name + " -> " + hoverPage.name, tooltipPageX + 4, tooltipPageY + 16);
                text("Views: " + format.format(hoverPage.pageviews), tooltipPageX + 4, tooltipPageY + 36);
            }
        }

    }

    void ChangeSelections(Group g, Page p) {

        selectedPage = p;

        if (selectedPage != null) {
            selectedGroup = selectedPage.group;
        } else {
            selectedGroup = g;
        }

        drawDirection = 0;

        if (g == null && p == null) {
            colorDirection = 1;
        } else {
            colorDirection = 0;
        }
    }
}
