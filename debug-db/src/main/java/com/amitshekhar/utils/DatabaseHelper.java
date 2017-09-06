/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.amitshekhar.utils;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.amitshekhar.model.Response;
import com.amitshekhar.model.RowDataRequest;
import com.amitshekhar.model.TableDataResponse;
import com.amitshekhar.model.UpdateRowResponse;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

import static android.R.attr.start;
import static android.R.attr.value;

/**
 * Created by amitshekhar on 06/02/17.
 */

public class DatabaseHelper {

    static final String NULL = "null";


    private DatabaseHelper() {
    }

    public static Response getAllTableName(BoxStore boxStore) {
        Response response = new Response();
        for (Class aClass : boxStore.getAllEntityClasses()) {
            response.rows.add(aClass.getSimpleName());
        }

        response.isSuccessful = true;
        return response;
    }

    public static TableDataResponse getTableData(BoxStore boxStore, String tableName) {


        Log.e("App", "gettabledata " + tableName);
        List<Class> allEntityClasses = new ArrayList<>(boxStore.getAllEntityClasses());


        List<String> names = new ArrayList<>();
        for (Class allEntityClass : allEntityClasses) {
            names.add(allEntityClass.getSimpleName());
        }


        Log.e("App", " allEntityClasses " + allEntityClasses);
        Box<Object> box = boxStore.boxFor(allEntityClasses.get(names.indexOf(tableName)));

        //todo timber
        Log.e("App", " set box store " + boxStore);
        Log.e("App", " set box store " + box.count());
        Log.e("App", " set box store " + Arrays.toString(box.getEntityInfo().getAllProperties()));


        TableDataResponse tableData = new TableDataResponse();
        tableData.isSelectQuery = true;
//        tableName =

        tableData.tableInfos = getTableInfo(boxStore, names.indexOf(tableName));

        tableData.isEditable = tableData.tableInfos != null;


        // setting tableInfo when tableName is not known and making
        // it non-editable also by making isPrimary true for all
        if (tableData.tableInfos == null) {
            tableData.tableInfos = new ArrayList<>();


            for (Property property : box.getEntityInfo().getAllProperties()) {
                TableDataResponse.TableInfo tableInfo = new TableDataResponse.TableInfo();
                tableInfo.title = property.dbName;
                tableInfo.isPrimary = true;
                tableData.tableInfos.add(tableInfo);
            }
        }

        tableData.isSuccessful = true;
        tableData.rows = new ArrayList<>();

        for (Object o : box.getAll()) {
            long id = box.getId(o);
            Log.e("App", "id " + id);


            Log.e("App", "id " + box.get(id).toString());
            Log.e("App", "id " + box.get(id).getClass().getName());

            List<TableDataResponse.ColumnData> row = new ArrayList<>();
            Field[] fields = box.get(id).getClass().getDeclaredFields();


            List<String> propertyNames = new LinkedList<>();

            for (Property property : box.getEntityInfo().getAllProperties()) {
                propertyNames.add(property.dbName);
            }

            List<Field> sortedFields = new LinkedList<>();

            for (String propertyName : propertyNames) {
                for (Field field : fields) {
                    if (field.getName().equals(propertyName)) {
                        sortedFields.add(field);
                        break;
                    }
                }
            }

            for (Field field : sortedFields) {
                TableDataResponse.ColumnData columnData = new TableDataResponse.ColumnData();

                if (field.getName().equals("$change") || field.getName().equals("serialVersionUID")) {
                    continue;
                }
                field.setAccessible(true);

                if ((field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(long.class))) {
                    assingField(columnData, field, DataType.LONG, box, id);
                } else if (field.getType().isAssignableFrom(String.class)) {
                    assingField(columnData, field, DataType.TEXT, box, id);
                } else if (field.getType().isAssignableFrom(Date.class)) {

                    try {
                        Date date = (Date) field.get(box.get(id));

//                    assingField(columnData, field, DataType.TEXT, box, id);

                        columnData.dataType = DataType.TEXT;
                        columnData.value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);


                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }


                } else if (field.getType().isAssignableFrom(Float.class) || field.getType().isAssignableFrom(float.class)) {
                    assingField(columnData, field, DataType.FLOAT, box, id);
                } else if (field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(double.class)) {
                    assingField(columnData, field, DataType.REAL, box, id);
                } else if (field.getType().isAssignableFrom(Boolean.class) || field.getType().isAssignableFrom(boolean.class)) {
                    assingField(columnData, field, DataType.BOOLEAN, box, id);
                } else if (field.getType().isAssignableFrom(Integer.class) || field.getType().isAssignableFrom(int.class)) {
                    assingField(columnData, field, DataType.INTEGER, box, id);
                }

                Log.e("Column", "column data " + columnData);
                row.add(columnData);

            }


            Log.e("Rows", "rows: " + tableData.rows);
            tableData.rows.add(row);


        }

        return tableData;
    }

    static void assingField(TableDataResponse.ColumnData columnData, Field field, String type, Box<?> box, long id) {
        try {
            columnData.dataType = type;
            columnData.value = field.get(box.get(id));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private static String getQuotedTableName(String tableName) {
        return String.format("[%s]", tableName);
    }

    private static List<TableDataResponse.TableInfo> getTableInfo(BoxStore boxStore, int index) {
        List<Class> allEntityClasses = new ArrayList<>(boxStore.getAllEntityClasses());
        Log.e("App", " allEntityClasses " + allEntityClasses);
        Box<Object> box = boxStore.boxFor(allEntityClasses.get(index));

        Log.e("App", " set box store " + boxStore);
        Log.e("App", " set box store " + box.count());
        Log.e("App", " set box store " + Arrays.toString(box.getEntityInfo().getAllProperties()));

        List<TableDataResponse.TableInfo> tableInfoList = new ArrayList<>();

        for (Property property : box.getEntityInfo().getAllProperties()) {
            TableDataResponse.TableInfo tableInfo = new TableDataResponse.TableInfo();
            tableInfo.title = property.dbName;
            tableInfo.isPrimary = true;

            tableInfoList.add(tableInfo);
        }

        return tableInfoList;
    }


    public static UpdateRowResponse addRow(SQLiteDatabase db, String tableName,
                                           List<RowDataRequest> rowDataRequests) {
        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (rowDataRequests == null || tableName == null) {
            updateRowResponse.isSuccessful = false;
            return updateRowResponse;
        }

        tableName = getQuotedTableName(tableName);

        ContentValues contentValues = new ContentValues();

        for (RowDataRequest rowDataRequest : rowDataRequests) {
            if (NULL.equals(rowDataRequest.value)) {
                rowDataRequest.value = null;
            }

//            switch (rowDataRequest.dataType) {
//                case DataType.INTEGER:
//                    contentValues.put(rowDataRequest.title, Long.valueOf(rowDataRequest.value));
//                    break;
//                case DataType.REAL:
//                    contentValues.put(rowDataRequest.title, Double.valueOf(rowDataRequest.value));
//                    break;
//                case DataType.TEXT:
//                    contentValues.put(rowDataRequest.title, rowDataRequest.value);
//                    break;
//                default:
//                    contentValues.put(rowDataRequest.title, rowDataRequest.value);
//                    break;
//            }
        }

        long result = 0;// db.insert(tableName, null, contentValues);
        updateRowResponse.isSuccessful = result > 0;

        return updateRowResponse;

    }


    public static UpdateRowResponse updateRow(SQLiteDatabase db, String tableName, List<RowDataRequest> rowDataRequests) {

        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (rowDataRequests == null || tableName == null) {
            updateRowResponse.isSuccessful = false;
            return updateRowResponse;
        }

        tableName = getQuotedTableName(tableName);

        ContentValues contentValues = new ContentValues();

        String whereClause = null;
        List<String> whereArgsList = new ArrayList<>();

//        for (RowDataRequest rowDataRequest : rowDataRequests) {
//            if (NULL.equals(rowDataRequest.value)) {
//                rowDataRequest.value = null;
//            }
//            if (rowDataRequest.isPrimary) {
//                if (whereClause == null) {
//                    whereClause = rowDataRequest.title + "=? ";
//                } else {
//                    whereClause = whereClause + "and " + rowDataRequest.title + "=? ";
//                }
//                whereArgsList.add(rowDataRequest.value);
//            } else {
//                switch (rowDataRequest.dataType) {
//                    case DataType.INTEGER:
//                        contentValues.put(rowDataRequest.title, Long.valueOf(rowDataRequest.value));
//                        break;
//                    case DataType.REAL:
//                        contentValues.put(rowDataRequest.title, Double.valueOf(rowDataRequest.value));
//                        break;
//                    case DataType.TEXT:
//                        contentValues.put(rowDataRequest.title, rowDataRequest.value);
//                        break;
//                    default:
//                }
//            }
//        }

        String[] whereArgs = new String[whereArgsList.size()];

        for (int i = 0; i < whereArgsList.size(); i++) {
            whereArgs[i] = whereArgsList.get(i);
        }

//        db.update(tableName, contentValues, whereClause, whereArgs);
        updateRowResponse.isSuccessful = true;
        return updateRowResponse;
    }


    public static UpdateRowResponse deleteRow(BoxStore boxStore, String tableName,
                                              List<RowDataRequest> rowDataRequests) {

        UpdateRowResponse updateRowResponse = new UpdateRowResponse();

        if (rowDataRequests == null || tableName == null) {
            updateRowResponse.isSuccessful = false;
            return updateRowResponse;
        }

        String tempTableName = tableName;

        tableName = getQuotedTableName(tableName);


        //todo refactor
        String whereClause = null;
//        List<String> whereArgsList = new ArrayList<>();


        List<Pair<Property, RowDataRequest>> propertyValuePairs = new ArrayList<>();

        for (RowDataRequest rowDataRequest : rowDataRequests) {

            Log.e("App", "rowDataRequest " + rowDataRequest);
            if (NULL.equals(rowDataRequest.value)) {
                rowDataRequest.value = null;
            }
            if (rowDataRequest.isPrimary) {
                if (whereClause == null) {
                    whereClause = rowDataRequest.title + "=? ";
                } else {
                    whereClause = whereClause + "and " + rowDataRequest.title + "=? ";
                }

//                whereArgsList.add(rowDataRequest.value);
            }
        }

//        if (whereArgsList.size() == 0) {
//            updateRowResponse.isSuccessful = true;
//            return updateRowResponse;
//        }
//
//        String[] whereArgs = new String[whereArgsList.size()];
//
//        for (int i = 0; i < whereArgsList.size(); i++) {
//            whereArgs[i] = whereArgsList.get(i);
//        }


        Class<?> tableClass = null;//todo handle diffrent names of tables dbName vs name


        for (Class aClass : boxStore.getAllEntityClasses()) {

            if (aClass.getSimpleName().equals(tempTableName)) {
                tableClass = aClass;
                break;
            }
        }


        if (tableClass != null) {
            Box<?> box = boxStore.boxFor(tableClass);
            QueryBuilder<?> query = box.query();

            for (Property property : box.getEntityInfo().getAllProperties()) {
                for (RowDataRequest pair : rowDataRequests) {
                    Log.e("Property", "dbName " + property.dbName + " " + pair.title);
                    if (property.dbName.equals(pair.title)) {
                        propertyValuePairs.add(new Pair<>(property, pair));
                        break;
                    }
                }
            }

            //todo reflection utils

            boolean started = false;
            try {
                for (Pair<Property, RowDataRequest> propertyValuePair : propertyValuePairs) {
                    //need kotlin here...
//                propertyValuePair.first.
//                Log.e("App", "property type " + propertyValuePair.first.customType);
                    Log.e("App", "pair  " + propertyValuePair.first.type + " value: " + propertyValuePair.second.title + " " + propertyValuePair.second.value);
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(Long.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(long.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(Integer.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(int.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(double.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(Double.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(Float.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(float.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(Date.class));
                    Log.e("App", "pair  " + propertyValuePair.first.type.isAssignableFrom(String.class));


                    if (propertyValuePair.first.type.isAssignableFrom(Date.class)) {
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(propertyValuePair.second.value.toString());

                            if (!started) {
                                query.equal(propertyValuePair.first, date);
                                started = true;
                            } else {
                                query.and().equal(propertyValuePair.first, date);
                            }

                            Log.e("App", "added date  " + date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else if (propertyValuePair.first.type.isAssignableFrom(Double.class) || propertyValuePair.first.type.isAssignableFrom(double.class)
                            || propertyValuePair.first.type.isAssignableFrom(Float.class) || propertyValuePair.first.type.isAssignableFrom(float.class)) {
                        double value = Double.parseDouble(propertyValuePair.second.value.toString());

                        if (!started) {
                            query.and().between(propertyValuePair.first, value, value);
                            started = true;
                        } else {
                            query.between(propertyValuePair.first, value, value);
                        }
                        Log.e("App", "added double  " + value);

                    } else if (propertyValuePair.first.type.isAssignableFrom(Long.class) || propertyValuePair.first.type.isAssignableFrom(long.class)
                            || propertyValuePair.first.type.isAssignableFrom(Integer.class) || propertyValuePair.first.type.isAssignableFrom(int.class)) {
                        Log.e("App", "added long  " + propertyValuePair.second.value.toString());
                        try {
                            long value = Long.parseLong(propertyValuePair.second.value.toString());

                            if (!started) {
                                query.equal(propertyValuePair.first, value);

                                started = true;
                            } else {
                                query.and().equal(propertyValuePair.first, value);

                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }


                    } else if (propertyValuePair.first.type.isAssignableFrom(Boolean.class) || propertyValuePair.first.type.isAssignableFrom(boolean.class)) {
                        boolean value = Boolean.parseBoolean(propertyValuePair.second.value.toString());

                        Log.e("App", "added boolean  " + value);

                        if (!started) {
                            query.equal(propertyValuePair.first, value);
                            started = true;
                        } else {
                            query.and().equal(propertyValuePair.first, value);

                        }

                    } else if (propertyValuePair.first.type.isAssignableFrom(String.class)) {
                        String value = (propertyValuePair.second.value.toString());


                        if (!started) {
                            query.equal(propertyValuePair.first, value);
                            started = true;
                        } else {
                            query.and().equal(propertyValuePair.first, value);

                        }

                        Log.e("App", "added string  " + value);
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }


            Query<?> build = query.build();
            Log.e("App", "delete count " + query.toString());
            Log.e("App", "delete count " + build.count());
            build.remove();
        }


        Log.e("App", "delete " + tableName + " " + whereClause); ///+ " " + Arrays.toString(whereArgs));
//        Log.e("App", "pairs: " + propertyNamesValuePairs);
        Log.e("App", "pairs: " + propertyValuePairs);
//        db.delete(tableName, whereClause, whereArgs);
        updateRowResponse.isSuccessful = true;
        return updateRowResponse;
    }


    public static TableDataResponse exec(SQLiteDatabase database, String sql) {
        TableDataResponse tableDataResponse = new TableDataResponse();
        tableDataResponse.isSelectQuery = false;
        try {

            String tableName = getTableName(sql);

            if (!TextUtils.isEmpty(tableName)) {
                String quotedTableName = getQuotedTableName(tableName);
                sql = sql.replace(tableName, quotedTableName);
            }

            database.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
            tableDataResponse.isSuccessful = false;
            tableDataResponse.errorMessage = e.getMessage();
            return tableDataResponse;
        }
        tableDataResponse.isSuccessful = true;
        return tableDataResponse;
    }

    private static String getTableName(String selectQuery) {
        // TODO: 24/4/17 Handle JOIN Query
        TableNameParser tableNameParser = new TableNameParser(selectQuery);
        HashSet<String> tableNames = (HashSet<String>) tableNameParser.tables();

        for (String tableName : tableNames) {
            if (!TextUtils.isEmpty(tableName)) {
                return tableName;
            }
        }

        return null;
    }

}
