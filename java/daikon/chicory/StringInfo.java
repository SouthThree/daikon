package daikon.chicory;

import java.util.*;


/**
 * The StringInfo class is a subtype of DaikonVariableInfo used for variable types which are
 * strings.
 */
public class StringInfo extends DaikonVariableInfo
{
    public StringInfo(String theName, boolean arr)
    {
        super(theName, arr);
    }

    public Object getMyValFromParentVal(Object value)
    {
        return value;
    }


    /**
     * Returns a String which contains a string representation of val, used for
     * dtrace information.
     */
    public String getDTraceValueString(Object val)
    {
        if(isArray)
        {
            return getStringList((List)val);
        }
        else
        {
            return getValueStringNonArr(val);
        }
    }

    /**
     * Returns a space-separated String of the elements in theValues.  If theValues
     * is null, returns "null." If theValues is nonsensical, returns "nonsensical".
     * @param theValues A list of values, each is a String
     * @return a space-separated String of the elements in theValues
     */
    public static String getStringList(List<String> theValues)
    {
        StringBuffer buf = new StringBuffer();

        if (theValues == null)
        {
            //buf.append("null");
            return "null" + DaikonWriter.lineSep + "1";
        }

        if (theValues instanceof NonsensicalList)
        {
            //buf.append("nonsensical");
            return "nonsensical" + DaikonWriter.lineSep + "2";
        }


        buf.append("[");
        for (Iterator iter = theValues.iterator(); iter.hasNext();)
        {
            String str = (String) iter.next();

            if (str == null)
                buf.append(str);
            else
                buf.append("\"" + encodeString(str) + "\"");

            //put space between elements in array
            if (iter.hasNext())
                buf.append(" ");
        }
        buf.append("]");

        if (theValues instanceof NonsensicalList)
            buf.append(DaikonWriter.lineSep + "2");
        else
            buf.append(DaikonWriter.lineSep + "1");

        return buf.toString();
    }


    /**
     * Similar to showStringList, but used for non-array objects.
     */
    public String getValueStringNonArr(Object val)
    {
        String retString;

        if (val == null)
            retString = ("null" + DaikonWriter.lineSep);
        else if (val instanceof NonsensicalObject)
            retString = ("nonsensical" + DaikonWriter.lineSep);
        else
        {
            retString = getString((String) val);
            retString += DaikonWriter.lineSep;
        }

        if (val instanceof NonsensicalObject)
            retString += ("2");
        else
            retString += ("1");

        return retString;
    }

    //encodes a string: surrounds in quotes and removes line breaks
    private String getString(String stringRef)
    {
        return ("\"" + encodeString(stringRef) + "\"");
    }

    //removes endlines in string
    private static String encodeString(String input)
    {
        return Runtime.quote(input);
    }
}
