package edu.util.fileprocess;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Map;

class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {

super.setColumnMapping(new String[FieldUtils.getAllFields(bean.getClass()).length]);
        final int numColumns = findMaxFieldIndex();
        if (!isAnnotationDriven() || numColumns == -1) {
            // Return an empty header if the annotations are not driven or no fields are found.
            return new String[0];
        }

        String[] header = new String[numColumns + 1];

        BeanField<T,Integer> beanField;
        for (int i = 0; i <= numColumns; i++) {
            beanField = findField(i);
            String columnHeaderName = extractHeaderName(beanField);
            header[i] = columnHeaderName;
        }
        return header;
    }



    private String extractHeaderName(final BeanField<T,Integer> beanField) {
        if (beanField == null || beanField.getField() == null
                || beanField.getField().getDeclaredAnnotationsByType(CsvBindByName.class).length == 0) {
            return StringUtils.EMPTY;
        }

        final CsvBindByName bindByNameAnnotation = beanField.getField()
                .getDeclaredAnnotationsByType(CsvBindByName.class)[0];
        return bindByNameAnnotation.column();
    }

    private int findMaxFieldIndex() {
        int maxIndex = -1;
        Map<Integer, BeanField<T, Integer>> fieldMap = (Map<Integer, BeanField<T, Integer>>) getFieldMap();
        for (int i = 0; i < fieldMap.size(); i++) {
            BeanField<T, Integer> beanField = fieldMap.get(i);
            if (beanField != null && beanField.getField() != null) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}