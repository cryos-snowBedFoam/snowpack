package ProWin;  // no changes of Steiniger's version

import java.util.*;

/**
 * ArrayMap is a Map which combines the features of a HashMap
 * and an ArrayList
 *
 * @author  Manfred Steiniger
 */

public class ArrayMap extends HashMap
{

/** array is used to remain the ordering */
protected ArrayList array = new ArrayList();


  public ArrayMap()
  {
  }


  public Object put(Object key, Object value)
  {
    if (!super.containsKey(key))
    {
      array.add(key); // add new element
    }
    return super.put(key, value);
  }


  public Object remove(Object key)
  {
    array.remove(key);
    return super.remove(key);
  }


  public void clear()
  {
    array.clear();
    super.clear();
  }


  public Object getKey(int index)
  {
    return array.get(index);
  }


  /*
  public static void main(String[] args)
  {
    ArrayMap am = new ArrayMap();

    am.put("Winter", "weiss");
    am.put("Fruehling", "gruen");
    am.put("Sommer", "gelb");
    am.put("Herbst", "rot");

    for (int i=0; i<am.size(); i++)
    {
      String key = (String) am.getKey(i);
      System.out.println(key + " = " + am.get(key));
    }

    am.put("Fruehling", "rosa");
    am.remove("Sommer");
    am.put("Jahr", "schoen");

    for (int i=0; i<am.size(); i++)
    {
      String key = (String) am.getKey(i);
      System.out.println(key + " = " + am.get(key));
    }

    Util.waitForEnterKey();
  }
  */

}
