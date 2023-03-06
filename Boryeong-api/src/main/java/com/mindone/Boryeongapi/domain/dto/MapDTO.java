package com.mindone.Boryeongapi.domain.dto;

import java.util.ArrayList;
import java.util.List;

import com.mindone.Boryeongapi.utils.CamelUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections4.map.ListOrderedMap;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class MapDTO extends ListOrderedMap {
    private static final long serialVersionUID = 1181128341847522416L;

    /**
     * camelcase 형식으로 저장한다.
     */
    public Object put(Object key, Object value) {
        return super.put(CamelUtil.convert2CamelCase((String) key), value);
    }

    /**
     * camelcase 형식을 준수안할 경우
     *
     * @param key
     * @param value
     * @return
     */
    public Object putKey(Object key, Object value) {
        return super.put((String) key, value);
    }

    /**
     * object에 담겨있는 string 데이타를 추출한다.
     *
     * @param key
     * @return
     */
    public String getString(Object key) {
        Object obj = super.get(key);

        if (obj != null) {
            if (obj instanceof java.lang.String) {
                return (String) obj;
            } else if (obj instanceof java.math.BigDecimal) {

                return String.valueOf(((java.lang.Number) obj).intValue());
            } else if (obj instanceof java.lang.Integer) {
                return String.valueOf((Integer) obj);
            }
        }

        return "";
    }

    /**
     * object에 담겨있는 int 데이타를 추출한다.
     *
     * @param key
     * @return
     */
    public int getInt(Object key) {
        Object obj = super.get(key);

        if (obj != null) {
            if (obj instanceof java.math.BigDecimal) {
                return ((java.lang.Number) obj).intValue();
            } else if (obj instanceof java.lang.Integer) {
                return (Integer) obj;
            } else if (obj instanceof java.lang.Long) {
                return ((java.lang.Long) obj).intValue();
            } else if (obj instanceof java.lang.String) {
                return Integer.parseInt((String) obj);
            }
        }

        return 0;
    }

    /**
     * object에 담겨있는 double 데이타를 추출한다.     *
     * @param key
     * @return
     */
    public double getDouble(Object key) {
        Object obj = super.get(key);
        if (obj != null) {
            if (obj instanceof java.math.BigDecimal) {
                return ((java.lang.Number) obj).doubleValue();
            } else if (obj instanceof java.lang.Double) {
                return (Double) obj;
            } else if (obj instanceof java.lang.Long) {
                return ((java.lang.Long) obj).doubleValue();
            } else if (obj instanceof java.lang.String) {
                return Double.parseDouble((String) obj);
            }
        }

        return 0;
    }

    /**
     * 문자열배열형태를 List object 담아 전송한다.
     * @MethodName : getStringList
     * @Comment :
     * @return : List<String>
     */
    public List<String> getStringList(Object key) {
        Object obj = super.get(key);
        List<String> rList = new ArrayList<String>();

        if (obj != null) {
            if (obj instanceof java.lang.String[]) {
                String[] aValue = (String[]) obj;
                for (int i = 0; i < aValue.length; i++) {
                    rList.add(aValue[i]);
                }
            } else if (obj instanceof java.lang.String) {
                rList.add((String) obj);
            }
        }

        return rList;
    }
}
