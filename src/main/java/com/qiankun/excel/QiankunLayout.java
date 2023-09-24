package com.qiankun.excel;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QiankunLayout extends QiankunExcel {

    List<QiankunExcel> rowData;
    List<QiankunExcel> colData;

    protected QiankunLayout(List<QiankunExcel> rowData, List<QiankunExcel> colData) {
        this.rowData = rowData;
        this.colData = colData;

        if(rowData != null) {
            List<Integer> allRowCols = rowData.stream().map(QiankunExcel::getCols).filter(Objects::nonNull).sorted().collect(Collectors.toList());
            if(! allRowCols.isEmpty()) {
                // 不校验导出的是行列一致
                // if(! allRowCols.get(0).equals(allRowCols.get(allRowCols.size() - 1))) {
                //     throw new RuntimeException("所有行，其列数不相等");
                // }
                cols = allRowCols.get(0);
                rowData.stream().forEach(mme -> mme.solve(null, allRowCols.get(0)));
            }
            if(rowData.stream().allMatch(mme -> mme.getRows() != null)) {
                rows = rowData.stream().mapToInt(QiankunExcel::getRows).sum();
            }
        }
        if(colData != null) {
            List<Integer> allColRows = colData.stream().map(QiankunExcel::getRows).filter(Objects::nonNull).sorted().collect(Collectors.toList());
            if(! allColRows.isEmpty()) {
                // 不校验导出的是行列一致
                // if(! allColRows.get(0).equals(allColRows.get(allColRows.size() - 1))) {
                //     throw new RuntimeException("所有列，其行数不相等");
                // }
                rows = allColRows.get(0);
                colData.stream().forEach(mme -> mme.solve(allColRows.get(0), null));
            }
            if(colData.stream().allMatch(mme -> mme.getCols() != null)) {
                cols = colData.stream().mapToInt(QiankunExcel::getCols).sum();
            }
        }
    }

    @Override
    protected void solve(Integer rows, Integer cols) {
        if(cols != null) {
            this.cols = cols;
            if(rowData != null) {
                rowData.forEach(mme -> mme.solve(rows, cols));
            }
            if(colData != null) {
                List<QiankunExcel> unknownCols = colData.stream().filter(mme -> mme.cols == null).collect(Collectors.toList());
                if(! unknownCols.isEmpty()) {
                    int remainCols = cols - colData.stream().map(QiankunExcel::getCols).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
                    if(remainCols < 1) {
                        throw new RuntimeException("确定列宽时遇到小于1的");
                    }
                    for (int i = 0; i < unknownCols.size(); i++) {
                        if (i < unknownCols.size() - 1) {
                            // 前面平均分配列
                            unknownCols.get(i).solve(rows, remainCols / unknownCols.size());
                        } else {
                            // 最后一个分剩下的列
                            unknownCols.get(i).solve(rows, remainCols / unknownCols.size() + remainCols % unknownCols.size());
                        }
                    }
                }
            }
        }
        if(rows != null) {
            this.rows = rows;
            if(colData != null) {
                colData.forEach(mme -> mme.solve(rows, cols));
            }
            if(rowData != null) {
                List<QiankunExcel> unknownRows = rowData.stream().filter(mme -> mme.rows == null).collect(Collectors.toList());
                if(! unknownRows.isEmpty()) {
                    int remainRows = rows - rowData.stream().map(QiankunExcel::getRows).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
                    if(remainRows < 1) {
                        throw new RuntimeException("确定行高时遇到小于1的");
                    }
                    for (int i = 0; i < unknownRows.size(); i++) {
                        if (i < unknownRows.size() - 1) {
                            // 前面平均分配行
                            unknownRows.get(i).solve(remainRows / unknownRows.size(), cols);
                        } else {
                            // 最后一个分剩下的行
                            unknownRows.get(i).solve(remainRows / unknownRows.size() + remainRows % unknownRows.size(), cols);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void solveStart() {
        int lastStartRow = startRow, lastStartCol = startCol;
        if(rowData != null) {
            for(QiankunExcel mme: rowData) {
                mme.startRow = lastStartRow;
                mme.startCol = lastStartCol;
                mme.solveStart();
                lastStartRow += mme.getRows();
            }
        }
        if(colData != null) {
            for(QiankunExcel mme: colData) {
                mme.startRow = lastStartRow;
                mme.startCol = lastStartCol;
                mme.solveStart();
                lastStartCol += mme.getCols();
            }
        }
    }

    @Override
    protected void travel(Consumer<QiankunCell> visitor) {
        if(rowData != null) {
            rowData.stream().forEach(e -> e.travel(visitor));
        }
        if(colData != null) {
            colData.stream().forEach(e -> e.travel(visitor));
        }
    }

    public static <T> QiankunExcel rows(Collection<T> data, Function<T, QiankunExcel> func) {
        return new QiankunLayout(data.stream().map(func).collect(Collectors.toList()), null);
    }

    // public static <T> MikuMikuExcel rows(Object object, String eval, Function<Function<String, String>, MikuMikuExcel> func) {
    //     return new MikuMikuLayout(dynamic(object, eval, func), null);
    // }

    public static QiankunExcel rows(QiankunExcel... rows) {
        return new QiankunLayout(Arrays.asList(rows), null);
    }

    public static QiankunExcel rows(List<QiankunExcel> rows) {
        return new QiankunLayout(rows, null);
    }

    public static <T> QiankunExcel cols(Collection<T> data, Function<T, QiankunExcel> func) {
        return new QiankunLayout(null, data.stream().map(func).collect(Collectors.toList()));
    }

    // public static <T> MikuMikuExcel cols(Object object, String eval, Function<Function<String, String>, MikuMikuExcel> func) {
    //     return new MikuMikuLayout(null, dynamic(object, eval, func));
    // }

    public static QiankunExcel cols(QiankunExcel... cols) {
        return new QiankunLayout(null, Arrays.asList(cols));
    }

    // private static List<MikuMikuExcel> dynamic(Object object, String eval, Function<Function<String, String>, MikuMikuExcel> func) {
    //     List<?> list = (List<?>) EvalAnalysisUtils.eval(eval, object);
    //     List<MikuMikuExcel> result = new ArrayList<>();
    //     for (int i = 0; i < list.size(); i++) {
    //         result.add(func.apply(MikuMikuExcelUtils.dynamicEval(eval, i)));
    //     }
    //     return result;
    // }
}
