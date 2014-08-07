package ua.samosfator;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class Parser {
    private String baseHost = "http://vstup.info/";
    private String baseURL = null;
    private Direction direction = null;
    private int year;
    private HashMap<String, TreeMap<String, String>> all = new HashMap<>();

    public static List<String> names = new ArrayList<>();
    public static List<String> city = new ArrayList<>();
    public static List<Integer> places = new ArrayList<>();
    public static List<Double> passingScore = new ArrayList<>();
    public static List<String> links = new ArrayList<>();
    public static List<String> courseLinks = new ArrayList<>();


    Parser(int year, String dir) throws IOException {
        this.baseURL = (baseHost + year);
        this.year = year;
        getLinks(year, dir);

        Map.Entry<String, String> entry = all.get(dir).firstEntry();
        this.direction = new Direction(dir, entry.getKey(), entry.getValue());
    }

    Parser(String dir) throws IOException {
        new Parser(2013, dir);
    }

    class Direction {
        String number = null;
        String link = null;
        String name = null;

        Direction(String number, String link, String name) {
            this.number = number;
            this.link = link;
            this.name = name;
        }
    }

    public void parse() throws IOException {
        parseNames();
        parseCity();
        parsePlaces();
        parsePassingScore();

        DataBase db = new DataBase();
        db.add(year, direction.number);
    }

    private void parsePassingScore() throws IOException {
        for (int i = 0; i < courseLinks.size(); i++) {
            String link = courseLinks.get(i);
            if (link == null) {
                passingScore.add(-1.0d);
            } else {
                Document doc = Jsoup.connect(this.baseURL + link).get();
                Integer passingPlace = places.get(i);
                Elements el = passingPlace > 0 ? doc.select("#c > .row table tr").eq(passingPlace).select("td").eq(2) : null;
                try {
                    passingScore.add(Double.parseDouble(el.text()));
                } catch (Exception e) {
                    passingScore.add(-1.0d);
                }
            }
        }
    }

    private void parsePlaces() throws IOException {
        for (String link : links) {
            Document doc = Jsoup.connect(this.baseURL + link.replace(".html", "b.html")).get();
            Elements el = doc.select("#c > .row table tr")
                    .select("tr:contains(" + direction.name + ")")
                    .select("tr:contains(Бакалавр)").select("td");
            try {
                places.add(Integer.valueOf(el.eq(4).text()));
            } catch (NumberFormatException nfe) {
                places.add(Integer.valueOf(0));
            }
            if (el.eq(2).select("a").text().equals("очікуйте")) {
                courseLinks.add(null);
            } else {
                courseLinks.add(el.eq(2).select("a").attr("href").replace("./", "/"));
            }
        }
    }

    private void parseCity() throws IOException {
        for (String link : links) {
            Document doc = Jsoup.connect(this.baseURL + link).get();
            Elements el = doc.select("#c > .row > table tr").eq(5).select("td").eq(1);
            String rawCity = el.text();
            if (rawCity.substring(0, 6).equals("М.КИЇВ")) {
                city.add("Київ");
            } else {
                Pattern p = Pattern.compile(",(.*?),(.*?),");
                Matcher m = p.matcher(rawCity);
                if (m.find()) {
                    city.add(m.group(1).trim().substring(0, 1) + m
                            .group(1).trim().substring(1).toLowerCase());
                } else {
                    city.add("Unknown city");
                }
            }
        }
    }

    private void parseNames() throws IOException {
        Document doc = Jsoup.connect(baseHost + direction.link).get();

        Elements el = doc.select("#c > .row span#branch2:contains(" + this.direction.number + ")").first().nextElementSibling().select("li a");
        for (Element e : el) {
            names.add(e.text().replace("\"", ""));
            links.add(e.attr("href").substring(1));
        }
    }

    public void getLinks(int year, String dir) throws IOException {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://vstup.info/" + year + "/i" + year + "okr1.html").get();
        } catch (HttpStatusException httpEx) {
            if (httpEx.getStatusCode() == 404) {
                throw new IllegalArgumentException("No information for this year. Try previous one", httpEx);
                //TODO: handle in more stable way
            }
        }
        Elements elements = doc.select("span#branch1 a");
        for (Element el : elements) {
            doc = Jsoup.connect("http://vstup.info/" + el.attr("href")).get();
            Elements nums = doc.select("span#branch2");
            for (Element e : nums) {
                TreeMap<String, String> linkName = new TreeMap<>();
                linkName.put(el.attr("href"), e.text().substring(18).replace("\"", ""));

                this.all.put(e.text().substring(8, 16), linkName);
            }
        }
    }
}