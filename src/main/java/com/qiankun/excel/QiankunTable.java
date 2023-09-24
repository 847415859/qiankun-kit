package com.qiankun.excel;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.qiankun.common.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class QiankunTable extends QiankunExcel {

    private QiankunTable() {
    }

    /**
     * 简易的生成表格方法
     *
     * @param propertyNames 属性对应列名，推荐 {@link java.util.LinkedHashMap}
     * @param rows          表中各行
     * @return
     */
    public static QiankunExcel of(int headerCols, Map<String, String> propertyNames, List<?> rows) {
        return of(headerCols, propertyNames, rows, null,false);
    }

    /**
     * 简易的生成表格方法
     *
     * @param propertyNames 属性对应列名，推荐 {@link java.util.LinkedHashMap}
     * @param rows          表中各行
     * @param isHeader      是否渲染表头样式
     * @return
     */
    public static QiankunExcel of(int headerCols, Map<String, String> propertyNames, List<?> rows,Boolean isHeader) {
        return of(headerCols, propertyNames, rows, null,isHeader);
    }

    public static QiankunExcel of(int headerCols, Map<String, String> propertyNames, List<?> rows, Consumer<JSONObject> handler,Boolean isHeader) {
        if(Objects.isNull(rows)){
            rows = new ArrayList<>();
        }
        List<QiankunExcel> lines = new ArrayList<>(rows.size() + 1);
        lines.add(QiankunLayout.cols(propertyNames.values(), v -> QiankunCell.fixedRows(headerCols, v,isHeader)));
        for (int i = 0; i < rows.size(); i++) {
            Object data = rows.get(i);
            if (data == null) {
                lines.add(QiankunLayout.cols(propertyNames.keySet(), k -> QiankunCell.fixed(null)));
            } else {
                JSONObject obj = (JSONObject) JSON.toJSON(data);
                if (!obj.containsKey("lineNumber")) {
                    obj.put("lineNumber", i + 1);
                }
                if (handler != null) {
                    handler.accept(obj);
                }
                lines.add(QiankunLayout.cols(propertyNames.keySet(), k -> QiankunCell.fixed(cellValue(obj, k))));
            }
        }
        return QiankunLayout.rows(lines, Function.identity());
    }

    // public static MikuMikuExcel ofEval(int headerCols, Object object, String eval, Map<String, String> propertyNames) {
    //     return ofEval(headerCols, object, eval, propertyNames, null);
    // }
    //
    // public static MikuMikuExcel ofEval(int headerCols, Object object, String eval, Map<String, String> propertyNames, Consumer<JSONObject> handler) {
    //     return of(headerCols, propertyNames, (List<?>) EvalAnalysisUtils.eval(eval, object), handler);
    // }

    public static QiankunExcel empty(int rows, int cols) {
        return QiankunCell.fixed(rows, cols, null);
    }

    static Object cellValue(JSONObject obj, String key) {
        String[] keys = key.split(",");
        return Stream.of(keys).map(k -> obj.getString(k)).filter(StringUtils::isNotBlank).findFirst().orElse(null);
    }
}
