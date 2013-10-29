package edgebundle;

import java.util.*;
import java.util.Map.Entry;
import processing.core.PApplet;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
/**
 *
 * @author Michael Lee
 */
public class PageMngr {

    //GoogleAnalytics ga = new GoogleAnalytics(this);
    Map<String, Group> groups =
            new HashMap<String, Group>();
    Map<String, Page> byUrl =
            new HashMap<String, Page>();
    Object[][] rules;
    
    List<Entry<Group, Integer>> groupsByPageViews;

    public static void main(String... args) {
        PageMngr p = new PageMngr(new PApplet());

        p.findOrCreatePageByURL("www.webcampus.stevens.edu/Faqs.aspx");

        System.out.println(p.groups);
        System.out.println(p.byUrl);
        System.out.println(p.getPageCount());
        System.out.println(p.getAllPages());
    }

    public PageMngr(PApplet app) {
        Table table = new Table("Urls.csv", app);
        rules = new Object[table.getRowCount() - 1][5];
        for (int i = 1; i < table.getRowCount(); i++) {
            rules[i - 1][0] = table.getString(i, 0);
            rules[i - 1][1] = table.getString(i, 1);
            rules[i - 1][2] = table.getString(i, 2);
            rules[i - 1][3] = table.getString(i, 3);
            rules[i - 1][4] = 0;

        }
    }

    public Page findOrCreatePageByURL(String url) {
        if (!byUrl.containsKey(url)) {
            String[] nameAndGroup = getPageName(url);
            Page p = findOrCreatePageByNameAndGroup(nameAndGroup[1], nameAndGroup[0]);
            //System.out.println(p.toString());
            byUrl.put(url, p);
        }

        return byUrl.get(url);
    }

    public Page findOrCreatePageByNameAndGroup(String pageName, String groupName) {

        if (pageName != null && groupName != null) {
            if (!groups.containsKey(groupName)) {
                Group g = new Group(groupName);
                groups.put(groupName, g);
            }
            Group g = groups.get(groupName);
            Page p = g.findOrCreatePage(pageName);
            return p;
        }
        return null;
    }

    public List<Page> getAllPages() {
        List<Page> out = new LinkedList<Page>();
        for (Group g : groups.values()) {
            out.addAll(g.getPages());
        }
        return out;
    }

    int getPageCount() {
        int pagecount = 0;
        for (Map.Entry<String, Group> pair : groups.entrySet()) {
            pagecount += pair.getValue().getPageCount();
        }
        return pagecount;
    }

    int getGroupCount() {
        return this.groups.size();
    }

    private String[] getPageName(String pagePath) {

        for (int r = 0; r < rules.length; r++) {
            if (rules[r][0].equals("Equals") && pagePath.toLowerCase().equals(rules[r][1].toString().toLowerCase())) {
                //System.out.println(rules[r][2].toString());
                return new String[]{rules[r][2].toString(), rules[r][3].toString()};

            } else if (rules[r][0].equals("StartsWith") && pagePath.toLowerCase().startsWith(rules[r][1].toString().toLowerCase())) {
                return new String[]{rules[r][2].toString(), rules[r][3].toString()};

            } else if (rules[r][0].equals("Contains") && pagePath.toLowerCase().contains(rules[r][1].toString().toLowerCase())) {
                return new String[]{rules[r][2].toString(), rules[r][3].toString()};
                        
            } else if (rules[r][0].equals("EndsWith") && pagePath.toLowerCase().endsWith(rules[r][1].toString().toLowerCase())) {
                return new String[]{rules[r][2].toString(), rules[r][3].toString()};
            }  
            //} else if (rules[r][0].equals("Wildcard") && pagePath.toLowerCase().endsWith(rules[r][1].toString().toLowerCase())) {
            //    return new String[]{rules[r][2].toString(), rules[r][3].toString()};
            //}
        }

        //System.out.println(pagePath);
        
        return new String[]{null, null};
    }

    private void setGroupsByPageViews() {
        Map<Group, Integer> totals = new HashMap();
        for (Group g : groups.values()) {
            totals.put(g, g.getPageViewCount());
        }

        groupsByPageViews = new LinkedList(totals.entrySet());

        Collections.sort(groupsByPageViews, new Comparator<Entry<Group, Integer>>() {

            @Override
            public int compare(Entry<Group, Integer> a, Entry<Group, Integer> b) {
                return a.getValue().compareTo(b.getValue())*-1;
            }
        });
    }
    
    public List<Entry<Group, Integer>> getGroupsByPageViews(){
        if(groupsByPageViews==null){
            setGroupsByPageViews();
        }
        return groupsByPageViews;
    }

    int getMaxPageViews() {
        int max = 0;
        
        for(Group g : groups.values()){
            if(max<g.getMaxPageViews()){
                max = g.getMaxPageViews();
            }
        }
        return max;
    }
}
