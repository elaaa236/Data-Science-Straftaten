import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Analyse {


    /*
     * wir haben die jahre 10-20, 20-30 etc.
     * bevöllkerungszahl für 10-20, 20-30 etc.
     * wir haben die schlüssel: 800000 sowie für die jeweiligen jahre (93, 94, 95) -> summe an straftaten für 10-20, 20-30
     * formel: (anzahl Tatverdächtige (alter, jahr) / anzahl bevölkerungsgruppe (alter, jahr))*100.000
     * */
    private final int year;
    private final double tenToTwenty;
    private final double twentyToThirty;
    private final double thirtyToForty;
    private final double fortyToFifty;
    private final double fiftyToSixty;
    private int total = -1;

    public Analyse(int year, int tenToTwenty, int twentyToThirty, int thirtyToForty, int fortyToFifty, int fiftyToSixty, int total) {
        this.year = year;
        this.tenToTwenty = tenToTwenty;
        this.twentyToThirty = twentyToThirty;
        this.thirtyToForty = thirtyToForty;
        this.fortyToFifty = fortyToFifty;
        this.fiftyToSixty = fiftyToSixty;
        this.total = total;
    }

    public Analyse(int year, double tenToTwenty, double twentyToThirty, double thirtyToForty, double fortyToFifty, double fiftyToSixty) {
        this.year = year;
        this.tenToTwenty = tenToTwenty;
        this.twentyToThirty = twentyToThirty;
        this.thirtyToForty = thirtyToForty;
        this.fortyToFifty = fortyToFifty;
        this.fiftyToSixty = fiftyToSixty;
    }


    private static HashMap<String, ArrayList<Analyse>> rechnungen = new HashMap<>();
    private static HashMap<Integer, Analyse> objekte = new HashMap<>();


    /*
     * ließt alles aus test.csv ein
     * test csv = bövlkerungszahl von Deutschland, quelle = Bundeskriminalamt
     * überarbeitet test.csv so dass nur die zeilen relevant sind die auch dazugehören
     * berechnet dann daraus die werte
     * speichert diese ab
     * startet achter
     *
     * */
    private static void readIn() {
        try (var testReader = new BufferedReader(new FileReader("test.csv"))) {
            for (var line = testReader.readLine(); line != null; line = testReader.readLine()) {
                var rows = line.split(";");
                var year = Integer.parseInt(rows[0]);
                if (year < 1993)
                    continue;
                var tenToTwenty = Integer.parseInt(rows[4]) + Integer.parseInt(rows[5]) + Integer.parseInt(rows[9]) + Integer.parseInt(rows[10]);
                var twentyToThirty = Integer.parseInt(rows[14]) + Integer.parseInt(rows[15]);
                var thirtyToForty = Integer.parseInt(rows[16]);
                var fortyToFifty = Integer.parseInt(rows[17]);
                var fiftyToSixty = Integer.parseInt(rows[18]);
                var total = tenToTwenty + twentyToThirty + thirtyToForty + fortyToFifty + fiftyToSixty;
                objekte.put(year, new Analyse(year, tenToTwenty, twentyToThirty, thirtyToForty, fortyToFifty, fiftyToSixty, total));
            }
            //weiter:

            /*
             * formel: (anzahl Tatverdächtige (alter, jahr) / anzahl bevölkerungsgruppe (alter, jahr))*100.000
             * */

            for (var key : Straftat.mappe.keySet()) {
                var straftatGruppe = Straftat.mappe.get(key);
                for (var straftatAlter : straftatGruppe) {
                    var year = straftatAlter.getYear();
                    double a =  (((double) straftatAlter.getTen_to_twenty() / objekte.get(year).tenToTwenty) * 100000);
                    double b =  (((double) straftatAlter.getTwentyone_to_twentynine() / objekte.get(year).twentyToThirty) * 100000);
                    double c =  (((double) straftatAlter.getThirty_to_thirtynine() / objekte.get(year).thirtyToForty) * 100000);
                    double d =  (((double) straftatAlter.getForty_to_fourtynine() / objekte.get(year).fortyToFifty) * 100000);
                    double e =  (((double) straftatAlter.getFifty_to_fiftynine() / objekte.get(year).fiftyToSixty) * 100000);
                    var analyse = new Analyse(year,  a,  b, c, d,e);
                    if (!rechnungen.containsKey(key))
                        rechnungen.put(key, new ArrayList<>());
                    rechnungen.get(key).add(analyse);
                }
            }

            var mappe = new HashMap<String, Straftat>();
            for (var key : rechnungen.keySet()) {
                var total_10_20 = 0;
                var total_20_30 = 0;
                var total_30_40 = 0;
                var total_40_50 = 0;
                var total_50_60 = 0;
                var total_total = 0;
                for (var analyse : rechnungen.get(key)) {
                    total_10_20 += analyse.tenToTwenty;
                    total_20_30 += analyse.twentyToThirty;
                    total_30_40 += analyse.thirtyToForty;
                    total_40_50 += analyse.fortyToFifty;
                    total_50_60 += analyse.fiftyToSixty;
                }
                total_total = (total_10_20 + total_20_30 + total_30_40 + total_40_50 + total_50_60);
                var straftat = new Straftat(key, "", 0, total_10_20, total_20_30, total_30_40, total_40_50, total_50_60, total_total);
                mappe.put(key, straftat);
            }
            var total_total_10_20 = 0;
            var total_total_20_30 = 0;
            var total_total_30_40 = 0;
            var total_total_40_50 = 0;
            var total_total_50_60 = 0;
            var total_total_total = 0;
            for (var key : mappe.keySet()) {
                total_total_10_20 += mappe.get(key).getTen_to_twenty();
                total_total_20_30 += mappe.get(key).getTwentyone_to_twentynine();
                total_total_30_40 += mappe.get(key).getThirty_to_thirtynine();
                total_total_40_50 += mappe.get(key).getForty_to_fourtynine();
                total_total_50_60 += mappe.get(key).getFifty_to_fiftynine();
            }
            total_total_total = total_total_10_20 + total_total_20_30 + total_total_30_40 + total_total_40_50 + total_total_50_60;
            var master = new Straftat("-", "total", 0, total_total_10_20, total_total_20_30, total_total_30_40, total_total_40_50, total_total_50_60, total_total_total);

            var writer = new BufferedWriter(new FileWriter("ende.csv"));
            var builder = new StringBuilder();
            builder.append("Straftat-Key;10-20;20-30;30-40;40-50;50-60;total\n");
            builder.append(master.getId() + ";" + master.getTen_to_twenty() + ";" + master.getTwentyone_to_twentynine() + ";" + master.getThirty_to_thirtynine() + ";" + master.getForty_to_fourtynine() + ";" + master.getFifty_to_fiftynine() + ";" + master.getTotal() + "\n");

            for (var key : mappe.keySet()) {
                builder.append(key + ";" + mappe.get(key).getTen_to_twenty() + ";" + mappe.get(key).getTwentyone_to_twentynine() + ";" + mappe.get(key).getThirty_to_thirtynine() + ";" + mappe.get(key).getForty_to_fourtynine() + ";" + mappe.get(key).getFifty_to_fiftynine() + ";" + mappe.get(key).getTotal() + "\n");
            }
            writer.write(builder.toString());
            writer.flush();
            achter(mappe);
        } catch (NumberFormatException | FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Speichert und lädt nur die werte die am ende auch mit einer 8 als key beginnen
    //berechnet ihre totalwerte und speichert diese ab
    private static void achter(HashMap<String, Straftat> mappe) throws IOException {
        try (var writer = new BufferedWriter(new FileWriter("kategorie.csv"))) {
            var builder = new StringBuilder();
            writer.write("nummer;name;10-20;21-29;30-39;40-49;50-59;total\n");
            var total_10_20 = 0;
            var total_21_29 = 0;
            var total_30_39 = 0;
            var total_40_49 = 0;
            var total_50_59 = 0;
            var total = 0;
            for (var key : mappe.keySet()) {
                if (!key.startsWith("8"))
                    continue;
                total_10_20 += mappe.get(key).getTen_to_twenty();
                total_21_29 += mappe.get(key).getTwentyone_to_twentynine();
                total_30_39 += mappe.get(key).getThirty_to_thirtynine();
                total_40_49 += mappe.get(key).getForty_to_fourtynine();
                total_50_59 += mappe.get(key).getFifty_to_fiftynine();
                total += mappe.get(key).getTotal();


            }
            builder.append("achter").append(";").append("name").append(";").append(total_10_20).append(";").append(total_21_29).append(";").append(total_30_39).append(";").append(total_40_49).append(";").append(total_50_59).append(";").append(total).append("\n");

            writer.write(builder.toString());
            writer.flush();
        }
    }

    public static void main(String[] args) throws IOException {
        Straftat.readFile();
        readIn();

    }
}

class Straftat {

    public static final HashMap<String, ArrayList<Straftat>> mappe = new HashMap<>();
    private final String id;
    private final String name;
    private final int year;
    private final int total;
    private final int ten_to_twenty;
    private final int twentyone_to_twentynine;
    private final int thirty_to_thirtynine;
    private final int forty_to_fourtynine;
    private final int fifty_to_fiftynine;

    public Straftat(String id, String name, int year, int total, int ten_to_twenty, int twentyone_to_twentynine, int thirty_to_thirtynine, int forty_to_fourtynine, int fifty_to_fiftynine) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.total = total;
        this.ten_to_twenty = ten_to_twenty;
        this.twentyone_to_twentynine = twentyone_to_twentynine;
        this.thirty_to_thirtynine = thirty_to_thirtynine;
        this.forty_to_fourtynine = forty_to_fourtynine;
        this.fifty_to_fiftynine = fifty_to_fiftynine;
    }


    //liest alle straftaten ein und erstellt objekte die danach passen
    // data = straftaten, PKA quelle
    public static void readFile() throws IOException {
        var reader = new BufferedReader(new FileReader("data.csv"));
        while (reader.ready()) {
            try {
                var line = reader.readLine().replace(".", "");
                var objects = line.split(";");
                if (line.contains("*") || line.contains("E+"))
                    continue;

                var id = objects[0];

                while (id.length() < 6)
                    id = "0" + id;
                var name = objects[1];
                var year = Integer.parseInt(objects[2]);
                if (year < 1993)
                    continue;
                var ten_to_twenty = Integer.parseInt(objects[7]) + Integer.parseInt(objects[8]) + Integer.parseInt(objects[13]) + Integer.parseInt(objects[15]);
                var twentyone_to_twentynine = Integer.parseInt(objects[21]) + Integer.parseInt(objects[23]);
                var thirty_to_thirtynine = Integer.parseInt(objects[24]);
                var forty_to_fourtynine = Integer.parseInt(objects[25]);
                var fifty_to_fiftynine = Integer.parseInt(objects[26]);
                var total = ten_to_twenty + twentyone_to_twentynine + thirty_to_thirtynine + forty_to_fourtynine + fifty_to_fiftynine;
                if (!mappe.containsKey(id)) {
                    mappe.put(id, new ArrayList<>());
                }
                mappe.get(id).add(new Straftat(id, name, year, total, ten_to_twenty, twentyone_to_twentynine, thirty_to_thirtynine, forty_to_fourtynine, fifty_to_fiftynine));
            } catch (NumberFormatException e) {
            }
        }
        reader.close();


        for (var key : new HashSet<>(mappe.keySet())) {
            var collection = mappe.get(key);
            if (collection.size() != 29)
                mappe.remove(key);
        }

        //print out the map


    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public int getTotal() {
        return total;
    }

    public int getTen_to_twenty() {
        return ten_to_twenty;
    }

    public int getTwentyone_to_twentynine() {
        return twentyone_to_twentynine;
    }

    public int getThirty_to_thirtynine() {
        return thirty_to_thirtynine;
    }

    public int getForty_to_fourtynine() {
        return forty_to_fourtynine;
    }

    public int getFifty_to_fiftynine() {
        return fifty_to_fiftynine;
    }
}
