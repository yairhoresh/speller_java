package similarWords;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author YHoresh
 *
 */
public class BeforeAndAfter {
    private Map<String, Integer> befores;
    private Map<String, Integer> afters;

    BeforeAndAfter() {
        this.befores = new HashMap<>();
        this.afters = new HashMap<>();
    }

    BeforeAndAfter(Map<String, Integer> befores, Map<String, Integer> afters) {
        this.befores = befores;
        this.afters = afters;
    }


    /**
     * @param word - word
     */
    public void addBefores(String word) {

        Integer count = this.befores.get(word);
        if (count == null) {
            count = 0;
        }
        this.befores.put(word, count + 1);
    }


    /**
     * @param word - word
     */
    public void addAfters(String word) {

        Integer count = this.afters.get(word);
        if (count == null) {
            count = 0;
        }
        this.afters.put(word, count + 1);
    }


    public Map<String, Integer> getBefores() {
        return this.befores;
    }


    public Map<String, Integer> getAfters() {
        return this.afters;
    }


    /**
     * @param anotherBeforeAndAfter - beforeAndAfter object
     */
    public void concatenateWithAnother(BeforeAndAfter anotherBeforeAndAfter) {

        // befores
        for (Entry<String, Integer> entry : anotherBeforeAndAfter.getBefores().entrySet()) {

            String key = entry.getKey();
            Integer anotherCount = entry.getValue();
            Integer count = this.befores.get(key);
            if (count == null) {
                count = 0;
            }

            this.befores.put(key, count + anotherCount);
        }

        // afters
        for (Entry<String, Integer> entry : anotherBeforeAndAfter.getAfters().entrySet()) {

            String key = entry.getKey();
            Integer anotherCount = entry.getValue();
            Integer count = this.afters.get(key);
            if (count == null) {
                count = 0;
            }

            this.afters.put(key, count + anotherCount);
        }


    }


    /**
     * @param anotherBeforeAndAfter - beforeAndAfter object
     */
    public void RemoveNotUniques(BeforeAndAfter anotherBeforeAndAfter) {

        for (int i = 0; i < 2; i++)
        {

            Set<String> keysToBeRemoved = new HashSet<String>();
            Map<String, Integer> map;

            if (i == 0) {
                map = this.befores;
            } else {
                map = this.afters;
            }

            for (Entry<String, Integer> entry : map.entrySet()) {

                String key = entry.getKey();

                Integer anotherCount = anotherBeforeAndAfter.getBefores().get(key);

                if (anotherCount != null) {
                    keysToBeRemoved.add(key);
                }
            }

            for (String key : keysToBeRemoved) {
                map.remove(key);
            }

        }


    }


    /**
     * @param minimalCount - minimal count
     */
    public String getBeforesAndAfters(int minimalCount, char beforeOrAfter) {

        StringBuffer sb = new StringBuffer();
        Set<Entry<String, Integer>> setOfEntries;
        if (beforeOrAfter == 'b') {
            setOfEntries = this.befores.entrySet();
        } else {

            setOfEntries = this.afters.entrySet();
        }


        for (Entry<String, Integer> entry : setOfEntries) {
            if (entry.getValue() >= minimalCount) {
                sb.append(entry.getKey());
                sb.append(' ');
            }
        }

        return sb.toString().trim();

    }


    public BeforeAndAfter getClone() {

        BeforeAndAfter clone = new BeforeAndAfter();

        clone.befores = new HashMap<>();
        for (Entry<String, Integer> entry : this.befores.entrySet()) {
            clone.befores.put(entry.getKey(), entry.getValue());
        }


        clone.afters = new HashMap<>();
        for (Entry<String, Integer> entry : this.afters.entrySet()) {
            clone.afters.put(entry.getKey(), entry.getValue());
        }


        return clone;

    }


}



